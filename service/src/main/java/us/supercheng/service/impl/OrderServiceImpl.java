package us.supercheng.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import us.supercheng.bo.ShopcartItemBO;
import us.supercheng.bo.SubmitOrderBO;
import us.supercheng.enums.OrderStatusEnum;
import us.supercheng.enums.YesOrNo;
import us.supercheng.mapper.OrderItemsMapper;
import us.supercheng.mapper.OrderStatusMapper;
import us.supercheng.mapper.OrdersMapper;
import us.supercheng.pojo.*;
import us.supercheng.service.AddressService;
import us.supercheng.service.ItemsService;
import us.supercheng.service.OrderService;
import us.supercheng.utils.*;
import us.supercheng.vo.MerchantOrdersVO;
import us.supercheng.vo.OrderVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    //String payReturnUrl = "http://api.z.mukewang.com/foodie-dev-api/orders/notifyMerchantOrderPaid";
    String payReturnUrl = "http://jd2ka6.natappfree.cc/orders/notifyMerchantOrderPaid";

    @Autowired
    private Sid sid;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    @Override
    public OrderVO createOrder(SubmitOrderBO submitOrderBO, String url, HttpServletRequest req, HttpServletResponse resp) {
        int totalAmt = 0,
            actualAmt = 1,
            postAmt = 0;

        Orders order = new Orders();
        List<String> list = new ArrayList<>();
        String[] specIds = submitOrderBO.getItemSpecIds().split(",");
        Date now = new Date();
        String orderId = this.sid.nextShort(),
               userId = submitOrderBO.getUserId(),
               redisKey = "shopping_cart:" + userId,
               jsonStr = this.redisOperator.get(redisKey);

        if (StringUtils.isBlank(jsonStr)) {
            CookieUtils.deleteCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY);
            throw new RuntimeException("Cannot create order due to redis shopping cart is missing");
        }

        if (StringUtils.isBlank(submitOrderBO.getToken())) {
            throw new RuntimeException("Cannot create order due to create order token is missing");
        }

        try {
            RLock lock = this.redisson.getLock("LOCK_ORDER_" + submitOrderBO.getToken());
            if (!lock.tryLock(0, 3, TimeUnit.SECONDS))
                return null;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot create order due to lock token exception: " + ex.getMessage());
        }

        List<ShopcartItemBO> itemBOS = JsonUtils.jsonToList(jsonStr, ShopcartItemBO.class);

        order.setId(orderId);
        order.setPayMethod(submitOrderBO.getPayMethod());
        order.setUserId(userId);

        if (!StringUtils.isBlank(submitOrderBO.getLeftMsg()))
            order.setLeftMsg(submitOrderBO.getLeftMsg());

        UserAddress address = this.addressService.getUserAddressByUserIdAndAddressId(userId, submitOrderBO.getAddressId());

        if (!StringUtils.isBlank(address.getProvince()))
            list.add(address.getProvince());

        if (!StringUtils.isBlank(address.getCity()))
            list.add(address.getCity());

        if (!StringUtils.isBlank(address.getDistrict()))
            list.add(address.getDistrict());

        if (!StringUtils.isBlank(address.getDetail()))
            list.add(address.getDetail());

        StringBuilder sb = new StringBuilder();
        for (int i=0, len=list.size(); i<len; i++)
            if (i == len - 1)
                sb.append(list.get(i));
            else
                sb.append(list.get(i) + ", ");

        if (sb.length() > 0)
            order.setReceiverAddress(sb.toString());

        if (!StringUtils.isBlank(address.getReceiver()))
            order.setReceiverName(address.getReceiver());

        if (!StringUtils.isBlank(address.getMobile()))
            order.setReceiverMobile(address.getMobile());

        OrderStatus orderStatus = new OrderStatus();
        List<ShopcartItemBO> delList = new ArrayList<>();

        // money
        order.setTotalAmount(0);
        order.setRealPayAmount(actualAmt);
        // order.setRealPayAmount(0);
        order.setPostAmount(postAmt);

        order.setIsComment(YesOrNo.No.type);
        order.setIsDelete(YesOrNo.No.type);

        order.setCreatedTime(now);
        order.setUpdatedTime(now);

        this.ordersMapper.insert(order);

        for (String specId : specIds) {
            OrderItems orderItems = new OrderItems();

            ItemsSpec itemsSpec = this.itemsService.getItemsSpecByItemsSpecId(specId);

            if (itemsSpec != null) {
                Items items = this.itemsService.getItemsByItemsId(itemsSpec.getItemId());

                if (items != null) {
                    totalAmt += itemsSpec.getPriceNormal();
                    actualAmt += itemsSpec.getPriceDiscount();
                    orderItems.setBuyCounts(this.getShoppingCartItemCount(specId, itemBOS, delList));
                    orderItems.setId(this.sid.nextShort());
                    orderItems.setItemId(itemsSpec.getItemId());
                    ItemsImg img = this.itemsService.getItemsImgMainByItemId(items.getId());
                    if (img != null)
                        orderItems.setItemImg(img.getUrl());
                    orderItems.setItemName(items.getItemName());
                    orderItems.setItemSpecId(specId);
                    orderItems.setItemSpecName(itemsSpec.getName());
                    orderItems.setOrderId(orderId);
                    orderItems.setPrice(itemsSpec.getPriceDiscount());

                    this.orderItemsMapper.insert(orderItems);
                    Map<String, Object> map = new HashMap<>();
                    map.put("specId", specId);
                    map.put("amt", 1);
                    this.itemsService.updateItemsSpecCount(map);
                }
            }
        }

        orderStatus.setCreatedTime(now);
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);

        this.orderStatusMapper.insert(orderStatus);

        order.setTotalAmount(totalAmt);
        order.setUserId(null);
        this.ordersMapper.updateByPrimaryKeySelective(order);

        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setAmount(order.getRealPayAmount());
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setPayMethod(submitOrderBO.getPayMethod());
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        OrderVO ret = new OrderVO();
        ret.setOrders(order);
        ret.setMerchantOrdersVO(merchantOrdersVO);
        itemBOS.removeAll(delList);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");
        System.out.println(ret.getMerchantOrdersVO());
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(ret.getMerchantOrdersVO(), headers);
        ResponseEntity<APIResponse> respEntity = restTemplate.postForEntity(url, entity, APIResponse.class);

        APIResponse apiResp = respEntity.getBody();
        if (apiResp == null || apiResp.getStatus() != 200)
            throw new RuntimeException("Interval Payment error...... from " + url + "\n" +apiResp.getMsg());

        jsonStr = JsonUtils.objectToJson(itemBOS);
        this.redisOperator.set(redisKey, jsonStr);
        CookieUtils.setCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY, jsonStr, true);

        return ret;
    }

    @Transactional
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus os = new OrderStatus();
        os.setOrderId(orderId);
        os.setOrderStatus(orderStatus);
        os.setPayTime(new Date());
        this.orderStatusMapper.updateByPrimaryKeySelective(os);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus getOrderStatusByOrderId(String id) {
        return this.orderStatusMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void closeExpiredOrders() {
        Date now = new Date();
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> orderStatusList = this.orderStatusMapper.select(orderStatus);
        for (OrderStatus os : orderStatusList) {
            if (DateUtil.daysBetween(os.getCreatedTime(), now) >= 1) {
                os.setCloseTime(now);
                os.setOrderStatus(OrderStatusEnum.CLOSE.type);
                this.orderStatusMapper.updateByPrimaryKeySelective(os);
            }
        }
    }

    private int getShoppingCartItemCount(String itemSpecId, List<ShopcartItemBO> list, List<ShopcartItemBO> delList) {
        for (ShopcartItemBO each : list)
            if (each.getSpecId().equalsIgnoreCase(itemSpecId)) {
                delList.add(each);
                return each.getBuyCounts();
            }

        return 0;
    }
}
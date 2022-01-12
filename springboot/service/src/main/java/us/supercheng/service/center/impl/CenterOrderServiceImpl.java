package us.supercheng.service.center.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import us.supercheng.bo.center.OrderItemsCommentBO;
import us.supercheng.enums.OrderStatusEnum;
import us.supercheng.enums.YesOrNo;
import us.supercheng.mapper.*;
import us.supercheng.pojo.OrderItems;
import us.supercheng.pojo.OrderStatus;
import us.supercheng.pojo.Orders;
import us.supercheng.service.center.CenterOrderService;
import us.supercheng.utils.PagedResult;
import us.supercheng.vo.MyCommentVO;
import us.supercheng.vo.MyOrdersVO;
import us.supercheng.vo.OrderStatusCountsVO;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CenterOrderServiceImpl implements CenterOrderService {

    @Autowired
    private Sid sid;

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult query(String userId, Integer orderStatus, Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();

        map.put("userId", userId);

        if (orderStatus != null)
            map.put("orderStatus", orderStatus);

        PageHelper.startPage(pageNum, pageSize);
        List<MyOrdersVO> myOrdersVOs = this.ordersMapperCustom.query(map);
        PageInfo<?> pageInfo = new PageInfo<>(myOrdersVOs);

        return new PagedResult(pageNum, pageInfo.getPages(), pageInfo.getTotal(), myOrdersVOs);
    }

    @Transactional
    @Override
    public boolean updateOrderStatus(String orderId, Integer fromOrderStatus, Integer toOrderStatus) {
        OrderStatus os = new OrderStatus();
        os.setOrderStatus(toOrderStatus);
        os.setDeliverTime(new Date());

        if (toOrderStatus == OrderStatusEnum.SUCCESS.type)
            os.setSuccessTime(new Date());

        Example exp = new Example(OrderStatus.class);
        Example.Criteria criteria = exp.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", fromOrderStatus);

        return orderStatusMapper.updateByExampleSelective(os, exp) > 0;
    }

    @Transactional
    @Override
    public boolean markOrderDelete(String orderId, String userId) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setUserId(userId);
        order.setIsDelete(YesOrNo.Yes.type);
        order.setUpdatedTime(new Date());
        return this.ordersMapper.updateByPrimaryKeySelective(order) > 0;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders getOrdersByUserIdAndOrderId(String orderId, String userId) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setUserId(userId);
        order.setIsDelete(YesOrNo.No.type);
        return this.ordersMapper.selectOne(order);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> getOrderItemsByOrderId(String orderId) {
        OrderItems orderItems = new OrderItems();
        orderItems.setOrderId(orderId);
        return this.orderItemsMapper.select(orderItems);
    }

    @Transactional
    @Override
    public void insertItemsComments(String userId, List<OrderItemsCommentBO> orderItemsCommentBOs) {
        for (OrderItemsCommentBO o : orderItemsCommentBOs)
            o.setCommentId(sid.nextShort());

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("comments", orderItemsCommentBOs);

        this.itemsCommentsMapperCustom.saveUserCommentList(map);
    }

    @Transactional
    @Override
    public void markOrderCommented(String orderId) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setUpdatedTime(new Date());
        order.setIsComment(YesOrNo.Yes.type);
        this.ordersMapper.updateByPrimaryKeySelective(order);
    }

    @Transactional
    @Override
    public void updateOrderStatusCommentTime(String orderId) {
        OrderStatus os = new OrderStatus();
        os.setOrderId(orderId);
        os.setCommentTime(new Date());
        this.orderStatusMapper.updateByPrimaryKeySelective(os);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryUserComments(String userId, Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(pageNum, pageSize);
        List<MyCommentVO> list = this.itemsCommentsMapperCustom.queryUserComments(map);
        PageInfo<?> pageInfo = new PageInfo<>(list);

        return new PagedResult(pageNum, pageInfo.getPages(), pageInfo.getTotal(), list);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVO getOrderStatusCountSummary(String userId) {
        OrderStatusCountsVO ret = new OrderStatusCountsVO();
        Map<String, Object> map = new HashMap<>();

        map.put("userId", userId);
        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);
        ret.setWaitPayCounts(this.ordersMapperCustom.getOrderStatusCount(map));

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        ret.setWaitDeliverCounts(this.ordersMapperCustom.getOrderStatusCount(map));

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        ret.setWaitReceiveCounts(this.ordersMapperCustom.getOrderStatusCount(map));


        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        map.put("isCommented", YesOrNo.No.type);
        ret.setWaitCommentCounts(this.ordersMapperCustom.getOrderStatusCount(map));

        return ret;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getTrend(String userId, Integer pageNum, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(pageNum, pageSize);
        List<OrderStatus> list = this.ordersMapperCustom.getTrend(map);
        PageInfo<?> pageInfo = new PageInfo<>(list);

        return new PagedResult(pageNum, pageInfo.getPages(), pageInfo.getTotal(), list);
    }
}

package us.supercheng.api.controller.center;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.api.controller.BaseController;
import us.supercheng.bo.center.OrderItemsCommentBO;
import us.supercheng.enums.YesOrNo;
import us.supercheng.pojo.Orders;
import us.supercheng.service.OrderService;
import us.supercheng.service.center.CenterOrderService;
import us.supercheng.utils.APIResponse;
import java.util.List;

@RestController
@RequestMapping("mycomments")

public class CenterCommentController extends BaseController {

    @Autowired
    private CenterOrderService centerOrderService;

    @Autowired
    private OrderService orderService;

    @PostMapping("pending")
    public APIResponse getMyComments(@RequestParam String userId, @RequestParam String orderId) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field Order ID and/or User ID");

        Orders order = this.centerOrderService.getOrdersByUserIdAndOrderId(orderId, userId);

        if (order == null)
            return APIResponse.errorMsg("Order ID doest not associate with User ID or this order is already deleted");

        if (order.getIsComment() != YesOrNo.No.type)
            return APIResponse.errorMsg("This order has already been commented");

        return APIResponse.ok(this.centerOrderService.getOrderItemsByOrderId(orderId));
    }

    @PostMapping("saveList")
    public APIResponse saveList(@RequestParam String userId, @RequestParam String orderId, @RequestBody List<OrderItemsCommentBO> orderItemList) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field Order ID and/or User ID");

        Orders order = this.centerOrderService.getOrdersByUserIdAndOrderId(orderId, userId);

        if (order == null)
            return APIResponse.errorMsg("Order ID doest not associate with User ID or this order is already deleted");

        if (orderItemList == null || orderItemList.isEmpty())
            return APIResponse.errorMsg("Make sure send in a valid OrderItem List");

        this.centerOrderService.insertItemsComments(userId, orderItemList);
        this.centerOrderService.markOrderCommented(orderId);
        this.centerOrderService.updateOrderStatusCommentTime(orderId);

        return APIResponse.ok();
    }

    @PostMapping("query")
    public APIResponse query(@RequestParam String userId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field User ID");

        if (page == null)
            page = DEFAULT_PAGE;

        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;

        return APIResponse.ok(this.centerOrderService.queryUserComments(userId, page, pageSize));
    }
}
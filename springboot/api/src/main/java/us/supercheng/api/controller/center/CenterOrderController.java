package us.supercheng.api.controller.center;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.api.controller.BaseController;
import us.supercheng.enums.OrderStatusEnum;
import us.supercheng.pojo.Orders;
import us.supercheng.service.center.CenterOrderService;
import us.supercheng.utils.APIResponse;

@RestController
@RequestMapping("myorders")
public class CenterOrderController extends BaseController {

    @Autowired
    private CenterOrderService centerOrderService;

    @PostMapping("query")
    public APIResponse query(@RequestParam String userId, @RequestParam(required = false, name = "orderStatus") Integer orderStatus,
                             @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field User ID");

        if (page == null)
            page = DEFAULT_PAGE;

        if (pageSize == null)
            pageSize = DEFAULT_PAGE_SIZE;

        return APIResponse.ok(this.centerOrderService.query(userId, orderStatus, page, pageSize));
    }

    @GetMapping("shipout")
    public APIResponse shipout(@RequestParam String orderId) {
        if (StringUtils.isBlank(orderId))
            return APIResponse.errorMsg("Missing required field Order ID");

        boolean res = this.centerOrderService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_DELIVER.type, OrderStatusEnum.WAIT_RECEIVE.type);

        if (res)
            return APIResponse.ok();

        return APIResponse.errorMsg("Failed to mark Order as shipped");
    }

    @PostMapping("confirmReceive")
    public APIResponse confirmReceive(@RequestParam String orderId, @RequestParam String userId) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field Order ID and/or User ID");

        Orders order = this.centerOrderService.getOrdersByUserIdAndOrderId(orderId, userId);

        if (order == null)
            return APIResponse.errorMsg("Order ID doest not associate with User ID or this order is already deleted");

        boolean res = this.centerOrderService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_RECEIVE.type, OrderStatusEnum.SUCCESS.type);
        if (res)
            return APIResponse.ok();

        return APIResponse.errorMsg("Failed to mark Order as received");
    }

    @PostMapping("delete")
    public APIResponse deleteOrder(@RequestParam String orderId, @RequestParam String userId) {
        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field Order ID and/or User ID");

        Orders order = this.centerOrderService.getOrdersByUserIdAndOrderId(orderId, userId);

        if (order == null)
            return APIResponse.errorMsg("Order ID doest not associate with User ID or this order is already deleted");

        boolean res = this.centerOrderService.markOrderDelete(orderId, userId);
        if (res)
            return APIResponse.ok();

        return APIResponse.errorMsg("Failed to delete Order");
    }

    @PostMapping("statusCounts")
    public APIResponse statusCounts(@RequestParam String userId) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field User ID");

        return APIResponse.ok(this.centerOrderService.getOrderStatusCountSummary(userId));
    }

    @PostMapping("trend")
    public APIResponse getTrend(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field User ID");

        if (page == null || page < 1)
            page = DEFAULT_PAGE;

        if (pageSize == null || pageSize < 1)
            pageSize = DEFAULT_PAGE_SIZE;

        return APIResponse.ok(this.centerOrderService.getTrend(userId, page, pageSize));
    }
}

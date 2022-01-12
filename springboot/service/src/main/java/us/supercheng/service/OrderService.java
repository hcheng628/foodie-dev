package us.supercheng.service;

import us.supercheng.bo.SubmitOrderBO;
import us.supercheng.pojo.OrderStatus;
import us.supercheng.vo.OrderVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface OrderService {
    OrderVO createOrder(SubmitOrderBO submitOrderBO, String url, HttpServletRequest req, HttpServletResponse resp);
    void updateOrderStatus(String orderId, Integer orderStatus);
    OrderStatus getOrderStatusByOrderId(String id);
    void closeExpiredOrders();
}
package us.supercheng.service;

import us.supercheng.bo.ShopcartItemBO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ShoppingCartService {
    void addToCart(String userId, ShopcartItemBO shopcartItem, HttpServletRequest req, HttpServletResponse resp);
    void delCartItemBySpecId(String userId, String itemSpecId, HttpServletRequest req, HttpServletResponse resp);
}

package us.supercheng.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.supercheng.bo.ShopcartItemBO;
import us.supercheng.service.ShoppingCartService;
import us.supercheng.utils.CookieUtils;
import us.supercheng.utils.JsonUtils;
import us.supercheng.utils.RedisOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private RedisOperator redisOperator;

    @Transactional
    @Override
    public void addToCart(String userId, ShopcartItemBO shopcartItem, HttpServletRequest req, HttpServletResponse resp) {
        String key = "shopping_cart:" + userId,
                shoppingCart = this.redisOperator.get(key),
                itemId = shopcartItem.getSpecId(),
                jsonStr = null;
        List<ShopcartItemBO> list = null;

        if (StringUtils.isBlank(shoppingCart)) {
            list = new ArrayList<>();
            list.add(shopcartItem);
        } else {
            list = JsonUtils.jsonToList(shoppingCart, ShopcartItemBO.class);
            boolean duplicate = false;
            for (ShopcartItemBO bo : list) {
                if (bo.getSpecId().equalsIgnoreCase(itemId)) {
                    bo.setBuyCounts(bo.getBuyCounts() + shopcartItem.getBuyCounts());
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate) {
                list.add(shopcartItem);
            }
        }

        jsonStr = JsonUtils.objectToJson(list);
        this.redisOperator.set(key, jsonStr);
        CookieUtils.setCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY, jsonStr, true);
    }

    @Transactional
    @Override
    public void delCartItemBySpecId(String userId, String itemSpecId, HttpServletRequest req, HttpServletResponse resp) {
        String key = "shopping_cart:" + userId,
                shoppingCart = this.redisOperator.get(key),
                jsonStr = null;
        List<ShopcartItemBO> list = null;

        if (StringUtils.isBlank(shoppingCart)) {
            CookieUtils.deleteCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY);
            throw new RuntimeException("Could not find shopping cart in redis and could not remove item from shopping cart.");
        } else {
            int delIdx = -1;
            list = JsonUtils.jsonToList(shoppingCart, ShopcartItemBO.class);

            for (int i=list.size()-1; i>-1 && delIdx == -1; i--)
                if (list.get(i).getSpecId().equalsIgnoreCase(itemSpecId))
                    delIdx = i;

            list.remove(delIdx);
        }

        jsonStr = JsonUtils.objectToJson(list);
        this.redisOperator.set(key, jsonStr);
        CookieUtils.setCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY, jsonStr, true);
    }
}

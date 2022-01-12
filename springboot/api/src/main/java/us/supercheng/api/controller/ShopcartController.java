package us.supercheng.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import us.supercheng.bo.ShopcartItemBO;
import us.supercheng.service.ShoppingCartService;
import us.supercheng.utils.APIResponse;
import us.supercheng.utils.RedisOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("shopcart")
@Controller
public class ShopcartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private RedisOperator redisOperator;

    @PostMapping("add")
    @ResponseBody
    public APIResponse addShopcart(@RequestParam String userId,
                                      @RequestBody ShopcartItemBO shopcartItem,
                                      HttpServletRequest req,
                                      HttpServletResponse resp) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required User ID");

        if (shopcartItem == null)
            return APIResponse.errorMsg("Missing required Shopping cart item");

        this.shoppingCartService.addToCart(userId, shopcartItem, req, resp);

        return APIResponse.ok();
    }

    @PostMapping("del")
    @ResponseBody
    public APIResponse delShopcartItemBySpecId(@RequestParam String userId,
                                               @RequestParam String itemSpecId,
                                               HttpServletRequest req,
                                               HttpServletResponse resp) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId))
            return APIResponse.errorMsg("Missing required fields User ID and/or Item Spec ID");

        this.shoppingCartService.delCartItemBySpecId(userId, itemSpecId, req, resp);

        return APIResponse.ok();
    }
}
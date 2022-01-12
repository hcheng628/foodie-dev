package us.supercheng.api.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.supercheng.bo.UserBO;
import us.supercheng.service.UsersService;
import us.supercheng.utils.APIResponse;
import us.supercheng.utils.CookieUtils;
import us.supercheng.vo.UsersVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UsersService usersService;


    @GetMapping("/isUsernameExist")
    public Object isUsernameExist(@RequestParam String username) {
        if (StringUtils.isBlank(username))
            return APIResponse.errorMsg("Empty username");

        if (this.usersService.isUsernameExist(username))
            return APIResponse.errorMsg("This username is already taken.");

        return APIResponse.ok();
    }

    @PostMapping("regist")
    public APIResponse regist(@RequestBody UserBO userBO, HttpServletRequest req, HttpServletResponse resp) {
        String username = userBO.getUsername(),
                pwd = userBO.getPassword(),
                pwdConfirm = userBO.getConfirmPassword();

        if (StringUtils.isBlank(username))
            return APIResponse.errorMsg("Empty username");

        if (StringUtils.isBlank(pwd))
            return APIResponse.errorMsg("Empty password");

        if (pwd.length() < 6)
            return APIResponse.errorMsg("Password is too short");

        if (!pwd.equals(pwdConfirm))
            return APIResponse.errorMsg("Password does not match");

        if (this.usersService.isUsernameExist(username))
            return APIResponse.errorMsg("This username is already taken.");

        UsersVO user = this.usersService.createUser(userBO, req, resp);
        this.usersService.syncShoppingCart(user.getId(), req, resp);
        return APIResponse.ok();
    }

    @PostMapping("login")
    public APIResponse login(@RequestBody UserBO userBO, HttpServletRequest req, HttpServletResponse resp) {
        if (StringUtils.isBlank(userBO.getUsername()))
            return APIResponse.errorMsg("Empty username");

        if (StringUtils.isBlank(userBO.getPassword()))
            return APIResponse.errorMsg("Empty password");

        UsersVO user = this.usersService.login(userBO, req, resp);

        if (user == null)
            return APIResponse.errorMsg("Incorrect Username or Password");

        this.usersService.syncShoppingCart(user.getId(), req, resp);
        return APIResponse.ok(user);
    }

    @PostMapping("logout")
    public APIResponse logout(HttpServletRequest req, HttpServletResponse resp, String userId) {
        CookieUtils.deleteCookie(req, resp, CookieUtils.USER_COOKIE_KEY);
        CookieUtils.deleteCookie(req, resp, CookieUtils.SHOPCART_COOKIE_KEY);
        if (StringUtils.isNotBlank(userId))
            this.usersService.delUserSession(userId);
        return APIResponse.ok();
    }
}
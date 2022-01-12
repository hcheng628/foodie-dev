package us.supercheng.api.controller.center;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.supercheng.pojo.Users;
import us.supercheng.service.center.CenterUsersService;
import us.supercheng.utils.APIResponse;
import us.supercheng.utils.CookieUtils;
import us.supercheng.utils.JsonUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("center")

public class CenterController {

    @Autowired
    private CenterUsersService centerUsersService;

    @GetMapping("userInfo")
    public APIResponse getUserInfo(@RequestParam String userId, HttpServletRequest req, HttpServletResponse resp) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field userId");

        Users user = this.centerUsersService.getUserByUserId(userId);

        if (user != null)
            this.setUsersObjCookie(user, req, resp, false);

        return APIResponse.ok(user);
    }

    private void setUsersObjCookie(Users user, HttpServletRequest req, HttpServletResponse resp, boolean setCookie) {
        user.setPassword(null);
        if (setCookie)
            CookieUtils.setCookie(req, resp, CookieUtils.USER_COOKIE_KEY, JsonUtils.objectToJson(user), true);
    }
}

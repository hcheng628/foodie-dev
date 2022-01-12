package us.supercheng.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import us.supercheng.pojo.Users;
import us.supercheng.service.UsersService;
import us.supercheng.sso.entity.AuthInfo;
import us.supercheng.sso.service.AuthService;
import us.supercheng.utils.APIResponse;
import us.supercheng.utils.JsonUtils;
import us.supercheng.utils.MD5Utils;
import us.supercheng.utils.RedisOperator;
import us.supercheng.vo.UsersVO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final String USER_CAS_SESSION_KEY = "user_cas_session:";
    private final String USER_CAS_TKT_KEY = "user_cas_tkt:";
    private final String USER_CAS_TEMP_TKT_KEY = "user_cas_tmp_tkt:";
    private final String USER_COOKIE_TKT = "user_cookie_tkt";

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private UsersService usersService;

    @Override
    public boolean checkCASSSO(HttpServletRequest req) {
        String tkt = this.getCookie(req, USER_COOKIE_TKT);

        if (StringUtils.isBlank(tkt))
            return false;

        String userId = this.redisOperator.get(USER_CAS_TKT_KEY + tkt);
        if (StringUtils.isBlank(userId))
            return false;

        return StringUtils.isNotBlank(this.redisOperator.get(USER_CAS_SESSION_KEY + userId));
    }

    @Transactional
    @Override
    public AuthInfo createCASSession(String username, String password, String retUrl, Model model, HttpServletRequest req, HttpServletResponse resp) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            model.addAttribute("errMsg", "Username and/or Password is empty.");
            return null;
        }

        Users user = this.usersService.validateLogin(username, password);

        if (user == null) {
            model.addAttribute("errMsg", "Username and/or Password does not match or does not exist.");
            return null;
        }

        // Create CAS Session
        UsersVO usersVO = this.createCasUserSession(user);
        String redisCASUUID = usersVO.getUserUniqueToken(),
               userId = user.getId();

        // Create CAS TKT
        String tkt = this.createTKT(resp, userId);
        usersVO.setUserSessionTkt(tkt);
        this.redisOperator.set(USER_CAS_SESSION_KEY + user.getId(), JsonUtils.objectToJson(usersVO));

        // Create CAS Temp TKT
        String tempTkt = this.createTempTKT();

        //System.out.println("redisCASUUID: " + redisCASUUID + "TKT: " + tkt + "Temp TKT: " + tempTkt);
        return new AuthInfo(tkt, tempTkt);
    }

    @Transactional
    @Override
    public APIResponse verifyTempTkt(String tempTkt, String tkt, HttpServletRequest req) {
        String key = USER_CAS_TEMP_TKT_KEY + tempTkt,
               tktVal = this.redisOperator.get(key);

        if (StringUtils.isBlank(tktVal) || StringUtils.isBlank(tkt))
            return APIResponse.errorMsg("TKT and/or Temp TKT is empty.");

        try {
            if (tktVal.equals(MD5Utils.getMD5Str(tempTkt))) {
                this.redisOperator.del(key);
                // System.out.println("REDIS TEMP TKT Deleted!!!");
            } else
                return APIResponse.errorMsg("Temp TKT Verification Failed 2");
        } catch (Exception ex) {}

        String userId = this.redisOperator.get(USER_CAS_TKT_KEY + tkt);

        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Temp TKT Verification Failed 3 : TKT key: " + (USER_CAS_TKT_KEY + tkt));

        String userStr = this.redisOperator.get(USER_CAS_SESSION_KEY + userId);

        // System.out.println(userStr);
        if (StringUtils.isBlank(userStr))
            return APIResponse.errorMsg("Temp TKT Verification Failed 4");

        return APIResponse.ok(JsonUtils.jsonToPojo(userStr, UsersVO.class));
    }

    @Transactional
    @Override
    public void logout(String userId, String tkt, HttpServletRequest req, HttpServletResponse resp) {
        this.remCookie(resp, USER_COOKIE_TKT);

        if (StringUtils.isNotBlank(tkt))
            this.redisOperator.del(USER_CAS_TKT_KEY + tkt);

        if (StringUtils.isNotBlank(userId))
            this.redisOperator.del(USER_CAS_SESSION_KEY + userId);
    }

    /**
     * User ID - UserVO object kay-val pair
     *
     * @param user
     * @return
     */
    private UsersVO createCasUserSession(Users user) {
        String redisCASUUID = UUID.randomUUID().toString().trim();
        this.usersService.removeUsersPII(user);
        return this.usersService.convertUsersToUsersVOWithToken(user, redisCASUUID);
    }


    private String createTKT(HttpServletResponse resp, String userId) {
        String redisCASTKTUUID = UUID.randomUUID().toString().trim();
        this.redisOperator.set(USER_CAS_TKT_KEY + redisCASTKTUUID, userId);
        this.setCookie(resp, USER_COOKIE_TKT, redisCASTKTUUID);
        return redisCASTKTUUID;
    }

    public String createTempTKT() {
        String tempTkt = UUID.randomUUID().toString().trim();
        try {
            this.redisOperator.set(USER_CAS_TEMP_TKT_KEY + tempTkt, MD5Utils.getMD5Str(tempTkt), 600);
        } catch (Exception ex){}
        return tempTkt;
    }

    void setCookie(HttpServletResponse resp, String key, String val) {
        Cookie cookie = new Cookie(key, val);
        cookie.setPath("/");
        cookie.setDomain("sso.com");
        cookie.setMaxAge(-1);
        resp.addCookie(cookie);
    }

    void remCookie(HttpServletResponse resp, String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath("/");
        cookie.setDomain("sso.com");
        cookie.setMaxAge(-1);
        resp.addCookie(cookie);
    }

    String getCookie(HttpServletRequest req, String key) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null)
            for (Cookie c :cookies)
                if (c.getName().equals(key))
                    return c.getValue();

        return null;
    }
}

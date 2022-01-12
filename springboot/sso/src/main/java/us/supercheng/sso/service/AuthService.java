package us.supercheng.sso.service;

import org.springframework.ui.Model;
import us.supercheng.sso.entity.AuthInfo;
import us.supercheng.utils.APIResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    boolean checkCASSSO(HttpServletRequest req);
    String createTempTKT();
    AuthInfo createCASSession(String username, String password, String retUrl, Model model, HttpServletRequest req, HttpServletResponse resp);
    APIResponse verifyTempTkt(String tempTkt, String tkt, HttpServletRequest req);
    void logout(String userId, String tkt, HttpServletRequest req, HttpServletResponse resp);
}

package us.supercheng.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.sso.entity.AuthInfo;
import us.supercheng.sso.service.impl.AuthServiceImpl;
import us.supercheng.utils.APIResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class SSOAuthController {

    @Autowired
    private AuthServiceImpl authService;

    @RequestMapping("login")
    public String login(@RequestParam("retUrl") String retUrl,
                        Model model,
                        HttpServletRequest req) {
        model.addAttribute("retUrl", retUrl);

        if (this.authService.checkCASSSO(req)) {
            String tmpTkt = this.authService.createTempTKT();
            return "redirect:" + retUrl + "?tempTkt=" + tmpTkt;
        }

        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("retUrl") String retUrl,
                          Model model,
                          HttpServletRequest req,
                          HttpServletResponse resp) {
        AuthInfo authInfo = this.authService.createCASSession(username, password, retUrl, model, req, resp);

        if (authInfo == null)
            return "login";

        return "redirect:" + retUrl + "?tempTkt=" + authInfo.getTempTkt() + "&tkt=" + authInfo.getTkt();
    }

    @PostMapping("/verifyTempTkt")
    @ResponseBody
    public APIResponse verifyTempTkt(String tempTkt,
                                     String tkt,
                                     HttpServletRequest req) {

        // System.out.println("verifyTempTkt tempTkt: " + tempTkt + " tkt: " + tkt);
        return this.authService.verifyTempTkt(tempTkt, tkt, req);
    }

    @PostMapping("/logout")
    @ResponseBody
    public APIResponse logout(@RequestParam String userId,
                              @RequestParam String tkt,
                              HttpServletRequest req,
                              HttpServletResponse resp) {
        this.authService.logout(userId, tkt, req, resp);
        return APIResponse.ok();
    }
}

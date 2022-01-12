package us.supercheng.api.controller.center;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.nio.ch.IOUtil;
import us.supercheng.api.resource.FileUpload;
import us.supercheng.bo.center.CenterUserBO;
import us.supercheng.pojo.Users;
import us.supercheng.service.center.CenterUsersService;
import us.supercheng.utils.APIResponse;
import us.supercheng.utils.CookieUtils;
import us.supercheng.utils.DateUtil;
import us.supercheng.utils.JsonUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("userInfo")

public class CenterUserController {

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private CenterUsersService centerUsersService;

    @PostMapping("update")
    public APIResponse update(@RequestParam String userId,
                         @RequestBody @Valid CenterUserBO userBO,
                         BindingResult bindingResult,
                         HttpServletRequest req, HttpServletResponse resp) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field userId");

        if (bindingResult.hasErrors()) {
            return APIResponse.errorMap(this.resToMap(bindingResult));
        }

        Users ret = this.centerUsersService.update(userId, userBO);
        this.setNullProperty(ret);
        CookieUtils.setCookie(req, resp, CookieUtils.USER_COOKIE_KEY, JsonUtils.objectToJson(ret), true);
        return APIResponse.ok(ret);
    }

    @PostMapping(value = "uploadAvatar")
    public APIResponse uploadAvatar(@RequestParam String userId,
                                    @RequestParam("file") MultipartFile file) {
        if (StringUtils.isBlank(userId))
            return APIResponse.errorMsg("Missing required field userId");

        if (file == null)
            return APIResponse.errorMsg("Missing required avatar image file");


        String contentType = file.getContentType();
        if (StringUtils.isBlank(contentType))
            contentType = "image/png";


        if (contentType.indexOf("image/") != 0)
            return APIResponse.errorMsg("Please upload a image file");

        String fileExtenstion = contentType.substring(6);
        if (StringUtils.isBlank(fileExtenstion))
            fileExtenstion = "png";

        if (!fileExtenstion.equalsIgnoreCase("png") &&
                !fileExtenstion.equalsIgnoreCase("jpg") &&
                !fileExtenstion.equalsIgnoreCase("jpeg"))
            return APIResponse.errorMsg(fileExtenstion + " format is not supported, please upload png, jpg, or jpeg format");

        String fileFullName = "avatar-" + userId + "." + fileExtenstion,
               imgPath = this.fileUpload.getUserAvatarLocation() + File.separator + fileFullName;

        File imgFile = new File(imgPath);
        if (imgFile.getParentFile() != null)
            imgFile.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(imgFile)) {
            IOUtils.copy(file.getInputStream(), fos);

        } catch (IOException ex) {
            return APIResponse.errorMsg("Internal Server File Error");
        }

        this.centerUsersService.updateAvatarByUserId(userId, this.fileUpload.getImgServerUrl() + fileFullName + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN));
        return APIResponse.ok("File Uploaded");
    }

    private void setNullProperty(Users user) {
        user.setPassword(null);
        user.setMobile(null);
        user.setEmail(null);
        user.setCreatedTime(null);
        user.setUpdatedTime(null);
        user.setBirthday(null);
    }

    private Map<String, String> resToMap(BindingResult result) {
        Map<String, String> ret = new HashMap<>();

        for (FieldError e : result.getFieldErrors())
            ret.put(e.getField(), e.getDefaultMessage());

        return ret;
    }
}

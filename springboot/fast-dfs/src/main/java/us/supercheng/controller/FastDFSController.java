package us.supercheng.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.supercheng.config.FastDFSConfigs;
import us.supercheng.service.center.CenterUsersService;
import us.supercheng.services.FastDFSService;
import us.supercheng.utils.APIResponse;

import java.util.Date;

@RestController
@RequestMapping("fast-dfs")
public class FastDFSController {

    @Autowired
    private FastDFSConfigs fastDFSConfigs;

    @Autowired
    private FastDFSService fastDFSService;

    @Autowired
    private CenterUsersService centerUsersService;

    @GetMapping("hello")
    public APIResponse hello() {
        return APIResponse.ok(new Date());
    }

    @PostMapping("uploadAvatar")
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

        try {
            String fileFullName = this.fastDFSService.uploadFile(file, fileExtenstion);
            this.centerUsersService.updateAvatarByUserId(userId, this.fastDFSConfigs.getHost() + "/" + fileFullName);
        } catch (Exception ex) {
            return APIResponse.errorMsg("Internal Server File Upload Error");
        }

        return APIResponse.ok("File Uploaded");
    }
}

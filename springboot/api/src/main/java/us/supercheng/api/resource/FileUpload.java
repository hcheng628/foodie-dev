package us.supercheng.api.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:app.properties")
@ConfigurationProperties(prefix = "file")

public class FileUpload {

    private String userAvatarLocation;
    private String imgServerUrl;

    public String getUserAvatarLocation() {
        return userAvatarLocation;
    }

    public void setUserAvatarLocation(String userAvatarLocation) {
        this.userAvatarLocation = userAvatarLocation;
    }

    public String getImgServerUrl() {
        return imgServerUrl;
    }

    public void setImgServerUrl(String imgServerUrl) {
        this.imgServerUrl = imgServerUrl;
    }
}

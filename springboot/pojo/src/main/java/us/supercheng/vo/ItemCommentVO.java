package us.supercheng.vo;

public class ItemCommentVO {
    private String nickname;
    private String userFace;
    private String specName;
    private String content;
    private String createdTime;

    public ItemCommentVO(String nickname, String userFace, String specName, String content, String createdTime) {
        this.nickname = nickname;
        this.userFace = userFace;
        this.specName = specName;
        this.content = content;
        this.createdTime = createdTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
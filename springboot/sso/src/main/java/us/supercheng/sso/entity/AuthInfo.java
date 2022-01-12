package us.supercheng.sso.entity;

public class AuthInfo {
    private String tkt;
    private String tempTkt;

    public AuthInfo(){}

    public AuthInfo(String tkt, String tempTkt) {
        this.tkt = tkt;
        this.tempTkt = tempTkt;
    }

    public String getTkt() {
        return tkt;
    }

    public void setTkt(String tkt) {
        this.tkt = tkt;
    }

    public String getTempTkt() {
        return tempTkt;
    }

    public void setTempTkt(String tempTkt) {
        this.tempTkt = tempTkt;
    }
}

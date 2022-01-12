package us.supercheng.enums;

public enum PaymentType {

    WechatPay(1, "Wechat Pay"),
    Alipay(2, "Alipay");

    public Integer type;
    public String value;

    PaymentType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
package us.supercheng.enums;

public enum YesOrNo {

    Yes(1, "Yes"),
    No(0, "No");

    public Integer type;
    public String value;


    YesOrNo (Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
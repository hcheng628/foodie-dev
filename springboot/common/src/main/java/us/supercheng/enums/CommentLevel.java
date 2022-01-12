package us.supercheng.enums;

public enum CommentLevel {

    Good(1, "Good"),
    Normal(2, "Normal"),
    Bad(3, "Bad");

    public Integer type;
    public String value;


    CommentLevel(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
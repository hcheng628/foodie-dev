package us.supercheng.enums;

public enum Sex {

    Felmale(0, "Female"),
    Male(1, "Male"),
    Secret(2, "Secret");

    public Integer type;
    public String value;


    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
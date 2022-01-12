package us.supercheng.es.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "foodie-items", type = "doc", createIndex = false)
public class Item {

    @Id
    private Integer sellCounts;

    @Field
    private String itemId;

    @Field
//    @JsonProperty("updated_time")
    private String updatedTime;

    @Field
    private String imgUrl;

    @Field
    private Integer price;

    @Field
    private String itemName;

    public Integer getSellCounts() {
        return sellCounts;
    }

    public void setSellCounts(Integer sellCounts) {
        this.sellCounts = sellCounts;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "Items{" +
                "sellCounts=" + sellCounts +
                ", itemId='" + itemId + '\'' +
                ", updatedTime='" + updatedTime + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", price=" + price +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
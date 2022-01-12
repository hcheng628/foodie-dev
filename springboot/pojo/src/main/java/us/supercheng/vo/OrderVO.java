package us.supercheng.vo;

import us.supercheng.pojo.Orders;

public class OrderVO {
    private Orders orders;
    private MerchantOrdersVO merchantOrdersVO;

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
}
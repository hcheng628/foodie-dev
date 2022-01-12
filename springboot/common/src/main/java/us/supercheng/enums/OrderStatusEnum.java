package us.supercheng.enums;

/**
 * @Description: 订单状态 枚举
 */
public enum OrderStatusEnum {

	WAIT_PAY(10, "Waiting payment"),
	WAIT_DELIVER(20, "Paid, but not shipped"),
	WAIT_RECEIVE(30, "Paid and shipped"),
	SUCCESS(40, "Transaction Success"),
	CLOSE(50, "Transaction Complete");

	public final Integer type;
	public final String value;

	OrderStatusEnum(Integer type, String value){
		this.type = type;
		this.value = value;
	}

}

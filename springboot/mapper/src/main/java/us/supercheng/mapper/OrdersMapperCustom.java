package us.supercheng.mapper;

import org.apache.ibatis.annotations.Param;
import us.supercheng.pojo.OrderStatus;
import us.supercheng.vo.MyOrdersVO;
import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {
    List<MyOrdersVO> query(@Param(("paraMap")) Map<String, Object> map);
    int getOrderStatusCount(@Param(("paraMap")) Map<String, Object> map);
    List<OrderStatus> getTrend(@Param(("paraMap")) Map<String, Object> map);
}
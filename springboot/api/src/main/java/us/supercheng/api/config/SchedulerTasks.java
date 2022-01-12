package us.supercheng.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.supercheng.service.OrderService;

@Component
public class SchedulerTasks {

    @Autowired
    private OrderService orderService;

    @Scheduled(cron = "0/3 * * * * ? ")
    public void closeOrders() {
        //this.orderService.closeExpiredOrders();
    }
}
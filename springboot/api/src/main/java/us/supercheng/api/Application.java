package us.supercheng.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = {"us.supercheng", "org.n3r.idworker"})
@MapperScan(basePackages = "us.supercheng.mapper")
@EnableScheduling

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
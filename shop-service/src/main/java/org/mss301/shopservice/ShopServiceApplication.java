package org.mss301.shopservice;

import org.mss301.commonservice.config.DotEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "org.mss301.shopservice",
        "org.mss301.commonservice"
})
public class ShopServiceApplication {

    public static void main(String[] args) {
        DotEnvConfig.loadEnv();
        SpringApplication.run(ShopServiceApplication.class, args);
    }

}

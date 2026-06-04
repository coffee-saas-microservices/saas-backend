package org.mss301.employeeservice;

import org.mss301.commonservice.config.DotEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(
        basePackages = {
                "org.mss301.employeeservice",
                "org.mss301.commonservice",
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = SpringBootApplication.class
        ),
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class EmployeeServiceApplication {

    public static void main(String[] args) {
        DotEnvConfig.loadEnv();
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }

}

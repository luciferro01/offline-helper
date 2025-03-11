package com.mohil_bansal.sellerservice.seller_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@EnableFeignClients
public class SellerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SellerServiceApplication.class, args);
    }

}

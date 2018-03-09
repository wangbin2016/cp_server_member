package com.caipiao.server.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class CpServerMemberApplication {
	public static void main(String[] args) {
		SpringApplication.run(CpServerMemberApplication.class, args);
	}
}

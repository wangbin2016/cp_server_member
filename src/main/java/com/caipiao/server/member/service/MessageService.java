package com.caipiao.server.member.service;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient("message-service")
public interface MessageService {
}

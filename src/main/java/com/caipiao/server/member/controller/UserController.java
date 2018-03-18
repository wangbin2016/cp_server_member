package com.caipiao.server.member.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.caipiao.member.entity.Member;
import com.caipiao.member.service.MemberService;
import com.caipiao.server.member.entity.Regist;
import com.caipiao.server.member.entity.Response;
import com.caipiao.server.member.entity.ResponseStatus;
import com.caipiao.server.member.service.MessageService;
import com.caipiao.server.member.service.MessageService.SmsSendRequest;
import com.google.gson.JsonObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user/")
public class UserController {
	private MemberService memberService;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MessageService messageService;

	@RequestMapping(value = { "regist/sms" })
	public ResponseEntity<SmsSendResponse> sms(String mobile) {
		Random random = new Random();
		int next = random.nextInt(10000000);
		String code = "" + (10000000 - next);
		ResponseEntity<SmsSendResponse> response = doSend(mobile, code);
		return response;
	}

	@RequestMapping(value = { "regist" })
	public ResponseEntity<Response<Regist>> regist(String account, String password, String mobile, String nickName,
			String email) {
		Response<Regist> responset = null;
		try {
			Member member = new Member();
			member.setAccount(account);
			member.setCreateTime(new Date());
			member.setEmail(email);
			member.setMobile(mobile);
			member.setNickname(nickName);
			member.setPassword(password);
			member.setStatus(1);
			memberService.regist(member);
			Regist regist = new Regist();
			regist.setId("5564");
			responset = new Response<Regist>(ResponseStatus.OK, regist);
		} catch (Exception e) {
			responset = new Response<Regist>(ResponseStatus.FAIR, null);
			e.printStackTrace();
		}
		ResponseEntity<Response<Regist>> response = new ResponseEntity<Response<Regist>>(responset, HttpStatus.OK);
		return response;
	}
	
	public static void main(String[] args) {
		Response<Regist> responset = new Response<Regist>(ResponseStatus.FAIR, null);
		ResponseEntity<Response<Regist>> response = new ResponseEntity<Response<Regist>>(responset, HttpStatus.OK);
		
	}

	public ResponseEntity<SmsSendResponse> doSend(String mobile, String code) {
		final String sendUrl = "http://message-service/message/sms/send";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("mobile", mobile);
		map.add("templateId", "CHECK_CODE");
		map.add("params['code']", code);
		log.info("发送参数：{}", map);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<SmsSendResponse> response = restTemplate.postForEntity(sendUrl, request, SmsSendResponse.class);
		return response;
	}

	@RequestMapping(value = { "regist/sms2" })
	public ResponseEntity<SmsSendResponse> sms2(String mobile) {
		Random random = new Random();
		int next = random.nextInt(10000000);
		String code = "" + (10000000 - next);
		ResponseEntity<SmsSendResponse> response = doSendFeign(mobile, code);
		return response;
	}

	public ResponseEntity<SmsSendResponse> doSendFeign(String mobile, String code) {
		SmsSendRequest request = new SmsSendRequest();
		request.setMobile(mobile);
		request.setTemplateId("CHECK_CODE");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		request.setParams(params);
		return messageService.send(request);
	}

	@Data
	public static class SmsSendResponse {
		/**
		 * 返回消息
		 */
		private String message;
		/**
		 * 返回状态码
		 */
		private String code;
	}

}

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

import com.caipiao.common.data.RequestData;
import com.caipiao.common.data.ResponseData;
import com.caipiao.common.data.responsestatus.CommonResponseStatus;
import com.caipiao.member.entity.Member;
import com.caipiao.member.service.MemberService;
import com.caipiao.server.member.body.request.RegistRequest;
import com.caipiao.server.member.body.response.MemberInfoResponse;
import com.caipiao.server.member.body.response.RegistReponse;
import com.caipiao.server.member.service.MessageService;
import com.caipiao.server.member.service.MessageService.SmsSendRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@RestController
@RequestMapping("/user/")
public class UserController {
	@Autowired
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
	
	@RequestMapping(value = { "info" })
	public ResponseEntity<ResponseData<MemberInfoResponse>> regist(String account) {
		ResponseData<MemberInfoResponse> responset = null;
		try {
			Member member = memberService.getMember(account, null);
			MemberInfoResponse memberInfo = new MemberInfoResponse();
			memberInfo.setAccount(account);
			memberInfo.setMobile(member.getMobile());
			memberInfo.setNickname(member.getNickname());
			responset = new ResponseData<MemberInfoResponse>(CommonResponseStatus.SUCCESS,memberInfo);
		} catch (Exception e) {
			responset = new ResponseData<MemberInfoResponse>(CommonResponseStatus.EXCEPTION);
			e.printStackTrace();
		}
		ResponseEntity<ResponseData<MemberInfoResponse>> response = new ResponseEntity<ResponseData<MemberInfoResponse>>(responset,HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = { "regist" })
	public ResponseEntity<ResponseData<RegistReponse>> regist(RequestData<RegistRequest> req) {
		ResponseData<RegistReponse> responset = null;
		try {
			RegistRequest regist = req.getBody();
			Member member = new Member();
			member.setAccount(regist.getAccount());
			member.setCreateTime(new Date());
			member.setEmail(regist.getEmail());
			member.setMobile(regist.getMobile());
			member.setNickname(regist.getNickName());
			member.setPassword(regist.getPassword());
			member.setStatus(1);
			memberService.regist(member);
			RegistReponse responsetBody = new RegistReponse();
			responsetBody.setId("5564");
			responset = new ResponseData<RegistReponse>(CommonResponseStatus.SUCCESS, responsetBody);
		} catch (Exception e) {
			responset = new ResponseData<RegistReponse>(CommonResponseStatus.EXCEPTION);
			e.printStackTrace();
		}
		ResponseEntity<ResponseData<RegistReponse>> response = new ResponseEntity<ResponseData<RegistReponse>>(responset, HttpStatus.OK);
		return response;
	}
	
	public static void main(String[] args) {
		ResponseData<RegistReponse> responset = new ResponseData<RegistReponse>(CommonResponseStatus.SUCCESS);
		ResponseEntity<ResponseData<RegistReponse>> response = new ResponseEntity<ResponseData<RegistReponse>>(responset, HttpStatus.OK);
		System.out.println(response.getBody());
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

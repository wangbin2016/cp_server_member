package com.caipiao.server.member.controller;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.caipiao.common.data.ResponseData;
import com.caipiao.common.data.body.DataBody;
import com.caipiao.common.data.responsestatus.CommonResponseStatus;
import com.caipiao.common.data.responsestatus.MemberResponseStatus;
import com.caipiao.common.redis.RedisUtils;
import com.caipiao.common.utils.MD5;
import com.caipiao.common.utils.constant.MemberConstant;
import com.caipiao.member.entity.Member;
import com.caipiao.member.service.MemberService;
import com.caipiao.server.member.body.request.CheckLoginParam;
import com.caipiao.server.member.body.request.LoginParam;
import com.caipiao.server.member.body.request.RegistParam;
import com.caipiao.server.member.body.response.LoginReponse;
import com.caipiao.server.member.body.response.MemberInfoResponse;
import com.caipiao.server.member.body.response.RegistReponse;
import com.caipiao.server.member.service.MessageService;

import lombok.Data;

@Data
@RestController
@RequestMapping("/member/")
public class MemberController<T> {
	@Autowired
	private MemberService memberService;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MessageService messageService;

	@RequestMapping(value = { "info" })
	public ResponseEntity<ResponseData<MemberInfoResponse>> regist(CheckLoginParam param) {
		ResponseData<MemberInfoResponse> responset = null;
		try {
			String token = param.getToken();
			String account = param.getAccount();
			MemberInfoResponse memberInfo = new MemberInfoResponse();
			Member member = RedisUtils.getSession(token,MemberConstant.MEMBER);
			if(member == null || account==null || !account.equals(member.getAccount())) {				
				responset = new ResponseData<MemberInfoResponse>(MemberResponseStatus.LOGIN_INFO_ERROR, memberInfo);
			}else {
				memberInfo.setAccount(account);
				memberInfo.setMobile(member.getMobile());
				memberInfo.setNickname(member.getNickname());
				memberInfo.setEmail(member.getEmail());
				memberInfo.setAddress("ss");
				responset = new ResponseData<MemberInfoResponse>(CommonResponseStatus.SUCCESS, memberInfo);
			}
		} catch (Exception e) {
			responset = new ResponseData<MemberInfoResponse>(CommonResponseStatus.EXCEPTION);
			e.printStackTrace();
		}
		ResponseEntity<ResponseData<MemberInfoResponse>> response = new ResponseEntity<ResponseData<MemberInfoResponse>>(
				responset, HttpStatus.OK);
		return response;
	}

	/**
	 * 登录接口{login}
	 * @param param
	 * @return
	 */
	@RequestMapping(value = { "login" })
	public ResponseEntity<ResponseData<DataBody>> login(LoginParam param) {
		ResponseData<DataBody> responset = null;
		Member member = memberService.getMember(param.getAccount(), param.getPassword());
		if (member == null) {
			responset = new ResponseData<DataBody>(MemberResponseStatus.LOGIN_ERROR, null);
		} else if(member.getStatus() == 0){
			responset = new ResponseData<DataBody>(MemberResponseStatus.MEMBER_LOCK_ERROR, null);
		}else {
			LoginReponse responsetBody = new LoginReponse();
			responsetBody.setAccount(member.getAccount());
			responsetBody.setToken(createToken(member));
			responset = new ResponseData<DataBody>(CommonResponseStatus.SUCCESS, responsetBody);
			RedisUtils.setSession(responsetBody.getToken(), MemberConstant.MEMBER, member);
		}
		return getResponseContent(responset);
	}
	
	public String createToken(Member member) {
		String msj = member.toString()+System.currentTimeMillis();
		String token = MD5.sign(msj);
		return token;
	}

	public  ResponseEntity<ResponseData<DataBody>> getResponseContent(ResponseData<DataBody> responseData) {
		ResponseEntity<ResponseData<DataBody>> response = new ResponseEntity<ResponseData<DataBody>>(responseData, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = { "regist" })
	public ResponseEntity<ResponseData<RegistReponse>> regist(RegistParam regist) {
		ResponseData<RegistReponse> responset = null;
		try {
			Member member = new Member();
			member.setAccount(regist.getAccount());
			member.setCreateTime(new Date());
			member.setEmail(regist.getEmail());
			member.setMobile(regist.getMobile());
			member.setNickname(regist.getNikename());
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
		ResponseEntity<ResponseData<RegistReponse>> response = new ResponseEntity<ResponseData<RegistReponse>>(
				responset, HttpStatus.OK);
		return response;
	}

	public static void main(String[] args) {
		ResponseData<RegistReponse> responset = new ResponseData<RegistReponse>(CommonResponseStatus.SUCCESS);
		ResponseEntity<ResponseData<RegistReponse>> response = new ResponseEntity<ResponseData<RegistReponse>>(
				responset, HttpStatus.OK);
		System.out.println(response.getBody());
	}

	/*public ResponseEntity<SmsSendResponse> doSend(String mobile, String code) {
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
	}*/
}

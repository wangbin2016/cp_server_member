package com.caipiao.server.member.body.request;

import lombok.Data;

@Data
public class CheckLoginParam {
	private String account;
	private String token;
}

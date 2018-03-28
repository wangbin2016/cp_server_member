package com.caipiao.server.member.body.request;


import lombok.Data;

@Data
public class MemberInfoParam{
	private String account;
	private String token;
}

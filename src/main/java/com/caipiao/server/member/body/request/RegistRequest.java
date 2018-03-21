package com.caipiao.server.member.body.request;

import com.caipiao.common.data.body.DataBody;

import lombok.Data;

@Data
public class RegistRequest implements DataBody{
	private String account;
	private String password; 
	private String mobile; 
	private String nickName;
	private String email;
}

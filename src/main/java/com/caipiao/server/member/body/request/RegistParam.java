package com.caipiao.server.member.body.request;

import lombok.Data;

@Data
public class RegistParam {
	private String account;
	private String password; 
	private String mobile; 
	private String nikename;
	private String email;
}

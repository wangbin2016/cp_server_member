package com.caipiao.server.member.body.response;

import com.caipiao.common.data.body.DataBody;

import lombok.Data;

@Data
public class LoginReponse implements DataBody {
	private String token;
	private String account;
}

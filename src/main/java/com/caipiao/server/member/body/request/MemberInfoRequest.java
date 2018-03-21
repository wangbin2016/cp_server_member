package com.caipiao.server.member.body.request;

import com.caipiao.common.data.body.DataBody;

import lombok.Data;

@Data
public class MemberInfoRequest implements DataBody{
	private String account;
	private String nickname;
	private String mobile;
}

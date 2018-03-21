package com.caipiao.server.member.body.response;

import com.caipiao.common.data.body.DataBody;

import lombok.Data;
@Data
public class MemberInfoResponse implements DataBody{
	private String account;
	private String nickname;
	private String mobile;
}

package com.caipiao.server.member.body.request;

import com.caipiao.common.data.body.DataBody;

import lombok.Data;

@Data
public class RegistRequest implements DataBody{
	private String id;
}

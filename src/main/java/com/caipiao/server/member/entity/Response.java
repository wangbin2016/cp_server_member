package com.caipiao.server.member.entity;

import lombok.Data;

@Data
public class Response<T> {
	ResponseStatus responseStatus;
	T t;
	public Response(ResponseStatus responseStatus,T t){
		this.responseStatus = responseStatus;
		this.t=t;
	}
}

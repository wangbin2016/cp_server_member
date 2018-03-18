package com.caipiao.server.member.entity;


public enum ResponseStatus {
	OK("0000", "成功"), FAIR("9999", "失败");
	ResponseStatus(String code, String message) {
		this.message = message;
		this.code = code;
	}
	public String code;
	public String message;
}

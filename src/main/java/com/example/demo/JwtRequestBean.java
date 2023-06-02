package com.example.demo;

public class JwtRequestBean {
	private String grant_type;

	public String getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

	public JwtRequestBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JwtRequestBean(String grant_type) {
		super();
		this.grant_type = grant_type;
	}

	@Override
	public String toString() {
		return "JwtRequestBean [grant_type=" + grant_type + "]";
	}
}

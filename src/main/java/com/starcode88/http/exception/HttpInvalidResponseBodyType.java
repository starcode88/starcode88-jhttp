package com.starcode88.http.exception;

public class HttpInvalidResponseBodyType extends HttpException {
	
	private Class<? extends Object> clazz;
	
	public Class<? extends Object> getBodyType() {
		return clazz;
	}

	public HttpInvalidResponseBodyType(Class<? extends Object> clazz) {
		this.clazz = clazz;
	}

}

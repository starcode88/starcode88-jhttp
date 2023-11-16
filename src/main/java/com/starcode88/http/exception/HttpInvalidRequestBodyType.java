package com.starcode88.http.exception;

public class HttpInvalidRequestBodyType extends HttpException {
	
	private static final long serialVersionUID = -4117404622765047912L;
	
	private Class<? extends Object> clazz;
	
	public Class<? extends Object> getBodyType() {
		return clazz;
	}

	public HttpInvalidRequestBodyType(Class<? extends Object> clazz) {
		this.clazz = clazz;
	}

}

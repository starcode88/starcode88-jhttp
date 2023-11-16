package com.starcode88.http.exception;

import java.net.http.HttpResponse;

public class HttpStatusCodeException extends HttpException {

	private static final long serialVersionUID = 4089317736435598447L;
	
	HttpResponse<?> response;
	
	public HttpResponse<?> getResponse() {
		return response;
	}

	public HttpStatusCodeException(HttpResponse<?> response) {
		this.response = response;
	}
}

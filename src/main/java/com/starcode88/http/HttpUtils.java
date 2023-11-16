package com.starcode88.http;

import java.net.HttpURLConnection;

public class HttpUtils {

	public static String getStatusText(int statusCode) {
	    switch (statusCode) {
	        case HttpURLConnection.HTTP_OK:
	            return "OK";
	        case HttpURLConnection.HTTP_CREATED:
	            return "Created";
	        case HttpURLConnection.HTTP_ACCEPTED:
	            return "Accepted";
	        case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
	            return "Non-Authoritative Information";
	        case HttpURLConnection.HTTP_NO_CONTENT:
	            return "No Content";
	        case HttpURLConnection.HTTP_RESET:
	            return "Reset Content";
	        case HttpURLConnection.HTTP_PARTIAL:
	            return "Partial Content";
	        case HttpURLConnection.HTTP_MULT_CHOICE:
	            return "Multiple Choices";
	        case HttpURLConnection.HTTP_MOVED_PERM:
	            return "Moved Permanently";
	        case HttpURLConnection.HTTP_MOVED_TEMP:
	            return "Moved Temporarily";
	        case HttpURLConnection.HTTP_SEE_OTHER:
	            return "See Other";
	        case HttpURLConnection.HTTP_NOT_MODIFIED:
	            return "Not Modified";
	        case HttpURLConnection.HTTP_USE_PROXY:
	            return "Use Proxy";
	        case HttpURLConnection.HTTP_BAD_REQUEST:
	            return "Bad Request";
	        case HttpURLConnection.HTTP_UNAUTHORIZED:
	            return "Unauthorized";
	        case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
	            return "Payment Required";
	        case HttpURLConnection.HTTP_FORBIDDEN:
	            return "Forbidden";
	        case HttpURLConnection.HTTP_NOT_FOUND:
	            return "Not Found";
	        case HttpURLConnection.HTTP_BAD_METHOD:
	            return "Method Not Allowed";
	        case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
	            return "Not Acceptable";
	        case HttpURLConnection.HTTP_PROXY_AUTH:
	            return "Proxy Authentication Required";
	        case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
	            return "Request Timeout";
	        case HttpURLConnection.HTTP_CONFLICT:
	            return "Conflict";
	        case HttpURLConnection.HTTP_GONE:
	            return "Gone";
	        case HttpURLConnection.HTTP_LENGTH_REQUIRED:
	            return "Length Required";
	        case HttpURLConnection.HTTP_PRECON_FAILED:
	            return "Precondition Failed";
	        case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
	            return "Request Entity Too Large";
	        case HttpURLConnection.HTTP_REQ_TOO_LONG:
	            return "Request-URI Too Long";
	        case HttpURLConnection.HTTP_UNSUPPORTED_TYPE:
	            return "Unsupported Media Type";
	        case HttpURLConnection.HTTP_INTERNAL_ERROR:
	            return "Internal Server Error";
	        case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
	            return "Not Implemented";
	        case HttpURLConnection.HTTP_BAD_GATEWAY:
	            return "Bad Gateway";
	        case HttpURLConnection.HTTP_UNAVAILABLE:
	            return "Service Unavailable";
	        case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
	            return "Gateway Timeout";
	        case HttpURLConnection.HTTP_VERSION:
	            return "HTTP Version Not Supported";
	        default:
	            return "Unknown";
	    }
	}

}

package com.starcode88.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.starcode88.http.exception.HttpInvalidRequestBodyType;
import com.starcode88.http.exception.HttpInvalidResponseBodyType;
import com.starcode88.http.exception.HttpStatusCodeException;

public class HttpClient {
	
	private static Logger logger = LogManager.getLogger(HttpClient.class);
	
	/** This list contains headers that will be automatically added to all HTTP requests */
	private LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

	/** 
	 * Base path of the server. The final URL will be build
	 * by the baseURL and an additional path. You can specify
	 * also an URL with a port number, e.g. http://localhost:8080 
	 */
	String baseUrl;
	
	/** SSL context is used to disable SSL verification for HTTPS */
	private static SSLContext sslContext = null; 
	
	/**
	 * enables/disables validation of SSL certificatess
	 */
	private boolean validateSSLCertificate = true;
	
	/**
	 * Constructor with base URL. Later the base URL will be used for 
	 * all HTTP requests. It is recommended to set the base URL
	 * without a "/" at the end. The "/" should be the first part of the
	 * path which will be later to the baseUrl.
	 * @param baseUrl
	 */
	public HttpClient(String baseUrl) {
		String version = System.getProperty("java.version");
		logger.debug("Current Java version = {}", version);
		this.baseUrl = baseUrl;
	}
	
	public void setValidateSSLCertificate(boolean validate) throws GeneralSecurityException {
		this.validateSSLCertificate = validate;
		if (validate == false && sslContext == null) {
			createSSLContextTrustAllCerts();
		}
	}
	
	public boolean getValidateSSLCertificate() {
		return this.validateSSLCertificate;
	}
	
	/**
	 * Adds a header. The headers will be added later to all our following HTTP requests.
	 * That is more convenient to set them once and later use them for all requests.
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}
	
	/**
	 * Removes the header given by key
	 * @param key
	 * @return The value of the header given by key. Maybe later you want to set
	 * it again.
	 */
	public String removeHeader(String key) {
		return this.headers.remove(key);
	}
	
	/**
	 * Gets the value of the header given by key
	 * @param key
	 * @return
	 */
	public String getHeader(String key) {
		return this.headers.get(key);
	}

	public <R> HttpResponse<R> DELETE(String path, Class<R> responseBodyClass) 
					throws URISyntaxException, HttpStatusCodeException,
					HttpInvalidResponseBodyType, IOException,
					InterruptedException {
		
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		setUri(requestBuilder, path);
		addHeaders(requestBuilder);
		requestBuilder.DELETE();

		HttpResponse<R> httpResponse = (HttpResponse<R>) 
				send(requestBuilder.build(), null, responseBodyClass, null);
		
		return httpResponse;
	}
	
	
	/**
	 * 
	 * @param <R> The return type, it must be <String> or <byte[]>
	 * @param path The path of the URL. It will be added to the URL given in the constructor
	 * @param responseBodyClass The type of the response
	 * @return The HTTP response
	 * @throws URISyntaxException Will be thrown if the syntax of the URL is invalid
	 * @throws HttpStatusCodeException Will be thrown if the status code is not 200
	 * @throws HttpInvalidResponseBodyType Will be thrown if the argument <responseBodyClass> is not String.class or byte[].class
	 * @throws IOException Will be thrown if there is a problem to access the URL, for example server can not be reached (connection timeout)
	 * @throws InterruptedException Will be thrown if the send request will be interrupted
	 */
	public <R> HttpResponse<R> GET(String path, Class<R> responseBodyClass)
			throws URISyntaxException, HttpStatusCodeException, 
			HttpInvalidResponseBodyType, IOException, InterruptedException {
		
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		setUri(requestBuilder, path);
		addHeaders(requestBuilder);
		requestBuilder.GET();

		HttpResponse<R> httpResponse = (HttpResponse<R>) send(requestBuilder.build(), null, responseBodyClass, null);
		return httpResponse;
	}
	
	/**
	 * @param path Path which will be added to the URL
	 * @param file File to which the response will be stored
	 * @return The HTTP response
	 * @throws URISyntaxException Will be thrown if the syntax of the URL is invalid
	 * @throws HttpStatusCodeException Will be thrown if the status code is not 200
	 * @throws HttpInvalidResponseBodyType Will be thrown if the argument <responseBodyClass> is not String.class or byte[].class
	 * @throws IOException Will be thrown if there is a problem to access the URL, for example server can not be reached (connection timeout)
	 * @throws InterruptedException Will be thrown if the send request will be interrupted
	 */
	public HttpResponse<Path> GET(String path, Path file) 
			    throws URISyntaxException, HttpStatusCodeException,
			    HttpInvalidResponseBodyType, IOException, InterruptedException {
		
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		setUri(requestBuilder, path);
		addHeaders(requestBuilder);
		requestBuilder.GET();

		HttpResponse<Path> httpResponse = send(requestBuilder.build(), null, Path.class, file);
		return httpResponse;
	}

	
	/**
	 * Sends a POST request
	 * @param <R> The response type, must be <String> or <byte[]>
	 * @param <T> 
	 * @param path
	 * @param body
	 * @param responseBodyClass
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws HttpStatusCodeException
	 * @throws HttpInvalidRequestBodyType
	 * @throws HttpInvalidResponseBodyType
	 */
	public <R, T> HttpResponse<R>
				POST(String path, T body, Class<R> responseBodyClass)
					throws URISyntaxException, IOException,
					InterruptedException, HttpStatusCodeException,
					HttpInvalidRequestBodyType,	HttpInvalidResponseBodyType {
		
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		setUri(requestBuilder, path);
		addHeaders(requestBuilder);
																																	
		if (body instanceof String) {
			requestBuilder.POST(HttpRequest.BodyPublishers.ofString((String)body));
		} else if (body instanceof byte[]) {
			requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray((byte[])body));
		} else if (body instanceof Iterable<?>) {
			Iterable<?> iterable = (Iterable<?>) body;
			if (iterable.iterator().hasNext() && iterable.iterator().next() instanceof byte[])
			{
				requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArrays((Iterable<byte[]>) body));
			}
			else {
				throw logger.throwing(new HttpInvalidRequestBodyType(body.getClass()));
			}
	
		} else if (body instanceof Path) {
			requestBuilder.POST(HttpRequest.BodyPublishers.ofFile((Path)body));
		} else if (body == null) {
			requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
		} else {
			throw logger.throwing(new HttpInvalidRequestBodyType(body.getClass()));
		}
		
		HttpResponse<R> response = (HttpResponse<R>) send(requestBuilder.build(), body, responseBodyClass, null);
		
		return response;
	}
	
	public <R, T> HttpResponse<R> 
					PUT(String path, T body, Class<R> responseBodyClass) 
						throws URISyntaxException, HttpInvalidRequestBodyType,
						HttpStatusCodeException, HttpInvalidResponseBodyType,
						IOException, InterruptedException {
		
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		setUri(requestBuilder, path);
		addHeaders(requestBuilder);
		
		if (body instanceof String) {
			requestBuilder.PUT(HttpRequest.BodyPublishers.ofString((String)body));
		} else if (body instanceof byte[]) {
			requestBuilder.PUT(HttpRequest.BodyPublishers.ofByteArray((byte[])body));
		} else if (body instanceof Path) {
			requestBuilder.PUT(HttpRequest.BodyPublishers.ofFile((Path)body));
		} else if (body == null) {
			requestBuilder.PUT(HttpRequest.BodyPublishers.noBody());
		} else {
			throw logger.throwing(new HttpInvalidRequestBodyType(body.getClass()));
		}
		
		HttpResponse<R> response = (HttpResponse<R>)
				send(requestBuilder.build(), body, responseBodyClass, null);
		
		return response;
	}
	/**
	 * Sends the HTTP request. This is the most important functions because
	 * here happens all the HTTP communication.
	 * 
	 * @param <R> The return type of the HttpResponse. This must be of type String or byte[].
	 * @param <T>
	 * @param request The prepared HttpRequest
	 * @param body The request body. The argument is only used for logging purpose. 
	 * @param responseBodyClass Must be String.class, byte[].class or Path.class
	 *        or null. The value null means that you don't want to get the
	 *        body of the response.
	 * @param file If the responseBodyClass is type of Path.class then you must
	 *             provide here a filename to which the response will be written
	 * @return
	 * @throws IOException
	 * @throws InterruptedException Will be thrown if the send function will be interrupted
	 * @throws HttpStatusCodeException Will be thrown if response status code is not 200.
	 * @throws HttpInvalidResponseBodyType Will be thrown if the
	 * 									   responseBodyClass is not String.class
	 * 									   or byte[].class. Other types are
	 *                                     not supported.
	 */
	@SuppressWarnings("unchecked") // Compiler doesn't understand that the
								   // typecast to BodyHandler<R> is save
								   // in our implementation 
	private <R, T> HttpResponse<R>
				send(HttpRequest request, T body, Class<R> responseBodyClass,
					Path file) throws IOException, InterruptedException,
						HttpStatusCodeException, HttpInvalidResponseBodyType {
		
		java.net.http.HttpClient.Builder httpClientBuilder = java.net.http.HttpClient.newBuilder();
		
		if (this.validateSSLCertificate == false) {
			if (sslContext != null) {
				logger.debug("Set SSL context ...");
				httpClientBuilder.sslContext(sslContext);
				logger.debug("SSL context had been set");
			} else {
				logger.error("SSL context is null");
			}
		} else {
			logger.warn("Validation of SSL certificate is enabled, this might "
				+ "cause an exception if SSL certificate does not exist "
				+ "in local trust store");
		}
		
		java.net.http.HttpClient httpClient = httpClientBuilder.build();
		
		HttpResponse<R> response = null;
		
		HttpResponse.BodyHandler<R> bodyHandler = null;
		
		if (responseBodyClass == null) {
			bodyHandler = (BodyHandler<R>) HttpResponse.BodyHandlers.discarding();
		} else if (responseBodyClass.equals(String.class)) {
			bodyHandler = (BodyHandler<R>) HttpResponse.BodyHandlers.ofString();
		} else if (responseBodyClass.equals(byte[].class)) {
			bodyHandler = (BodyHandler<R>) HttpResponse.BodyHandlers.ofByteArray();
		} else if (responseBodyClass.equals(Path.class)) {
			if (file == null) {
				throw logger.throwing(new IllegalArgumentException(
						"The argument <path> is null, but the responseBodyClass is Path.class. "
						+ "You must provide a valid path in case the responeBodyClass is type of Path.class"));
			}
			bodyHandler = (BodyHandler<R>) HttpResponse.BodyHandlers.ofFile(file);
		} else {
			throw logger.throwing(new HttpInvalidResponseBodyType(responseBodyClass));
		}
		
		try {
			logSendingHttpRequest(request, body);
			response = (HttpResponse<R>) httpClient.send(request, bodyHandler);
		} catch (IOException e) {
			throw logger.throwing(e);
		} catch (InterruptedException e) {
			throw logger.throwing(e);
		}
		
		logger.info("Received HTTP response:");
		int statusCode = response.statusCode();
		String statusText = HttpUtils.getStatusText(statusCode);
		logger.info("    Status code = {} ({})", statusCode, statusText);
		HttpHeaders headers = response.headers();
		logHeaders(headers);
		
		if (response.body() instanceof String) {
			logger.info("    Body = {}", (String)response.body());
		}

		if (response.statusCode() < 200 || response.statusCode() >= 300 ) {
			throw logger.throwing(new HttpStatusCodeException(response));
		}

		return response;
	}
	

	
	/**
	 * Sets the URI in the request builder. It will be set as baseUrl + path.
	 * The path is given as argument and the baseUrl has been given in the
	 * constructor.
	 * 
	 * @param builder The builder object on which the URL will be set.
	 * 
	 * @param path The path which will be added to the baseUrl. If the
	 *             path is null then only the baseUrl will be used.
	 *             
	 * @throws URISyntaxException Will be thrown if the syntax 
	 *                            of the URL is not correct
	 */
	private void setUri(HttpRequest.Builder builder, String path)
					throws URISyntaxException {
		try {
			String uri = baseUrl;
			if (path != null) {
				uri += path;
			}
			builder.uri(new URI(uri));
		} catch (URISyntaxException e) {
			throw logger.throwing(e);
		}
	}
	
	/**
	 * Adds the headers to the request builder.
	 * 
	 * @param builder
	 */
	private void addHeaders(HttpRequest.Builder builder) {
		Set<String> keys = headers.keySet();
		for (String key : keys) {
			String value = headers.get(key);
			logger.debug("Add header '" + key + "' with value '" + value + "'");
			builder.header(key, value);
		}
	}
	
	private void logHeaders(HttpHeaders headers) {
		if (headers == null) {
			return;
		}
		logger.info("    Headers:");
    	String keyWithValues = "";
    	Map<String, List<String>> map = headers.map();
    	Set<String> keys = map.keySet();
    	for (String key : keys) {
    		keyWithValues = key + " = ";
    		for (String value : map.get(key)) {
    			keyWithValues += value;
    		}
    		logger.info("        {}", keyWithValues);
    	}
	}
	
	/**
	 * Logs the HTTP request
	 * @param <T>
	 * @param request
	 * @param body
	 */
	private <T> void logSendingHttpRequest(HttpRequest request, T body) {
		logger.info("Sending HTTP request: ");
		logger.info("    {}", request.uri());
		logger.info("    {}", request.method());
		logHeaders(request.headers());
		if (body != null) {
			if (body instanceof String) {
				logger.info("    Body = {}", body);
			}
		}
	}
	
	/**
	 * Creates a SSL context to trust all SSL certificates. We use
	 * it later to build the HTTP request. We need to create this
	 * context only once, therefore the context is stored in a member
	 * variable.
	 * @throws GeneralSecurityException 
	 */
	public void createSSLContextTrustAllCerts() throws GeneralSecurityException {
		try {
			logger.debug("Create SSL context to disable verification of SSL certificates ...");
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(
			    null,
			    new TrustManager[]
			    {
			        new X509ExtendedTrustManager()
			        {
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
								throws CertificateException {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
								throws CertificateException {
						}

						@Override
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}

						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType,
								Socket socket) throws CertificateException {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType,
								Socket socket) throws CertificateException {
						}

						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType,
								SSLEngine engine) throws CertificateException {
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType,
								SSLEngine engine) throws CertificateException {
						}
			        }
			    },
			    null);
			logger.debug("SSL context created");
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			logger.fatal("Failed to create SSL context");
			sslContext = null;
			throw logger.throwing(Level.FATAL, e);
		}
	}
}

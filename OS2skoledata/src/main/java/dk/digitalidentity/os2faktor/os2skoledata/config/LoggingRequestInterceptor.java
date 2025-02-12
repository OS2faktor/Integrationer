package dk.digitalidentity.os2faktor.os2skoledata.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		traceRequest(request, body);
		return execution.execute(request, body);
	}

	private void traceRequest(HttpRequest request, byte[] body) throws IOException {
		log.info("===========================request begin================================================");
		log.info("URI         : {}", request.getURI());
		log.info("Method      : {}", request.getMethod());
		log.info("Request body: {}", new String(body, "UTF-8"));
		log.info("==========================request end================================================");
	}
}
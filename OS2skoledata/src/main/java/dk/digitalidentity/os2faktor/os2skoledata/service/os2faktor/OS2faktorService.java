package dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor;

import dk.digitalidentity.os2faktor.os2skoledata.dao.model.Municipality;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.OS2faktorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OS2faktorService {
	@Autowired
	private RestTemplate restTemplate;
	public boolean sendToOS2faktor(Municipality municipality, OS2faktorData data) {
		HttpHeaders headers = getHeaders(municipality.getOs2faktorApiKey());
		HttpEntity<OS2faktorData> request = new HttpEntity<>(data, headers);
		String url = municipality.getOs2faktorUrl() + "/api/stil/full";

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return true;
			}
		}
		catch (HttpClientErrorException e) {
			log.warn(e.getResponseBodyAsString());
		}

		return false;
	}

	private HttpHeaders getHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("ApiKey", apiKey);
		headers.add("Content-Type", "application/json");

		return headers;
	}
}

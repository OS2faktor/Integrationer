package dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata;

import dk.digitalidentity.os2faktor.os2skoledata.dao.model.Municipality;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.OS2skoledataInstitution;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.OS2skoledataGroup;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.OS2skoledataUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OS2skoledataService {

	@Autowired
	private RestTemplate restTemplate;

	public List<OS2skoledataInstitution> getInstitutions(Municipality municipality) throws Exception {
		log.info("Fetching institutions");
		HttpEntity<String> request = new HttpEntity<>(getHeaders(municipality));
		String query = municipality.getOs2skoledataUrl() + "/api/institutions";

		ResponseEntity<OS2skoledataInstitution[]> response = restTemplate.exchange(query, HttpMethod.GET, request, OS2skoledataInstitution[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch institutions. Will not update");
		}

		log.info("Finished fetching institutions");
		return Arrays.asList(response.getBody());
	}
	public List<OS2skoledataUser> getUsersForInstitution(OS2skoledataInstitution institution, Municipality municipality) throws Exception {

		HttpEntity<String> request = new HttpEntity<>(getHeaders(municipality));
		String query = municipality.getOs2skoledataUrl() + "/api/persons?institutionNumber=" + institution.getInstitutionNumber();

		ResponseEntity<OS2skoledataUser[]> response = restTemplate.exchange(query, HttpMethod.GET, request, OS2skoledataUser[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch institutions. Will not update");
		}

		log.info("Finished fetching users for institutions " + institution.getInstitutionName());
		return Arrays.asList(response.getBody());
	}

	public List<OS2skoledataGroup> getGroupsForInstitution(OS2skoledataInstitution institution, Municipality municipality) throws Exception {
		log.info("Fetching classes for institutions " + institution.getInstitutionName());
		HttpEntity<String> request = new HttpEntity<>(getHeaders(municipality));
		String query = municipality.getOs2skoledataUrl() + "/api/groups?institutionNumber=" + institution.getInstitutionNumber() + "&alltypes=true";

		ResponseEntity<OS2skoledataGroup[]> response = restTemplate.exchange(query, HttpMethod.GET, request, OS2skoledataGroup[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch groups for institution " + institution.getInstitutionName() + ". Will not update");
		}

		log.info("Finished fetching classes for institutions " + institution.getInstitutionName());
		return Arrays.asList(response.getBody());
	}

	private HttpHeaders getHeaders(Municipality municipality) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("apiKey", municipality.getOs2skoledataApiKey());
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		return headers;
	}
}

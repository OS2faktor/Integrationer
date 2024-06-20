package dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OS2skoledataInstitution {
	private long databaseId;
	@JsonProperty("number")
	private String institutionNumber;
	@JsonProperty("name")
	private String institutionName;
	private String allAzureSecurityGroupId;
	private String studentAzureSecurityGroupId;
	private String employeeAzureSecurityGroupId;
	private boolean locked;
}

package dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model;

import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.enums.OS2skoledataEmployeeRole;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.enums.OS2skoledataExternalRole;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.enums.OS2skoledataRole;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.enums.OS2skoledataStudentRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OS2skoledataUser {
	private long databaseId;
	private String cpr;
	private String firstName;
	private String familyName;
	private String username;
	private OS2skoledataRole role;
	private String currentInstitutionNumber;
	private String stilMainGroupCurrentInstitution;
	private List<String> stilGroupsCurrentInstitution;
	private String studentMainGroupLevelForInstitution;
	private OS2skoledataStudentRole studentRole;
	private List<OS2skoledataEmployeeRole> employeeRoles;
	private OS2skoledataExternalRole externalRole;
}

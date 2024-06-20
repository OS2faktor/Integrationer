package dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model;

import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.enums.OS2faktorPersonRole;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.enums.OS2faktorType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OS2faktorPerson {
	private String cpr;
	private String name;
	private String userId;
	private String institutionNumber;
	private String institutionName;
	private OS2faktorType type;
	private OS2faktorPersonRole role;
	private String level;
	private List<String> groups;
}

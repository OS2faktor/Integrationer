package dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model;

import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.enums.OS2skoledataGroupType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OS2skoledataGroup {
	private long databaseId;
	private String groupName;
	private String groupId;
	private String groupLevel;
	private OS2skoledataGroupType groupType;
}

package dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model;

import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.enums.OS2faktorGroupType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OS2faktorGroup {
    private String id;
    private OS2faktorGroupType type;
    private String institutionNumber;
    private String level;
    private String name;
}

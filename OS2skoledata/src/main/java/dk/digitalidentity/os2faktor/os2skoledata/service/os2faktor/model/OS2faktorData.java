package dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OS2faktorData {
    private List<OS2faktorGroup> studentGroups;
    private List<OS2faktorPerson> people;
    private String domainName;
}

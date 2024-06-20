package dk.digitalidentity.os2faktor.os2skoledata.dao;

import dk.digitalidentity.os2faktor.os2skoledata.dao.model.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MunicipalityDao extends JpaRepository<Municipality, Long> {
}
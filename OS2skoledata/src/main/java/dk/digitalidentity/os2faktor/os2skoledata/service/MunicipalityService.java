package dk.digitalidentity.os2faktor.os2skoledata.service;

import dk.digitalidentity.os2faktor.os2skoledata.dao.MunicipalityDao;
import dk.digitalidentity.os2faktor.os2skoledata.dao.model.Municipality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipalityService {

	@Autowired
	private MunicipalityDao municipalityDao;

	public List<Municipality> findAll() {
		return municipalityDao.findAll();
	}
}

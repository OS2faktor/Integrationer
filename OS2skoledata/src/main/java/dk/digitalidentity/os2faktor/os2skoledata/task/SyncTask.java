package dk.digitalidentity.os2faktor.os2skoledata.task;

import dk.digitalidentity.os2faktor.os2skoledata.dao.model.Municipality;
import dk.digitalidentity.os2faktor.os2skoledata.service.SyncService;
import dk.digitalidentity.os2faktor.os2skoledata.service.MunicipalityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class SyncTask {

	@Autowired
	private MunicipalityService municipalityService;
	@Autowired
	private SyncService syncService;

	// @Scheduled(fixedRate = 60 * 1000) // for testing
	// run daily at 01:10 unless something else is in the application.properties
	@Scheduled(cron = "${cron.fullSync:0 10 1 * * ?}")
	public void sync() throws Exception {
		for (Municipality municipality : municipalityService.findAll()) {
			if (municipality.isEnabled()) {
				log.info("Syncing municipality " + municipality.getName());
				syncService.syncMunicipality(municipality);
			}
			else {
				log.warn("Skipping municipality " + municipality.getName() + " because it is not enabled");
			}
		}
	}
}

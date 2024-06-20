package dk.digitalidentity.os2faktor.os2skoledata.service;

import dk.digitalidentity.os2faktor.os2skoledata.dao.model.Municipality;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.OS2faktorService;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.OS2faktorData;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.OS2faktorGroup;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.OS2faktorPerson;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.enums.OS2faktorGroupType;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.enums.OS2faktorPersonRole;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2faktor.model.enums.OS2faktorType;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.OS2skoledataService;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.OS2skoledataInstitution;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.OS2skoledataGroup;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.OS2skoledataUser;
import dk.digitalidentity.os2faktor.os2skoledata.service.os2skoledata.model.enums.OS2skoledataRole;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SyncService {

	@Autowired
	private OS2skoledataService os2skoledataService;

	@Autowired
	private OS2faktorService os2faktorService;

	@Transactional
	public void syncMunicipality(Municipality municipality) {
		try {
			List<OS2faktorPerson> os2faktorPersonList = new ArrayList<>();
			List<OS2faktorGroup> os2faktorGroups = new ArrayList<>();

			for (OS2skoledataInstitution institution : os2skoledataService.getInstitutions(municipality)) {
				log.info("Reading users from institution " + institution.getInstitutionName() + " for " + municipality.getName());

				List<OS2skoledataGroup> groups = os2skoledataService.getGroupsForInstitution(institution, municipality);
				List<OS2faktorPerson> personsInThisInsitution = new ArrayList<>();
				List<OS2faktorPerson> studentsInThisInstitution = new ArrayList<>();
				for (OS2skoledataUser person : os2skoledataService.getUsersForInstitution(institution, municipality)) {

					// ignore if there is no username object or if cpr contains anything besides digits (ie. test persons)
					if (person.getUsername() == null || !person.getCpr().matches("^\\d{10}$")) {
						continue;
					}

					if (person.getRole().equals(OS2skoledataRole.EMPLOYEE) || person.getRole().equals(OS2skoledataRole.STUDENT) || (person.getRole().equals(OS2skoledataRole.EXTERNAL) && municipality.isExternalEnabled())) {
						List<OS2faktorPerson> people = map(person, institution.getInstitutionNumber(), institution.getInstitutionName(), groups);
						personsInThisInsitution.addAll(people);
						studentsInThisInstitution.addAll(people.stream().filter(s -> s.getType().equals(OS2faktorType.STUDENT)).collect(Collectors.toList()));
					}
					else {
						log.debug("Skipping user: " + person.getUsername() + " for " + municipality.getName() + ". No Employee, Extern or Student object.");
					}
				}

				// perform filtering - only interested in groups that a student is a member of
				Set<String> studentGroups = new HashSet<>();
				for (OS2faktorPerson student : studentsInThisInstitution) {
					studentGroups.addAll(student.getGroups());
				}

				// now filter all the persons that we have created, so we get rid of non-relevant groups
				for (var person : personsInThisInsitution) {
					if (person.getGroups() == null || person.getGroups().size() == 0) {
						continue;
					}

					person.getGroups().removeIf(g -> !studentGroups.contains(g));
				}

				// all all groups to global list
				int groupCounter = 0;
				for (var group : groups) {
					if (!studentGroups.contains(group.getGroupId())) {
						continue;
					}

					OS2faktorGroupType actualType = getOS2faktorGroupType(group);

					if (actualType == null) {
						continue;
					}

					OS2faktorGroup os2faktorGroup = new OS2faktorGroup();
					os2faktorGroup.setId(group.getGroupId());
					os2faktorGroup.setLevel(group.getGroupLevel());
					os2faktorGroup.setName(group.getGroupName());
					os2faktorGroup.setInstitutionNumber(institution.getInstitutionNumber());
					os2faktorGroup.setType(actualType);
					os2faktorGroups.add(os2faktorGroup);

					groupCounter++;
				}

				// all all persons to global list
				os2faktorPersonList.addAll(personsInThisInsitution);

				log.info("Found " + personsInThisInsitution.size() + " users and " + groupCounter + " groups from institution " + institution.getInstitutionNumber());
			}

			log.info("Synchronizing " + os2faktorPersonList.size() + " users and " + os2faktorGroups.size() + " groups from OS2skoledata for " + municipality.getName());

			OS2faktorData data = new OS2faktorData();
			data.setStudentGroups(os2faktorGroups);
			data.setPeople(os2faktorPersonList);
			data.setDomainName(municipality.getOs2faktorDomainName());

			boolean success = os2faktorService.sendToOS2faktor(municipality, data);

			if (!success) {
				log.error("Failed to send data to NSIS for municipality " + municipality.getName());
			}

		}
		catch (Exception ex) {
			log.error("Synchronization failed for " + municipality.getName(), ex);
		}
	}

	private List<OS2faktorPerson> map(OS2skoledataUser person, String institutionNumber, String institutionName, List<OS2skoledataGroup> groups) {
		List<OS2faktorPerson> people = new ArrayList<>();
		try {
			if (person.getRole().equals(OS2skoledataRole.STUDENT)) {
				Set<String> groupIds = new HashSet<>();
				groupIds.add(person.getStilMainGroupCurrentInstitution());
				groupIds.addAll(person.getStilGroupsCurrentInstitution());

				people.add(getOS2faktorPerson(person, institutionNumber, institutionName, OS2faktorPersonRole.STUDENT, OS2faktorType.STUDENT, person.getStudentMainGroupLevelForInstitution(), groupIds, groups));
			}

			if (person.getRole().equals(OS2skoledataRole.EMPLOYEE)) {
				List<String> roles = new ArrayList<>();

				if (person.getEmployeeRoles() != null) {
					roles = person.getEmployeeRoles().stream().map(r -> r.toString()).collect(Collectors.toList());
				}

				for (String role : roles) {
					OS2faktorPersonRole actualRole = getOS2faktorPersonRole(role);
					people.add(getOS2faktorPerson(person, institutionNumber, institutionName, actualRole, OS2faktorType.EMPLOYEE, null, new HashSet<>(person.getStilGroupsCurrentInstitution()), groups));
				}
			}

			if (person.getRole().equals(OS2skoledataRole.EXTERNAL)) {
				OS2faktorPersonRole actualRole = getOS2faktorPersonRole(person.getExternalRole().toString());
				people.add(getOS2faktorPerson(person, institutionNumber, institutionName, actualRole, OS2faktorType.EXTERN, null, new HashSet<>(person.getStilGroupsCurrentInstitution()), groups));
			}
		}
		catch (Exception ex) {
			log.warn("Failed on : " + person.getUsername());
			throw ex;
		}

		return people;
	}

	private OS2faktorPerson getOS2faktorPerson(OS2skoledataUser person, String institutionNumber, String institutionName, OS2faktorPersonRole role, OS2faktorType type, String level, Set<String> groupIds, List<OS2skoledataGroup> allGroups) {
		OS2faktorPerson os2faktorPerson = new OS2faktorPerson();
		os2faktorPerson.setCpr(person.getCpr());
		os2faktorPerson.setName(person.getFirstName() + " " + person.getFamilyName());
		os2faktorPerson.setUserId(person.getUsername());
		os2faktorPerson.setInstitutionNumber(institutionNumber);
		os2faktorPerson.setInstitutionName(institutionName);
		os2faktorPerson.setRole(role);
		os2faktorPerson.setType(type);
		os2faktorPerson.setLevel(level);

		List<String> groups = new ArrayList<>();

		for (String groupId : groupIds) {
			OS2skoledataGroup match = allGroups.stream().filter(g -> g.getGroupId().equals(groupId)).findAny().orElse(null);

			if (match != null) {
				groups.add(groupId);
			}
			else {
				log.warn("Skipping group with id " + groupId + " for person : " + person.getUsername() + ". The group was not found in set.");
			}
		}
		os2faktorPerson.setGroups(groups);

		return os2faktorPerson;
	}

	private OS2faktorPersonRole getOS2faktorPersonRole(String role) {
		OS2faktorPersonRole actualRole = null;

		switch (role) {
			case "LÆRER":
				actualRole = OS2faktorPersonRole.TEACHER;
				break;
			case "PÆDAGOG":
				actualRole = OS2faktorPersonRole.PEDAGOGUE;
				break;
			case "VIKAR":
				actualRole = OS2faktorPersonRole.SUBSTITUTE;
				break;
			case "LEDER":
				actualRole = OS2faktorPersonRole.LEADER;
				break;
			case "LEDELSE":
				actualRole = OS2faktorPersonRole.MANAGEMENT;
				break;
			case "TAP":
				actualRole = OS2faktorPersonRole.TAP;
				break;
			case "KONSULENT":
				actualRole = OS2faktorPersonRole.CONSULTANT;
				break;
			case "EKSTERN":
				actualRole = OS2faktorPersonRole.EXTERN;
				break;
			case "PRAKTIKANT":
				actualRole = OS2faktorPersonRole.TRAINEE;
				break;
			default:
				log.error("Unknown role " + role);
				break;
		}

		return actualRole;
	}

	private OS2faktorGroupType getOS2faktorGroupType(OS2skoledataGroup group) {
		OS2faktorGroupType actualType = null;

		switch (group.getGroupType()) {
			case HOVEDGRUPPE:
				actualType = OS2faktorGroupType.MAIN_GROUP;
				break;
			case ÅRGANG:
				actualType = OS2faktorGroupType.YEAR;
				break;
			case RETNING:
				actualType = OS2faktorGroupType.DIRECTION;
				break;
			case HOLD:
				actualType = OS2faktorGroupType.UNIT;
				break;
			case SFO:
				actualType = OS2faktorGroupType.SFO;
				break;
			case TEAM:
				actualType = OS2faktorGroupType.TEAM;
				break;
			case ANDET:
				actualType = OS2faktorGroupType.OTHER;
				break;
			default:
				log.error("Unknown group type " + group.getGroupType().toString() + ". Will skip this group.");
				break;
		}

		return actualType;
	}
}
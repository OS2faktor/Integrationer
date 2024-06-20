package dk.digitalidentity.os2faktor.os2skoledata.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Municipality {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@Column
	private String os2skoledataUrl;
	
	@Column
	private String os2skoledataApiKey;

	@Column
	private String os2faktorUrl;
	
	@Column
	private String os2faktorApiKey;
	
	@Column
	private String os2faktorDomainName;

	@Column
	private boolean externalEnabled;
	
	@Column
	private boolean enabled;
}
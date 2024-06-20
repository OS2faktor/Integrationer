CREATE TABLE municipality (
  id                              BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name                            VARCHAR(255) NOT NULL,

  os2skoledata_url                VARCHAR(255) NOT NULL,
  os2skoledata_api_key            VARCHAR(36) NOT NULL,

  os2faktor_url                   VARCHAR(255) NOT NULL,
  os2faktor_api_key               VARCHAR(36) NOT NULL,
  os2faktor_domain_name           VARCHAR(255) NOT NULL,

  enabled                         BOOL NOT NULL DEFAULT TRUE,
  external_enabled                BOOL NOT NULL DEFAULT FALSE
);
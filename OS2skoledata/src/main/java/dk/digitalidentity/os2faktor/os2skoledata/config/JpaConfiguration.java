package dk.digitalidentity.os2faktor.os2skoledata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(basePackages = { "dk.digitalidentity.os2faktor.os2skoledata.dao" }, repositoryFactoryBeanClass = JpaRepositoryFactoryBean.class)
public class JpaConfiguration {

}
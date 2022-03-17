package io.gottabe.userservices;

import io.gottabe.commons.config.CommonsConfig;
import io.gottabe.commons.config.SiteConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableConfigurationProperties(SiteConfig.class)
@ComponentScan({"io.gottabe.userservices", "io.gottabe.commons"})
@Import(CommonsConfig.class)
@EnableAuthorizationServer
@EnableResourceServer
@EnableSwagger2
public class GottabeUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(GottabeUserApplication.class, args);
	}

}

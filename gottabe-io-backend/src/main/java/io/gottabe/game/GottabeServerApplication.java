package io.gottabe.game;

import io.gottabe.commons.config.CommonsConfig;
import io.gottabe.commons.config.SiteConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableConfigurationProperties(SiteConfig.class)
@ComponentScan({"io.gottabe.game", "io.gottabe.commons"})
@Import({CommonsConfig.class})
@EnableSwagger2
public class GottabeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GottabeServerApplication.class, args);
	}

}

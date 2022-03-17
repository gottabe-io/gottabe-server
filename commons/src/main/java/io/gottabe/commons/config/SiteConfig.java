package io.gottabe.commons.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
@ConfigurationProperties(prefix = "gottabeio")
public class SiteConfig {
	
	private String siteName;

	private String siteAddress;
	
}

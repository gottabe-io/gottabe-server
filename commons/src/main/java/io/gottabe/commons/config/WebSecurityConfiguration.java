package io.gottabe.commons.config;

import io.gottabe.commons.security.oauth.TokenAuthFilter;
import io.gottabe.commons.security.oauth.TokenServices;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.DigestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${gottabeio.config.salty.password}")
	String passwordSalty;

	@Autowired
	TokenServices tokenAuthService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder2() throws DecoderException {
		byte[] seed = DigestUtils.md5Digest(passwordSalty.getBytes());
		return new BCryptPasswordEncoder(5, new SecureRandom(seed));
	}

	@Bean
	public PasswordEncoder passwordEncoder() throws DecoderException {
		return new DelegatingPasswordEncoder("bcrypt", Map.of("bcrypt", passwordEncoder2()));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().filterSecurityInterceptorOncePerRequest(true).anyRequest().permitAll();
		http.addFilterBefore(new TokenAuthFilter(tokenAuthService), UsernamePasswordAuthenticationFilter.class);

		http.headers().cacheControl().disable();
	}

	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(List.of(
				HttpMethod.GET.name(),
				HttpMethod.PUT.name(),
				HttpMethod.POST.name(),
				HttpMethod.DELETE.name()
		));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
		return source;
	}

}

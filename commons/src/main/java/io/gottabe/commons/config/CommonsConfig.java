package io.gottabe.commons.config;

import io.gottabe.commons.store.FileStore;
import io.gottabe.commons.store.impl.SystemFileStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@EntityScan("io.gottabe.commons.entities")
@EnableJpaRepositories("io.gottabe.commons.repositories")
public class CommonsConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver  slr = new AcceptHeaderLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean(name = "fileStore")
    @ConditionalOnProperty(name = "gottabeio.store.useS3", havingValue = "false")
    public FileStore fileStoreFile(@Value("${gottabeio.store.base.dir}") String baseDir) {
        return new SystemFileStore(baseDir);
    }

}

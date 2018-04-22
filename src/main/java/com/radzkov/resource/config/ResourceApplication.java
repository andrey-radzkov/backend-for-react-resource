package com.radzkov.resource.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.StringUtils;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableResourceServer
@EnableJpaRepositories("com.radzkov.resource.repository")
@EntityScan("com.radzkov.resource.entity")
@ComponentScan("com.radzkov")
public class ResourceApplication extends ResourceServerConfigurerAdapter {

    private static Logger LOG = Logger.getLogger(ResourceApplication.class);
    @Value("${security.oauth2.resource.tokenInfoUri:}")
    private String tokenInfoUri; //TODO: remove log

    public static void main(String[] args) {

        SpringApplication.run(ResourceApplication.class, args);
    }

    @Bean
    public RemoteTokenServices tokenService() {

        if (!StringUtils.isEmpty(tokenInfoUri)) {
            RemoteTokenServices tokenService = new RemoteTokenServices();
            tokenService.setCheckTokenEndpointUrl(tokenInfoUri);
            tokenService.setClientId("acme");
            tokenService.setClientSecret("acmesecret");
            return tokenService;
        } else {
            return null;
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/localization/*").permitAll()
                .antMatchers("/**").access("#oauth2.hasScope('resource-read')")
        ;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource-id1");
    }


}


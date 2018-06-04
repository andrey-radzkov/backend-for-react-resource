package com.radzkov.resource.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
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
@EnableScheduling
@EnableJpaRepositories("com.radzkov.resource.repository")
@EntityScan("com.radzkov.resource.entity")
@ComponentScan("com.radzkov")
public class ResourceApplication extends ResourceServerConfigurerAdapter {
    //TODO: deploy to heroku with update in release phase, not in deploy
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
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-ui").permitAll()
                .antMatchers("/configuration/security").permitAll()
                .antMatchers("/swagger-resources/configuration/security").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll()
                .antMatchers("/configuration/ui").permitAll()
                .antMatchers("/webjars/*").permitAll()
                .antMatchers("/swagger-resources**").permitAll()
                .antMatchers("/swagger-resource**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/**").access("#oauth2.hasScope('resource-read')")
        ;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource-id1");
    }


}


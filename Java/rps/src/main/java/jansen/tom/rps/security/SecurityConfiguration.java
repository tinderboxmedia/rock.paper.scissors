package jansen.tom.rps.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final static String authUrl = "/api/authentication";
    private final static String accountUrl = "/api/account";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Allowing post call
        http.csrf().disable();

        http
                .authorizeRequests()
                // Allows anyone to access the following rest endpoints here
                .antMatchers("/", "/error", accountUrl, authUrl).permitAll()
                .anyRequest().authenticated();

    }

}

package jansen.tom.rps.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;

    private final static String authUrl = "/api/authentication";
    private final static String accountUrl = "/api/account";

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Allowing post call
        http.csrf().disable();

        http
                .authorizeRequests()
                // Allows anyone to access the following endpoints first
                .antMatchers(HttpMethod.POST, accountUrl, authUrl).permitAll()
                .antMatchers("/", "/error").permitAll().anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Intercept all requests as we put a filter before the spring security one
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }

}

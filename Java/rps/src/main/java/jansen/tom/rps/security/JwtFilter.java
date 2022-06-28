package jansen.tom.rps.security;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // We make sure that all the public paths are not filtered through
        if(!(path.equals("/api/authentication") && method.equals("POST")) &&
           !(path.equals("/api/account") && method.equals("POST")) &&
           !path.equals("/favicon.ico") &&
           !path.equals("/error") &&
           !path.equals("/"))
        {

            // Is for later use
            String email = null;
            String jwt = null;

            try {
                String bearerToken = request.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    jwt = bearerToken.substring(7);
                    email = jwtUtilities.extractUsername(jwt);
                }
            } catch (Exception error) {
                // Ignoring JWT errors
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<Account> foundAccount = accountRepository.findByEmailIgnoreCase(email);
                if (foundAccount.isPresent()) {
                    Account currentAccount = foundAccount.get();
                    if (jwtUtilities.validateToken(jwt, currentAccount)) {
                        if (currentAccount.getStatus() == Account.AccountStatus.VERIFIED) {
                            if (jwtUtilities.isTokenExpired(jwt)) {

                                // ...
                                System.out.println("The token should be refreshed...");
                                // ...

                            }
                            email = currentAccount.getEmail();
                            // We will allow the user of the json web token to use selected endpoint
                            var authentication = new UsernamePasswordAuthenticationToken(email, null, null);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

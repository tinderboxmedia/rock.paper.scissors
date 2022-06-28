package jansen.tom.rps.security;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.account.AccountRepository;
import jansen.tom.rps.account.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

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
                    Account account = foundAccount.get();
                    if (jwtUtilities.validateToken(jwt, account)) {
                        if (account.getStatus() == Account.AccountStatus.VERIFIED) {
                            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                            Set<Role> roles = account.getRoles();
                            email = account.getEmail();
                            for(Role role : roles) {
                                authorities.add(new SimpleGrantedAuthority(role.getName()));
                            }
                            var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

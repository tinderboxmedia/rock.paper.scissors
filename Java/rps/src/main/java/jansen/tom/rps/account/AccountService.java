package jansen.tom.rps.account;

import jansen.tom.rps.authentication.Authentication;
import jansen.tom.rps.authentication.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@PropertySource("classpath:authentication.properties")
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Value("${authentication.account.limit}")
    public String AUTH_ACCOUNT_REQUEST_LIMIT;

    @Value("${authentication.system.limit}")
    public String AUTH_SYSTEM_REQUEST_LIMIT;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account checkAccount(Account account) {
        String email = account.getEmail();
        Optional<Account> foundAccount = accountRepository.findByEmailIgnoreCase(email);
        if(foundAccount.isPresent()) {
            Account oldAccount = foundAccount.get();
            if (oldAccount.getStatus() != Account.AccountStatus.LOCKED) {
                authenticateAccount(oldAccount);
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "It seems like that this account may be locked.");
        }
        if(isValid(email)) {
            Account newAccount = new Account(email.toLowerCase());
            accountRepository.save(newAccount);
            authenticateAccount(newAccount);
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "This input is unfortunately not a valid email address.");
    }

    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z\\d_+&*-]+(?:\\." + "[a-zA-Z\\d_+&*-]+)*@"
                + "(?:[a-zA-Z\\d-]+\\.)+[a-z" + "A-Z]{2,15}$";
        if(email != null && !email.equals("")) {
            Pattern pattern = Pattern.compile(emailRegex);
            return pattern.matcher(email).matches();
        }
        // None valid
        return false;
    }

    private void authenticateAccount(Account account) {
        if(!accountMayAuthenticate(account)) {
            String authLimit = AUTH_ACCOUNT_REQUEST_LIMIT;
            String suffix = (authLimit.equals("1")) ? "" : "s";
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "You can send one authentication request every " + authLimit + " minute" + suffix + ".");
        }
        if(!systemMayAuthenticate()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "The authentication service is overloaded and not reachable.");
        }
        Authentication newAuthentication = new Authentication(account, uniqueToken());
        authenticationRepository.save(newAuthentication);

        // We now send the actual auth email and manage
        System.out.println(newAuthentication.getToken());
        // This exception should also be explored further
        throw new ResponseStatusException(HttpStatus.OK,
                "Success message. We still need to get SMTP feedback.");

    }

    private boolean systemMayAuthenticate() {
        Instant compareTime = Instant.now().minus(60, ChronoUnit.MINUTES);
        List<Authentication> entryList = authenticationRepository.findByCreationTimeGreaterThanEqual(compareTime);
        // Check if the system can manage all the authentication requests first
        return entryList.size() <= Integer.parseInt(AUTH_SYSTEM_REQUEST_LIMIT);
    }

    private boolean accountMayAuthenticate(Account account) {
        List<Authentication> entryList = authenticationRepository.findByAccountOrderByIdDesc(account);
        if(!entryList.isEmpty()) {
            // Check if an account did not reach their auth request limit
            Instant authCreationTime = entryList.get(0).getCreationTime();
            Instant compareTime = Instant.now().minus(Long.parseLong(AUTH_ACCOUNT_REQUEST_LIMIT), ChronoUnit.MINUTES);
            return authCreationTime.compareTo(compareTime) < 0;
        }
        return true;
    }

    private UUID uniqueToken() {
        // Check for possible duplicate
        UUID token = UUID.randomUUID();
        if(authenticationRepository.existsByToken(token)) {
            uniqueToken();
        }
        return token;
    }

}

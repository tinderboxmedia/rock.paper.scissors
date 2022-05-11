package jansen.tom.rps.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    private void authenticateAccount(Account account) {
        System.out.println("Authentication for " + account + " with identifier " + account.getId());
    }

    public Account checkAccount(Account account) {
        String email = account.getEmail();
        Optional<Account> foundAccount = accountRepository.findByEmailIgnoreCase(email);
        if(foundAccount.isPresent()) {
            // This account can be found thus get it
            authenticateAccount(foundAccount.get());
            throw new ResponseStatusException(HttpStatus.OK);
        }
        if(isValid(email)) {
            // This account is new and also valid thus create it
            Account newAccount = new Account(email.toLowerCase());
            accountRepository.save(newAccount);
            authenticateAccount(newAccount);
            return newAccount;
        }
        // Throw ambiguous exception if something is wrong with formatting
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z\\d_+&*-]+(?:\\." + "[a-zA-Z\\d_+&*-]+)*@"
                + "(?:[a-zA-Z\\d-]+\\.)+[a-z" + "A-Z]{2,15}$";
        if(email != null && !email.equals("")) {
            Pattern pattern = Pattern.compile(emailRegex);
            return pattern.matcher(email).matches();
        }
        // Not valid
        return false;
    }

}

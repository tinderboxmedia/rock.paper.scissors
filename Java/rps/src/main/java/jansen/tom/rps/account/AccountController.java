package jansen.tom.rps.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "admin")
    public String getAdmin() {
        return "I'm ADMIN";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "user")
    public String getUser() {
        return "I'm USER";
    }

    // Allow all
    @PostMapping
    public Account checkAccount(@RequestBody(required = false) String email) {
        return accountService.checkAccount(email);
    }

}

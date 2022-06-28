package jansen.tom.rps.account.role;

import jansen.tom.rps.account.Account;
import jansen.tom.rps.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AccountRepository accountRepository;

    public void saveRole(String name) {
        if(roleRepository.getRoleByNameIgnoreCase(name).isEmpty()) {
            roleRepository.save(new Role(name.toUpperCase()));
            Timestamp time = Timestamp.from(ZonedDateTime.now().toInstant());
            System.out.printf("[%s] The %s role is created. %n", time, name);
        }
    }

    public Boolean addRoleNameToAccountId(String name, Long id) {
        Optional<Role> optionalRole = roleRepository.getRoleByNameIgnoreCase(name);
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if(optionalRole.isPresent() && optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            Role role = optionalRole.get();
            Set<Role> accountRoles = account.getRoles();
            if (!accountRoles.contains(role)) {
                accountRoles.add(role);
                account.setRoles(accountRoles);
                accountRepository.save(account);
                // Role added
                return true;
            }
        }
        // No adding
        return false;
    }

}

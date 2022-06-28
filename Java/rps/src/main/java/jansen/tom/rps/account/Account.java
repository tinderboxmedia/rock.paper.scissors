package jansen.tom.rps.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jansen.tom.rps.account.role.Role;
import jansen.tom.rps.authentication.Authentication;

import javax.persistence.*;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@JsonIgnoreProperties({
        "authentications",
        "creationTime",
        "hash"
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_roles", inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private Timestamp creationTime;

    @Column(nullable = false)
    private String hash;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Authentication> authentications;

    // Deserialization
    public Account() {}

    public Account(String email, Role role) {
        this.email = email;
        this.status = AccountStatus.INACTIVE;
        this.creationTime = Timestamp.from(ZonedDateTime.now().toInstant());
        this.hash = getRandomNonce();
        this.roles.add(role);
    }

    public enum AccountStatus {
        INACTIVE, VERIFIED, LOCKED
    }

    private String getRandomNonce() {
        byte[] nonce = new byte[16];
        new SecureRandom().nextBytes(nonce);
        return bytesToHex(nonce);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}

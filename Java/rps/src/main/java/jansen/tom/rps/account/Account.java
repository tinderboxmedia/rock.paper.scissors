package jansen.tom.rps.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jansen.tom.rps.authentication.Authentication;

import javax.persistence.*;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

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

    @Column(nullable = false)
    private AccountRole role;

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

    public Account(String email) {
        this.email = email;
        this.role = AccountRole.USER;
        this.status = AccountStatus.INACTIVE;
        this.creationTime = Timestamp.from(ZonedDateTime.now().toInstant());
        this.hash = getRandomNonce();
    }

    public enum AccountStatus {
        INACTIVE, VERIFIED, LOCKED
    }

    public enum AccountRole {
        USER, ADMIN
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

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
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

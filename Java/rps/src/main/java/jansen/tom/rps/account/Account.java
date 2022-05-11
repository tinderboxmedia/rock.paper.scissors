package jansen.tom.rps.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jansen.tom.rps.authentication.Authentication;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@JsonIgnoreProperties("authentications")
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
    private Instant creationTime;

    @OneToMany(mappedBy="account")
    private List<Authentication> authentications;

    // Deserialization
    public Account() {}

    public Account(String email) {
        this.email = email;
        this.role = AccountRole.USER;
        this.status = AccountStatus.INACTIVE;
        this.creationTime = Instant.now();
    }

    public enum AccountStatus {
        INACTIVE, VERIFIED, LOCKED
    }

    public enum AccountRole {
        USER, ADMIN
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

    public Instant getCreationTime() {
        return creationTime;
    }

}

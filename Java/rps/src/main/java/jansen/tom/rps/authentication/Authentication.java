package jansen.tom.rps.authentication;

import jansen.tom.rps.account.Account;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Authentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID token;

    @Column(nullable = false)
    private Instant creationTime;

    @Column(nullable = false)
    private AuthenticationStatus status;

    @ManyToOne
    private Account account;

    // Deserialization
    public Authentication() {}

    public Authentication(Account account, UUID token) {
        this.token = token;
        this.creationTime = Instant.now();
        this.status = AuthenticationStatus.AWAIT;
        this.account = account;
    }

    public enum AuthenticationStatus {
        AWAIT, USED, EXPIRED
    }

    public Long getId() {
        return id;
    }

    public UUID getToken() {
        return token;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public AuthenticationStatus getStatus() {
        return status;
    }

    public void setStatus(AuthenticationStatus status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

}
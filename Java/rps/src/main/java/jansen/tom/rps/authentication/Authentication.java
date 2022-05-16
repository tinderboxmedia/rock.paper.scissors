package jansen.tom.rps.authentication;

import jansen.tom.rps.account.Account;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
public class Authentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID token;

    @Column(nullable = false)
    private Timestamp creationTime;

    @Column(nullable = false)
    private AuthenticationStatus status;

    @ManyToOne
    private Account account;

    // Deserialization
    public Authentication() {}

    public Authentication(Account account, UUID token) {
        this.token = token;
        this.creationTime = Timestamp.from(ZonedDateTime.now().toInstant());
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

    public Timestamp getCreationTime() {
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

package jansen.tom.rps.authorisation;

import jansen.tom.rps.account.Account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Authorisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Deserialization
    public Authorisation() {}

    public Authorisation(Account account) {

        // ...
        System.out.println("Authorisation(" + account + ") in Entity.");

    }

}
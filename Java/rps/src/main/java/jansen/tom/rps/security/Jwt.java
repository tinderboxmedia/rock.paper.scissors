package jansen.tom.rps.security;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class Jwt {

    private final Timestamp timestamp;
    private final String access;
    private final String refresh;

    public Jwt(String access, String refresh) {
        this.timestamp = Timestamp.from(ZonedDateTime.now().toInstant());
        this.access = access;
        this.refresh = refresh;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}

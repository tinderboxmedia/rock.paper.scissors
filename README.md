# Rock Paper Scissors
Rock Paper Scissors Game

## Contains

- Frontend in Angular
- Backend in Java

### Angular

`Empty`

### Java

**Properties**

Create and add an `application-restricted.properties` file into the main resource folder with the following content:

```
# Database
spring.datasource.url=url
spring.datasource.username=username
spring.datasource.password=password
```

**Database Connector**

Make sure to change `spring.datasource.driver-class-name` to the right driver. If you're running a MySQL database use `com.mysql.cj.jdbc.Driver` and if you're running a MariaDB database you should use `org.mariadb.jdbc.Driver` as the driver. If there is a discrepancy, it's be possible that timestamps inserted into the database omit their microseconds. These microseconds are used by the authentication checksums. Hence, double check to see if the database stores timestamps with these microseconds, else the user authentication will always fails.

**REST Response**

Authentication: If the `message` variable is present in the response, we may use that as user feedback right away. If this isn't the case on non-succesful exceptions however, it's an unexpected error and the user feedback must be generated and handled by the client side.
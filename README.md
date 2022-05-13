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
spring.datasource.url=url
spring.datasource.username=username
spring.datasource.password=password
```

**REST Response**

Authentication: If the `message` variable is present in the response, we may use that as user feedback right away. If this is not the case on non succesfull exceptions, it's an unexpected error and the user feedback must be generated on the client side.
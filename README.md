# Rock Paper Scissors
Rock Paper Scissors Game

## Contains

- Frontend in Angular
- Workers using Cloudflare
- Backend in Java

### Angular

`Empty`

### Cloudflare

**DKIM Keys**

We will be using Cloudflare Workers to create a script that we will use to send out the authentication mails. Cloudflare is pretty amazing as it will also be able to manage our DNS, domain and Angular project. Before we start creating the actual Worker, we will need to create a private and public DKIM key:

```
openssl genrsa 2048 | tee priv_key.pem | openssl rsa -outform der | openssl base64 -A >> private.txt
```

```
echo -n "v=DKIM1;p=" > public.txt && openssl rsa -in priv_key.pem -pubout -outform der | openssl base64 -A >> public.txt
```

You will end up with three files. Opening private.txt will provide you with the private key, public.txt will give you the DNS record. This DNS record should be added to the domain you plan on sending the authentication mails from.

**Cloudflare Worker**

The next step would be to create a Cloudflare Worker and add the `mail-api.js` script. You can do this by performing a Quick Edit. It is also important to create the following environment variables using their interface:

```
ACCESS_TOKEN	
DESTINATION_URL
DKIM_DOMAIN
DKIM_PRIVATE_KEY	
FROM_EMAIL
FROM_NAME
MAILCHANNELS_API
SERVICE_NAME
SUBJECT_EMAIL
```

Make sure to treat `ACCESS_TOKEN` and `DKIM_PRIVATE_KEY` as secrets. This will encrypt and secure them. Following `mail-api.js` should give you an idea on what the variable values should be. You can remove the three DKIM files if you added the DNS record and created the environment variables.

**Mailchannels**

For `MAILCHANNELS_API` one can use `https://api.mailchannels.net/tx/v1/send`. Thanks to their API we can use Cloudflare Workers in order to send out our authentication mails for free.

### Java

**Properties**

Create and add an `application-restricted.properties` file into the main resource folder with the following content:

```
# Database
spring.datasource.url=url
spring.datasource.username=username
spring.datasource.password=password

# API Access
sending.service.url=url
sending.service.access=token

# Json Web Token
jwt.secret.token=token
```

**Database Connector**

Make sure to change `spring.datasource.driver-class-name` to the right driver. For MySQL databases use `com.mysql.cj.jdbc.Driver` and `org.mariadb.jdbc.Driver` for MariaDB ones.

If there is a discrepancy, it's possible that the timestamps inserted into the database omit their microseconds. These microseconds are used by the authentication checksums. Hence, double check to see if the database stores timestamps with these microseconds, else the user authentication will always fails.

**REST Response**

Authentication: If the `message` variable is present in the response, we may use that as user feedback right away. If this isn't the case on non-succesful exceptions however, it's an unexpected error and the user feedback must be generated and handled by the client side.
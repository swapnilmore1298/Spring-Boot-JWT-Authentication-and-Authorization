# SpringBoot-JWT-Authentication-and-Authorization
Springboot 3.0 JWT auth understood

### Depenndencies

| Name                                                  | Version |
| ----------------------------------------------------- | ------- |
| spring-boot-starter-parent                            | 3.1.3   |
| spring-boot-starter-test                              | latest  |
|                                                       |         |
| spring-boot-starter-validation                        | latest  |
| spring-boot-starter-web                               | latest  |
|                                                       |         |
| mysql-connector-j                                     | latest  |
| spring-boot-starter-data-jpa                          | latest  |
|                                                       |         |
| spring-boot-starter-security                          | latest  |
|                                                       |         |
| springdoc-openapi-starter-webmvc-ui                   | 2.0.3   |
|                                                       |         |
| io.jsonwebtoken [jjwt-api / jjwt-impl / jjwt-jackson] | 0.11.5  |
|                                                       |         |
| slf4j-api                                             | 2.0.7   |

### In Memory Authentication


#### About

- Used for quick authentication with limited user and password(2-3 hardcoded)
- Can be updated later to start using Custom UserDetailService

#### Implementation

- Add dependency of `spring-boot-starter-security`
- by Default a username and password (Found in logs), will be auto implemented on application
- Also a Login Logout functionality is auto generated 
- To override this authentication, we create `MyConfig` class with `@Configuration`.
- Create 3 beans : userDetailsService, passwordEncoder and authenticationManager
- userDetailsService : Here use a User builder to generate users [mention username, password and roles]
- once generate return with `new InMemoryUserDetailsManager`

- Create a passwordEncoder bean returning `BCryptPasswordEncoder`.
- `authenticationManager` has injected configBuilder and return AuthManager.
  
```
@Bean
public UserDetailsService userDetailsService() {
    UserDetails userDetails = User.builder().
            username("DURGESH")
            .password(passwordEncoder().encode("DURGESH")).roles("ADMIN").
            build();
    return new InMemoryUserDetailsManager(userDetails);
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
    return builder.getAuthenticationManager();
}

```

## JWT authorisation

### Create security package and add following 3 classes 

#### JwtAuthenticationEntryPoint

**Code :**

```
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.println("Access Denied !! " + authException.getMessage());
        }
}
```

- Implement interface `AuthenticationEntryPoint`
- `JwtAuthenticationEntryPoint` is to handle unauthorized access attempts to your application. 
- When a user attempts to access a protected resource without proper authentication, this class sends an HTTP 401 (Unauthorized) response back to the client. 
- The response body contains a message indicating that access is denied, along with any relevant information from the `AuthenticationException`.

#### JwtHelper

- `JwtHelper` is a Spring component (@Component) that provides methods for working with JWTs in a Spring Security application.

- It defines a constant JWT_TOKEN_VALIDITY to specify the validity duration of JWT tokens in seconds (e.g., 5 hours).

- It uses a secret key (secret) for signing and verifying JWT tokens. This key should be kept secret.

- getUsernameFromToken and getExpirationDateFromToken methods extract information (subject and expiration date) from a JWT token.

- getClaimFromToken is a generic method to retrieve any claim from a JWT token using a function.

- getAllClaimsFromToken parses and extracts all claims from a JWT token.

- isTokenExpired checks if a JWT token has expired.

- generateToken creates a JWT token for a user with specified UserDetails.

- doGenerateToken generates a JWT token with claims, subject, issued date, expiration date, and signs it using the secret key.

- validateToken checks if a JWT token is valid by comparing the username from the token with the UserDetails and checking for token expiration.

- Overall, this class provides a convenient way to work with JWTs for user authentication and authorization in a Spring Security application. It's used to generate tokens during login and validate tokens during subsequent requests to protected resources.


**while creating the token -**
1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
2. Sign the JWT using the HS512 algorithm and secret key.
3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    compaction of the JWT to a URL-safe string


#### JWTAuthencticationFilter

- `JwtAuthenticationFilter` is a Spring component that extends `OncePerRequestFilter`, which ensures that the filter's `doFilterInternal` method is executed only once per HTTP request.

- Inside `doFilterInternal`, it extracts the "Authorization" header from the incoming HTTP request.

- If the header starts with "Bearer," it extracts the JWT token part.

- It attempts to extract the username from the token using the `JwtHelper` class, handling various exceptions that may occur during token processing.

- If a valid username is extracted and there is no existing authentication in the `SecurityContextHolder`, it fetches user details using the `UserDetailsService`.

- It validates the token using the `JwtHelper`, and if validation is successful, it sets the authentication details in the `SecurityContextHolder`.

- Finally, it continues processing the request by invoking the filterChain.`doFilter` method.

- This filter essentially intercepts incoming requests, extracts JWT tokens, and performs user authentication based on the tokens, if applicable. It integrates JWT-based authentication into a Spring Security application.

```

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        //Authorization

        String requestHeader = request.getHeader("Authorization");
        //Bearer 2352345235sdfrsfgsdfsdf
        logger.info(" Header :  {}", requestHeader);
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            //looking good
            token = requestHeader.substring(7);
            try {

                username = this.jwtHelper.getUsernameFromToken(token);

            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!");
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token !! Invalid Token");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }


        } else {
            logger.info("Invalid Header Value !! ");
        }


        //
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


            //fetch user detail from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {

                //set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);


            } else {
                logger.info("Validation fails !!");
            }


        }

        filterChain.doFilter(request, response);

    }
}

```
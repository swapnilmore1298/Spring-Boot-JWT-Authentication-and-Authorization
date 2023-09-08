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

- Create a passwordEncoder bean returning BCryptPasswordEncoder.
- authenticationManager has injected configBuilder and return AuthManager.
  
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
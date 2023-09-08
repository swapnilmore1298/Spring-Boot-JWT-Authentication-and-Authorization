package com.example.LoginRegistrationJWT.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JwtResponse {
    String jwtToken;
    String username;
}

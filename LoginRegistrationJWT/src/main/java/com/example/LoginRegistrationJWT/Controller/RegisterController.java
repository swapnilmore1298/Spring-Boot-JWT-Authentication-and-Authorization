package com.example.LoginRegistrationJWT.Controller;

import com.example.LoginRegistrationJWT.Model.JwtRequest;
import com.example.LoginRegistrationJWT.Model.JwtResponse;
import com.example.LoginRegistrationJWT.Security.JwtHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.example.LoginRegistrationJWT.DTO.UserDTO;
import com.example.LoginRegistrationJWT.Service.UserSvc;

import jakarta.validation.Valid;

@RestController
public class RegisterController {

    @Autowired
    UserSvc userSvc;
    
    @GetMapping("/health")
    public String healthCheck(){
        return "Backend working fine!!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO){
        if(!userSvc.isUserAlreadyExists(userDTO)){
          if(userSvc.newUser(userDTO)){
            return ResponseEntity.status(HttpStatus.CREATED)
            .body("User created Successfully!");
          }
          else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("User created Successfully!");
          }
        }
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("There is already an account registered with the same email");
        }
    }

    @GetMapping("getUsers")
    public ResponseEntity<?> getUserList(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userSvc.getUsers());
    }

    @RestController
    @RequestMapping("/auth")
    public static class AuthController {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private AuthenticationManager manager;


        @Autowired
        private JwtHelper helper;

        private Logger logger = LoggerFactory.getLogger(AuthController.class);


        @PostMapping("/login")
        public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

            this.doAuthenticate(request.getEmail(), request.getPassword());


            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = this.helper.generateToken(userDetails);

            JwtResponse response = JwtResponse.builder()
                    .jwtToken(token)
                    .username(userDetails.getUsername()).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        private void doAuthenticate(String email, String password) {

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
            try {
                manager.authenticate(authentication);


            } catch (BadCredentialsException e) {
                throw new BadCredentialsException(" Invalid Username or Password  !!");
            }

        }

        @ExceptionHandler(BadCredentialsException.class)
        public String exceptionHandler() {
            return "Credentials Invalid !!";
        }

    }
}


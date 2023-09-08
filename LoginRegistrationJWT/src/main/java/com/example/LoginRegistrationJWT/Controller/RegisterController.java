package com.example.LoginRegistrationJWT.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        if(!userSvc.findByEmail(userDTO)){
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
}

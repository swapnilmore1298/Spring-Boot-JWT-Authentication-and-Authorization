package com.example.LoginRegistrationJWT.Service;

import com.example.LoginRegistrationJWT.DTO.UserDTO;
import com.example.LoginRegistrationJWT.Entity.User;

import java.util.List;

public interface UserSvc {
    
    public boolean newUser(UserDTO userDTO);
    
    public String updateUser(UserDTO userDTO);

    public boolean isUserAlreadyExists(UserDTO userDTO);

    public List<UserDTO> getUsers();
 }

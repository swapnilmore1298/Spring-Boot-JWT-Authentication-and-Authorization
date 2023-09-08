package com.example.LoginRegistrationJWT.Service;

import com.example.LoginRegistrationJWT.DTO.UserDTO;

public interface UserSvc {
    
    public boolean newUser(UserDTO userDTO);
    
    public String updateUser(UserDTO userDTO);

    public boolean findByEmail(UserDTO userDTO);
}

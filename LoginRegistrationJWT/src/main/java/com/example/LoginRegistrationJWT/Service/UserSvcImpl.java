package com.example.LoginRegistrationJWT.Service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.LoginRegistrationJWT.DTO.UserDTO;
import com.example.LoginRegistrationJWT.Entity.Role;
import com.example.LoginRegistrationJWT.Entity.User;
import com.example.LoginRegistrationJWT.Repository.RoleRepo;
import com.example.LoginRegistrationJWT.Repository.UserRepo;

@Service
public class UserSvcImpl implements UserSvc {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Override
    public boolean newUser(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        Role role = roleRepo.findByName("user");
        
        if(role == null){
            role = new Role();
            role.setName("user");
            role.setDescription("Access restricted to Client User activities");
            role = roleRepo.save(role);
        }

        user.setRoles(Arrays.asList(role));
        User result = userRepo.save(user);
        
        if(result != null)
            return true;
        else
            return false;
    }

    @Override
    public String updateUser(UserDTO userDTO) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public boolean findByEmail(UserDTO userDTO) {
       try{
            User user = userRepo.findByEmail(userDTO.getEmail());
            if(user != null){
                return true;
            }
            else{
                return false;
            }
       }
       catch(Exception ex){
        return false;
       }
    }

}

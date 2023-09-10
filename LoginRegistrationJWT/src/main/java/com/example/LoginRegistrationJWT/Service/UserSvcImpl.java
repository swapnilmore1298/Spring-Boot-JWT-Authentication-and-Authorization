package com.example.LoginRegistrationJWT.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.LoginRegistrationJWT.DTO.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean newUser(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepo.findByName("user");
        
        if(role == null){
            role = new Role();
            role.setName("user");
            role.setDescription("Access restricted to Client User activities");
            role = roleRepo.save(role);
        }

        user.setRoles(List.of(role));
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
    public boolean isUserAlreadyExists(UserDTO userDTO) {
        return findByEmail(userDTO) != null;
    }


    public User findByEmail(UserDTO userDTO) {
       try{
            Optional<User> response = userRepo.findByEmail(userDTO.getEmail());
           return response.orElse(null);
       }
       catch(Exception ex){
        return null;
       }
    }

    @Override
    public List<UserDTO> getUsers(){
        List <UserDTO> userDTOList = new ArrayList<>();
        userRepo.findAll().forEach(user -> {
            userDTOList.add(UserMapper.entityToDTO(user));
        });
        return userDTOList;
    }



}

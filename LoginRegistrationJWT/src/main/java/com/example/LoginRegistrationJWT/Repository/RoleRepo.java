package com.example.LoginRegistrationJWT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LoginRegistrationJWT.Entity.Role;
import java.util.List;


@Repository
public interface RoleRepo extends JpaRepository <Role, Long>  {

    public Role findByName(String name);
}


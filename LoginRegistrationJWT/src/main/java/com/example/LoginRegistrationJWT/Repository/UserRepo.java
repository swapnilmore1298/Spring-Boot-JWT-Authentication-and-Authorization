package com.example.LoginRegistrationJWT.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LoginRegistrationJWT.Entity.User;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    public Optional<User> findByEmail(String email);
}

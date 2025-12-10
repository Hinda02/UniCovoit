package com.unicovoit.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicovoit.dao.UserAccountDao;
import com.unicovoit.dto.LoginRequestDto;
import com.unicovoit.dto.RegisterRequestDto;
import com.unicovoit.entity.Role;
import com.unicovoit.entity.UserAccount;

@Service
public class UserService {

    private final UserAccountDao userAccountDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserAccountDao userAccountDao,
                       PasswordEncoder passwordEncoder) {
        this.userAccountDao = userAccountDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount registerStudent(RegisterRequestDto dto) {

        // basic validation
        if (dto.getFirstName() == null || dto.getFirstName().isBlank()
                || dto.getLastName() == null || dto.getLastName().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getUniversity() == null || dto.getUniversity().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Tous les champs obligatoires doivent être remplis.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        if (userAccountDao.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée.");
        }

        UserAccount user = new UserAccount();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUniversity(dto.getUniversity());
        user.setRole(Role.STUDENT);               // default role
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        return userAccountDao.save(user);
    }
    
    @Transactional(readOnly = true)
    public UserAccount authenticate(LoginRequestDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email et mot de passe sont obligatoires.");
        }

        Optional<UserAccount> optionalUser = userAccountDao.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        UserAccount user = optionalUser.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        return user;
    }
    
}

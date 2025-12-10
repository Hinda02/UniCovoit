package com.unicovoit.service;

import java.util.Optional;

import com.unicovoit.exception.AuthenticationException;
import com.unicovoit.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicovoit.dao.UserAccountDao;
import com.unicovoit.dto.LoginRequestDto;
import com.unicovoit.dto.RegisterRequestDto;
import com.unicovoit.entity.Role;
import com.unicovoit.entity.UserAccount;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class UserService {

    private final UserAccountDao userAccountDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserAccountDao userAccountDao,
                       PasswordEncoder passwordEncoder) {
        this.userAccountDao = userAccountDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount registerStudent(@Valid RegisterRequestDto dto) {

        // Check password confirmation
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ValidationException("Les mots de passe ne correspondent pas.");
        }

        // Check if email already exists
        if (userAccountDao.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Cette adresse email est déjà utilisée.");
        }

        UserAccount user = new UserAccount();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUniversity(dto.getUniversity());
        user.setRole(Role.STUDENT); // default role
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        return userAccountDao.save(user);
    }

    @Transactional(readOnly = true)
    public UserAccount authenticate(@Valid LoginRequestDto dto) {
        Optional<UserAccount> optionalUser = userAccountDao.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new AuthenticationException("Email ou mot de passe incorrect.");
        }

        UserAccount user = optionalUser.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Email ou mot de passe incorrect.");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UserAccount getUserById(Long userId) {
        return userAccountDao.findById(userId)
                .orElseThrow(() -> new ValidationException("Utilisateur non trouvé."));
    }

    @Transactional(readOnly = true)
    public UserAccount getUserByEmail(String email) {
        return userAccountDao.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Utilisateur non trouvé."));
    }
}

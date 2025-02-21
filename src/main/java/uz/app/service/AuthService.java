package uz.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.app.config.JwtProvider;
import uz.app.dto.LoginDTO;
import uz.app.dto.SignUpDTO;
import uz.app.entity.User;
import uz.app.entity.enums.Role;
import uz.app.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public User registerUser(SignUpDTO signUpDTO) {
        if (userRepository.existsByPhoneNumber(signUpDTO.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use");
        }

        User user = User.builder()
                .firstName(signUpDTO.getFirstName())
                .lastName(signUpDTO.getLastName())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .phoneNumber(signUpDTO.getPhoneNumber())
                .birthYear(signUpDTO.getBirthYear())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .enabled(false)
                .build();

        return userRepository.save(user);
    }

    public String loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByPhoneNumber(loginDTO.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.save(user);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return jwtProvider.generateToken(user);
    }

}

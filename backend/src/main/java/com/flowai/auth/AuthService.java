package com.flowai.auth;

import com.flowai.auth.dto.AuthResponse;
import com.flowai.auth.dto.LoginRequest;
import com.flowai.auth.dto.RegisterRequest;
import com.flowai.config.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication business logic.
 *
 * SECURITY RULES enforced here:
 * - Passwords are NEVER stored in plaintext.
 * - Passwords are NEVER logged.
 * - Duplicate email registrations are rejected.
 * - Login uses Spring's AuthenticationManager — it handles
 *   bad credentials internally with a proper exception.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        // Build user entity — password is hashed before saving
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))  // BCrypt hash
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());  // Log email, NEVER password

        String token = jwtService.generateToken(savedUser);
        return AuthResponse.of(token, savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager handles bad credentials — throws AuthenticationException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // If we reach here, credentials were valid
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        log.info("User logged in: {}", user.getEmail());  // Log email, NEVER password

        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, user);
    }
}
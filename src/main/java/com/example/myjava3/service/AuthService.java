package com.example.myjava3.service;

import com.example.myjava3.exception.RefreshReuseException;
import com.example.myjava3.model.SessionStatus;
import com.example.myjava3.model.User;
import com.example.myjava3.model.UserSession;
import com.example.myjava3.repository.UserRepo;
import com.example.myjava3.repository.UserSessionRepository;
import com.example.myjava3.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final UserSessionRepository sessionRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepo userRepo, UserSessionRepository sessionRepo,
                       JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Map<String, String> login(String email, String password, String deviceId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bad credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Bad credentials");
        }
        return issuePair(user, deviceId);
    }

    @Transactional
    public Map<String, String> register(User newUser, String deviceId) {
        if (userRepo.findByEmail(newUser.getEmail()).isPresent()) {
            throw new RuntimeException("Conflict");
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepo.save(newUser);
        return issuePair(newUser, deviceId);
    }

    private Map<String, String> issuePair(User user, String deviceId) {
        String access = jwtService.generateAccessToken(user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getEmail());

        UserSession session = new UserSession();
        session.setEmail(user.getEmail());
        session.setDeviceId(deviceId);
        session.setAccessToken(access);
        session.setRefreshToken(refresh);
        session.setAccessTokenExpiry(jwtService.getExpiration(access));
        session.setRefreshTokenExpiry(jwtService.getExpiration(refresh));
        session.setStatus(SessionStatus.ACTIVE);
        sessionRepo.save(session);

        return Map.of("accessToken", access, "refreshToken", refresh);
    }

    @Transactional(noRollbackFor = RefreshReuseException.class)
    public Map<String, String> refresh(String refreshToken) {
        var claims = jwtService.parse(refreshToken);
        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new RuntimeException("Bad token type");
        }
        String email = claims.getSubject();

        UserSession session = sessionRepo.findByRefreshTokenForUpdate(refreshToken)
                .orElseThrow(() -> new RuntimeException("Unknown refresh"));

        if (!session.getEmail().equals(email)) throw new RuntimeException("Owner mismatch");
        if (session.getRefreshTokenExpiry().isBefore(Instant.now())) throw new RuntimeException("Expired");
        if (session.getStatus() == SessionStatus.REVOKED) throw new RuntimeException("Revoked");

        if (session.getStatus() == SessionStatus.USED) {
            session.setStatus(SessionStatus.REVOKED);
            sessionRepo.save(session);
            throw new RefreshReuseException("Reuse detected");
        }

        session.setStatus(SessionStatus.USED);
        sessionRepo.save(session);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String access = jwtService.generateAccessToken(user.getEmail());
        String newRefresh = jwtService.generateRefreshToken(user.getEmail());

        UserSession newSession = new UserSession();
        newSession.setEmail(user.getEmail());
        newSession.setDeviceId(session.getDeviceId());
        newSession.setAccessToken(access);
        newSession.setRefreshToken(newRefresh);
        newSession.setAccessTokenExpiry(jwtService.getExpiration(access));
        newSession.setRefreshTokenExpiry(jwtService.getExpiration(newRefresh));
        newSession.setStatus(SessionStatus.ACTIVE);
        sessionRepo.save(newSession);

        return Map.of("accessToken", access, "refreshToken", newRefresh);
    }
}
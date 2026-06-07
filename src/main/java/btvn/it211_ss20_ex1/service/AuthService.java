package btvn.it211_ss20_ex1.service;

import btvn.it211_ss20_ex1.dto.*;
import btvn.it211_ss20_ex1.entity.*;
import btvn.it211_ss20_ex1.repository.*;
import btvn.it211_ss20_ex1.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        String accessToken = jwtService.generateAccessToken(employee);
        String refreshToken = jwtService.generateRefreshToken(employee);

        saveToken(employee, accessToken, TokenType.ACCESS);
        saveToken(employee, refreshToken, TokenType.REFRESH);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        Token storedRefreshToken = tokenRepository
                .findByTokenValueAndTokenTypeAndRevokedFalseAndExpiredFalse(
                        request.getRefreshToken(),
                        TokenType.REFRESH
                )
                .orElseThrow(() -> new RuntimeException("Refresh Token không hợp lệ"));

        String username = jwtService.extractUsername(request.getRefreshToken());

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        String newAccessToken = jwtService.generateAccessToken(employee);

        saveToken(employee, newAccessToken, TokenType.ACCESS);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }

    public void logout(String accessToken) {
        String username = jwtService.extractUsername(accessToken);

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        tokenRepository.findAllByEmployeeAndRevokedFalseAndExpiredFalse(employee)
                .stream()
                .peek(token -> {
                    token.setRevoked(true);
                    token.setExpired(true);
                })
                .forEach(tokenRepository::save);

        SecurityContextHolder.clearContext();
    }

    private void saveToken(Employee employee, String tokenValue, TokenType tokenType) {
        Token token = Token.builder()
                .employee(employee)
                .tokenValue(tokenValue)
                .tokenType(tokenType)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }
}
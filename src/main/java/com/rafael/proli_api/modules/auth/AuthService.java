package com.rafael.proli_api.modules.auth;

import com.rafael.proli_api.core.security.TokenService;
import com.rafael.proli_api.modules.auth.dto.AuthResponseDTO;
import com.rafael.proli_api.modules.auth.dto.LoginRequestDTO;
import com.rafael.proli_api.modules.auth.dto.UserDTO;
import com.rafael.proli_api.modules.user.User;
import com.rafael.proli_api.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthResponseDTO authenticate(LoginRequestDTO loginDTO) {
        // 1. Busca o usuário pelo e-mail
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas."));

        // 2. Compara a senha digitada com o Hash do banco de dados
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas.");
        }

        // 3. Gera os tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        // 4. Monta o DTO do usuário
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()))
                .build();

        // 5. Retornamos o Access Token e o User (o Refresh Token será gerenciado pelo Controller no Cookie)
        // Usaremos um truque no Controller para extrair o Refresh Token
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .user(userDTO)
                .build();
    }

    // Método auxiliar exclusivo para pegar o refresh token na hora do login
    public String generateRefresh(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return tokenService.generateRefreshToken(user);
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        // Valida o token e extrai o e-mail
        String email = tokenService.validateTokenAndGetSubject(refreshToken);
        if (email == null) {
            throw new RuntimeException("Refresh token inválido ou expirado.");
        }
        // Busca o usuário e gera um NOVO Access Token
        User user = userRepository.findByEmail(email).orElseThrow();
        String newAccessToken = tokenService.generateAccessToken(user);

        return AuthResponseDTO.builder().accessToken(newAccessToken).build();
    }
}
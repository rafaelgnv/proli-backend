package com.rafael.proli_api.modules.auth;

import com.rafael.proli_api.modules.auth.dto.AuthResponseDTO;
import com.rafael.proli_api.modules.auth.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {

        // 1. Autentica e gera o Access Token
        AuthResponseDTO response = authService.authenticate(loginDTO);

        // 2. Gera o Refresh Token
        String refreshToken = authService.generateRefresh(loginDTO.getEmail());

        // 3. Cria o Cookie HTTP-Only e Secure (Seguro contra XSS)
        ResponseCookie springCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // Mude para TRUE em produção (quando tiver HTTPS)
                .path("/api/auth/refresh") // O cookie só será enviado nesta rota
                .maxAge(7 * 24 * 60 * 60) // 7 dias em segundos
                .sameSite("Strict")
                .build();

        // 4. Retorna a resposta com o cookie no Header
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(response);
    }

    // Endpoint de logout para limpar o cookie do navegador
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // TRUE em prod
                .path("/api/auth/refresh")
                .maxAge(0) // Expira imediatamente
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            AuthResponseDTO response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

}
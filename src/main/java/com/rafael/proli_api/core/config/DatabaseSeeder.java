package com.rafael.proli_api.core.config;

import com.rafael.proli_api.modules.user.Role;
import com.rafael.proli_api.modules.user.RoleRepository;
import com.rafael.proli_api.modules.user.User;
import com.rafael.proli_api.modules.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeta os valores definidos no application.properties / Variáveis de Ambiente
    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. Garante que os papéis fundamentais do RBAC existem no banco
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

        // 2. Verifica se o usuário root/admin já foi criado
        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            // Instancia o usuário e criptografa a senha antes de salvar
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));

            // O Admin herda as permissões de acesso de usuário comum e de administrador
            admin.setRoles(Set.of(roleUser, roleAdmin));

            userRepository.save(admin);

            System.out.println("=======================================================");
            System.out.println("✅ Database Seeder: Usuário administrador criado.");
            System.out.println("✅ E-mail de acesso: " + adminEmail);
            System.out.println("=======================================================");
        } else {
            System.out.println("✅ Database Seeder: Usuário administrador já existe.");
        }
    }
}
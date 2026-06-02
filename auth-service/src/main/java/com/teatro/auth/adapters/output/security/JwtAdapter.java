package com.teatro.auth.adapters.output.security;

import com.teatro.auth.domain.model.User;
import com.teatro.auth.ports.output.TokenServicePort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; //  ADICIONE
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtAdapter implements TokenServicePort {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public String generateToken(User user) {
        try {
            // Converte a String secreta em uma SecretKey segura exigida pelo JJWT 0.12+
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            long tempoExpiracaoMillis = 7200000; // 2 horas em milissegundos

            return Jwts.builder()
                    .issuer("auth-api")
                    .subject(user.getEmail())
                    .claim("role", user.getRole().name()) // Salva a ROLE do usuário dentro do token
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + tempoExpiracaoMillis))
                    .signWith(key) // Assina digitalmente usando a chave gerada
                    .compact(); // Gera a String final do JWT

        } catch (Exception exception) {
            throw new RuntimeException("Erro ao gerar o token JWT usando JJWT", exception);
        }
    }
}

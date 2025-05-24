package com.example.edu.eci.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET = "EPRiC0Bt0/2KcBRRWqVKhEWzModEtI6Q4K05RWuLgVQV4Xw92Ulk9kHPmQVjiRW5c9XtLNm4lgNoridiLgvZpg==";
    private static final SignatureAlgorithm ALGORITMO = SignatureAlgorithm.HS512; // Asegura HS512

    private final SecretKey key = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8) // Charset explícito
    );

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // Usa directamente la SecretKey
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.err.println("Token expirado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token no soportado: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.err.println("Token mal formado: " + ex.getMessage());
        } catch (SignatureException ex) {
            System.err.println("Firma inválida: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("Token vacío o nulo: " + ex.getMessage());
        }
        return false;
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


}
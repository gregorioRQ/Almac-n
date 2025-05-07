package com.pola.api_inventario.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.pola.api_inventario.rest.models.User;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final String secretKey;

    public JwtUtil() {
        Dotenv dotenv = Dotenv.load();
        this.secretKey = dotenv.get("JWT_SECRET_KEY");

        if (secretKey == null) {
            throw new RuntimeException("JWT_SECRET no encontrado en .env");
        }
    }

    public String getToken(UserDetails user) {
        // Extraer los roles del usuario
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Extraer el nombre del rol
                .toList()); // Convertir a lista
        // extrae el departamento de logistica al que esta vinculado el usuario.
        // Verificar si el usuario tiene el rol LOGISTICA antes de añadir el
        // departamento
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("LOGISTICA"))) {
            if (user instanceof User) {
                String departamento = ((User) user).getLogistica().getDepartamento();
                if (departamento != null) { // Asegurarse de que el departamento no sea nulo
                    extraClaims.put("department", departamento);
                }
            }
        }

        return getToken(extraClaims, user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3)) // 3 días
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> getRolesFromToken(String token) {
        return getClaim(token, claims -> claims.get("roles", List.class));
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}

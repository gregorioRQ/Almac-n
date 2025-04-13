package com.pola.api_inventario.rest.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.rest.models.AuthResponse;
import com.pola.api_inventario.rest.models.LoginRequest;
import com.pola.api_inventario.rest.models.RegisterRequest;
import com.pola.api_inventario.rest.models.Role;
import com.pola.api_inventario.rest.models.User;
import com.pola.api_inventario.rest.repositorio.IuserDao;
import com.pola.api_inventario.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
        private final IuserDao userDao;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;

        public AuthResponse login(LoginRequest request) {
                authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                                                request.getPassword()));

                UserDetails userDetails = userDao.findByUsername(request.getUsername());

                String token = jwtUtil.getToken(userDetails);
                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        public AuthResponse register(RegisterRequest request) {
                User user = User.builder()
                                .telefono(request.getTelefono())
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .firstName(request.getFirstName())
                                .email(request.getEmail())
                                .role(Role.USER)
                                .build();

                userDao.save(user);
                return AuthResponse.builder()
                                .token(jwtUtil.getToken(user))
                                .build();

        }
}

package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.LoginRequestDto;
import com.petsalud.backendpetsalud.dto.LoginResponseDto;
import com.petsalud.backendpetsalud.security.JwtUtil;
import com.petsalud.backendpetsalud.security.UsuarioDetails;
import com.petsalud.backendpetsalud.security.UsuarioDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // Autenticar credenciales del usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales invalidas");
        }

        // Cargar detalles del usuario autenticado
        UsuarioDetails userDetails = (UsuarioDetails) usuarioDetailsService.loadUserByUsername(loginRequest.getEmail());
        String rol = userDetails.getUsuario().getRol().name();

        // Generar token JWT con el rol asignado
        String token = jwtUtil.generateToken(userDetails, rol);

        LoginResponseDto response = LoginResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .email(userDetails.getUsername())
                .rol(rol)
                .build();

        return ResponseEntity.ok(response);
    }
}

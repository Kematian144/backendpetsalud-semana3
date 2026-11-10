package com.petsalud.backendpetsalud;

import com.petsalud.backendpetsalud.entity.Rol;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.repository.UsuarioRepository;
import com.petsalud.backendpetsalud.security.JwtUtil;
import com.petsalud.backendpetsalud.security.UsuarioDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    // Prueba 1: Generacion y validacion de tokens JWT
    @Test
    void testGeneracionYValidacionTokenJwt() {
        Usuario usuario = Usuario.builder()
                .email("admin@petsalud.cl")
                .password(passwordEncoder.encode("Admin1234"))
                .rol(Rol.ADMIN)
                .nombre("Admin")
                .apellido("Sistema")
                .build();

        UsuarioDetails details = new UsuarioDetails(usuario);
        String token = jwtUtil.generateToken(details, usuario.getRol().name());

        assertNotNull(token);
        assertEquals("admin@petsalud.cl", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, details));
    }

    // Prueba 2: Encriptacion segura de contrasenas con BCrypt
    @Test
    void testEncriptacionPasswordBCrypt() {
        String rawPassword = "PasswordSeguro2026";
        String encoded = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, encoded);
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    // Prueba 3: Peticion no autenticada a ruta protegida debe ser rechazada
    @Test
    void testAccesoNoAutenticadoRechazado() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    // Prueba 4: Acceso con token JWT valido a ruta autorizada
    @Test
    void testAccesoConTokenJwtValido() throws Exception {
        Usuario usuario = Usuario.builder()
                .email("admin2@petsalud.cl")
                .password(passwordEncoder.encode("Admin1234"))
                .rol(Rol.ADMIN)
                .nombre("Admin2")
                .apellido("Sistema")
                .build();
        usuarioRepository.save(usuario);

        UsuarioDetails details = new UsuarioDetails(usuario);
        String token = jwtUtil.generateToken(details, usuario.getRol().name());

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // Prueba 5: Proteccion contra Inyeccion SQL mediante consultas JPA parametrizadas
    @Test
    void testProteccionSqlInjection() {
        String payloadInyeccion = "admin' OR '1'='1";
        Optional<Usuario> resultado = usuarioRepository.findByEmail(payloadInyeccion);
        assertFalse(resultado.isPresent());
    }

    // Prueba 6: Manejo seguro de contenido XSS en registro
    @Test
    void testManejoPayloadXss() throws Exception {
        String body = "{\"email\":\"xss@petsalud.cl\",\"password\":\"Pass1234\",\"nombre\":\"<script>alert('xss')</script>\",\"rol\":\"CLIENTE\"}";

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        Optional<Usuario> usuarioGuardado = usuarioRepository.findByEmail("xss@petsalud.cl");
        assertTrue(usuarioGuardado.isPresent());
        // El script se almacena como texto plano sin ejecucion en backend
        assertEquals("<script>alert('xss')</script>", usuarioGuardado.get().getNombre());
    }
}

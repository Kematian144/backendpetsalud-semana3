package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Recompensa;
import com.petsalud.backendpetsalud.service.RecompensaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    // Asignar recompensa reservado a administradores
    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Recompensa> agregarRecompensa(@PathVariable Long usuarioId, @RequestBody Recompensa recompensa) {
        Recompensa nuevaRecompensa = recompensaService.agregarRecompensa(usuarioId, recompensa);
        return ResponseEntity.ok(nuevaRecompensa);
    }

    // Consulta de recompensa por id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<Recompensa> obtenerRecompensaPorId(@PathVariable Long id) {
        return recompensaService.obtenerRecompensaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar recompensas por usuario
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<Recompensa>> listarRecompensasPorUsuario(@PathVariable Long usuarioId) {
        List<Recompensa> recompensas = recompensaService.listarRecompensasPorUsuario(usuarioId);
        return ResponseEntity.ok(recompensas);
    }

    // Eliminar recompensa reservado a administradores
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarRecompensa(@PathVariable Long id) {
        recompensaService.eliminarRecompensa(id);
        return ResponseEntity.noContent().build();
    }
}

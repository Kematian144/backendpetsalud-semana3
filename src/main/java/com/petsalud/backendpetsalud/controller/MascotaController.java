package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Mascota;
import com.petsalud.backendpetsalud.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    // Registro de mascota permitido para clientes y administradores
    @PostMapping("/propietario/{propietarioId}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<Mascota> registrarMascota(@PathVariable Long propietarioId, @RequestBody Mascota mascota) {
        Mascota nuevaMascota = mascotaService.registrarMascota(mascota, propietarioId);
        return ResponseEntity.ok(nuevaMascota);
    }

    // Consulta de datos de mascota
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<Mascota> obtenerMascotaPorId(@PathVariable Long id) {
        return mascotaService.obtenerMascotaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizacion de datos de mascota
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<Mascota> actualizarMascota(@PathVariable Long id, @RequestBody Mascota mascota) {
        Mascota mascotaActualizada = mascotaService.actualizarMascota(id, mascota);
        return ResponseEntity.ok(mascotaActualizada);
    }

    // Eliminacion de mascota restringida a administradores
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }

    // Listado de mascotas por propietario
    @GetMapping("/propietario/{propietarioId}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<List<Mascota>> listarMascotasPorPropietario(@PathVariable Long propietarioId) {
        List<Mascota> mascotas = mascotaService.listarMascotasPorPropietario(propietarioId);
        return ResponseEntity.ok(mascotas);
    }
}

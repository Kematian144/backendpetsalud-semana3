package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Cita;
import com.petsalud.backendpetsalud.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    // Agendar cita permitido para clientes, veterinarios y administradores
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<Cita> agendarCita(
            @RequestParam Long mascotaId,
            @RequestParam Long veterinarioId,
            @RequestParam String fechaHora,
            @RequestParam String motivo) {
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora);
        Cita nuevaCita = citaService.agendarCita(mascotaId, veterinarioId, fechaHoraParsed, motivo);
        return ResponseEntity.ok(nuevaCita);
    }

    // Consulta de cita por id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<Cita> obtenerCitaPorId(@PathVariable Long id) {
        return citaService.obtenerCitaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizacion de estado solo por personal clinico o administracion
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('VETERINARIO', 'ADMIN')")
    public ResponseEntity<Cita> actualizarEstadoCita(@PathVariable Long id, @RequestParam String estado) {
        Cita citaActualizada = citaService.actualizarEstadoCita(id, estado);
        return ResponseEntity.ok(citaActualizada);
    }

    // Cancelar cita permitido para usuarios involucrados y admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        citaService.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    // Listar citas por mascota
    @GetMapping("/mascota/{mascotaId}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<List<Cita>> listarCitasPorMascota(@PathVariable Long mascotaId) {
        List<Cita> citas = citaService.listarCitasPorMascota(mascotaId);
        return ResponseEntity.ok(citas);
    }

    // Listar citas de veterinario especifico
    @GetMapping("/veterinario/{veterinarioId}")
    @PreAuthorize("hasAnyAuthority('VETERINARIO', 'ADMIN')")
    public ResponseEntity<List<Cita>> listarCitasPorVeterinario(@PathVariable Long veterinarioId) {
        List<Cita> citas = citaService.listarCitasPorVeterinario(veterinarioId);
        return ResponseEntity.ok(citas);
    }

    // Listar citas entre fechas para coordinacion clinica
    @GetMapping("/entre-fechas")
    @PreAuthorize("hasAnyAuthority('VETERINARIO', 'ADMIN')")
    public ResponseEntity<List<Cita>> listarCitasEntreFechas(
            @RequestParam String inicio,
            @RequestParam String fin) {
        LocalDateTime inicioParsed = LocalDateTime.parse(inicio);
        LocalDateTime finParsed = LocalDateTime.parse(fin);
        List<Cita> citas = citaService.listarCitasEntreFechas(inicioParsed, finParsed);
        return ResponseEntity.ok(citas);
    }
}

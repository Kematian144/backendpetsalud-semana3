package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Notificacion;
import com.petsalud.backendpetsalud.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    // Enviar notificaciones reservado a administradores
    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Notificacion> enviarNotificacion(@PathVariable Long usuarioId, @RequestBody Notificacion notificacion) {
        Notificacion nuevaNotificacion = notificacionService.enviarNotificacion(usuarioId, notificacion);
        return ResponseEntity.ok(nuevaNotificacion);
    }

    // Consulta de notificacion especifica
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<Notificacion> obtenerNotificacionPorId(@PathVariable Long id) {
        return notificacionService.obtenerNotificacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar notificaciones por usuario
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<Notificacion>> listarNotificacionesPorUsuario(@PathVariable Long usuarioId) {
        List<Notificacion> notificaciones = notificacionService.listarNotificacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    // Listar notificaciones no leidas
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<Notificacion>> listarNotificacionesNoLeidas(@PathVariable Long usuarioId) {
        List<Notificacion> notificaciones = notificacionService.listarNotificacionesNoLeidas(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    // Marcar notificacion como leida
    @PutMapping("/{id}/leida")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar notificacion
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}

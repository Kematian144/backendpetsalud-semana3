package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Producto;
import com.petsalud.backendpetsalud.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Solo administradores pueden agregar productos al inventario
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Producto> agregarProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.agregarProducto(producto);
        return ResponseEntity.ok(nuevoProducto);
    }

    // Consulta de producto disponible para usuarios autenticados
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo administradores pueden modificar productos
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(productoActualizado);
    }

    // Solo administradores pueden eliminar productos del catalogo
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // Catalogo visible para cualquier rol autenticado
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    // Filtro por categoria visible para usuarios autenticados
    @GetMapping("/categoria")
    @PreAuthorize("hasAnyAuthority('CLIENTE', 'VETERINARIO', 'ADMIN')")
    public ResponseEntity<List<Producto>> listarProductosPorCategoria(@RequestParam String categoria) {
        List<Producto> productos = productoService.listarProductosPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    // Control de stock bajo reservado a administradores
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Producto>> listarProductosConStockBajo(@RequestParam Integer cantidad) {
        List<Producto> productos = productoService.listarProductosConStockBajo(cantidad);
        return ResponseEntity.ok(productos);
    }
}

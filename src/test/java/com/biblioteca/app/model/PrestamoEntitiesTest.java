package com.biblioteca.app.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.biblioteca.app.entity.Usuario;

class PrestamoEntitiesTest {

    @Test
    void deberiaCrearComprobanteCorrectamente() {
        Comprobante comprobante = new Comprobante();

        comprobante.setIdComprobante(1);
        comprobante.setTipo("Boleta");

        assertEquals(1, comprobante.getIdComprobante());
        assertEquals("Boleta", comprobante.getTipo());
    }

    @Test
    void deberiaCrearPrestamoCorrectamente() {
        Usuario usuario = new Usuario();

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(1);
        comprobante.setTipo("Boleta");

        Prestamo prestamo = new Prestamo();

        LocalDateTime fechaPrestamo = LocalDateTime.now();

        prestamo.setIdPrestamo(1);
        prestamo.setUsuario(usuario);
        prestamo.setComprobante(comprobante);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setEstado("abierto");
        prestamo.setSerie("B001");
        prestamo.setNumero("000001");
        prestamo.setImpuesto(new BigDecimal("18.00"));
        prestamo.setMontoTotal(new BigDecimal("100.00"));
        prestamo.setObservaciones("Préstamo de prueba");

        assertEquals(1, prestamo.getIdPrestamo());
        assertSame(usuario, prestamo.getUsuario());
        assertSame(comprobante, prestamo.getComprobante());
        assertEquals(fechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals("abierto", prestamo.getEstado());
        assertEquals("B001", prestamo.getSerie());
        assertEquals("000001", prestamo.getNumero());
        assertEquals(new BigDecimal("18.00"), prestamo.getImpuesto());
        assertEquals(new BigDecimal("100.00"), prestamo.getMontoTotal());
        assertEquals("Préstamo de prueba", prestamo.getObservaciones());
    }

    @Test
    void deberiaTenerValoresInicialesEnPrestamo() {
        Prestamo prestamo = new Prestamo();

        assertEquals("abierto", prestamo.getEstado());
        assertEquals(BigDecimal.ZERO, prestamo.getImpuesto());
        assertEquals(BigDecimal.ZERO, prestamo.getMontoTotal());
    }

    @Test
    void deberiaCrearDetallePrestamoCorrectamente() {
        Prestamo prestamo = new Prestamo();
        Libro libro = new Libro();

        DetallePrestamo detalle = new DetallePrestamo();

        LocalDate fechaCompromiso = LocalDate.now().plusDays(7);
        LocalDate fechaDevolucion = LocalDate.now();

        detalle.setIdDetallePrestamo(1);
        detalle.setPrestamo(prestamo);
        detalle.setLibro(libro);
        detalle.setPrecioTiempo(new BigDecimal("5.00"));
        detalle.setTiempo(7);
        detalle.setFechaCompromiso(fechaCompromiso);
        detalle.setFechaDevolucion(fechaDevolucion);
        detalle.setEstado("devuelto");
        detalle.setSubtotal(new BigDecimal("35.00"));

        assertEquals(1, detalle.getIdDetallePrestamo());
        assertSame(prestamo, detalle.getPrestamo());
        assertSame(libro, detalle.getLibro());
        assertEquals(new BigDecimal("5.00"), detalle.getPrecioTiempo());
        assertEquals(7, detalle.getTiempo());
        assertEquals(fechaCompromiso, detalle.getFechaCompromiso());
        assertEquals(fechaDevolucion, detalle.getFechaDevolucion());
        assertEquals("devuelto", detalle.getEstado());
        assertEquals(new BigDecimal("35.00"), detalle.getSubtotal());
    }

    @Test
    void deberiaTenerValoresInicialesEnDetallePrestamo() {
        DetallePrestamo detalle = new DetallePrestamo();

        assertEquals("prestado", detalle.getEstado());
        assertEquals(1, detalle.getTiempo());
        assertEquals(BigDecimal.ZERO, detalle.getPrecioTiempo());
        assertEquals(BigDecimal.ZERO, detalle.getSubtotal());
    }
}
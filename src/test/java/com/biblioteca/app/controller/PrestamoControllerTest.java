package com.biblioteca.app.controller;

import com.biblioteca.app.dto.NuevoPrestamoDTO;
import com.biblioteca.app.entity.Usuario;
import com.biblioteca.app.model.Comprobante;
import com.biblioteca.app.model.DetallePrestamo;
import com.biblioteca.app.model.Libro;
import com.biblioteca.app.model.Prestamo;
import com.biblioteca.app.service.PrestamoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PrestamoControllerTest {

    @Mock
    private PrestamoService prestamoService;

    @InjectMocks
    private PrestamoController prestamoController;

    private MockMvc mockMvc;
    private Prestamo prestamo;
    private DetallePrestamo detalle;
    private Comprobante comprobante;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(prestamoController)
                .build();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombres("Jhordy");
        usuario.setApellidos("Gala");

        comprobante = new Comprobante();
        comprobante.setIdComprobante(1);
        comprobante.setTipo("Recibo");

        prestamo = new Prestamo();
        prestamo.setIdPrestamo(20);
        prestamo.setUsuario(usuario);
        prestamo.setComprobante(comprobante);
        prestamo.setFechaPrestamo(LocalDateTime.of(2026, 11, 9, 20, 0));
        prestamo.setEstado("abierto");
        prestamo.setSerie("R-2026");
        prestamo.setNumero("000001");
        prestamo.setImpuesto(BigDecimal.ZERO);
        prestamo.setMontoTotal(new BigDecimal("7.50"));
        prestamo.setObservaciones("Préstamo de prueba");

        Libro libro = new Libro();
        libro.setIdLibro(10);
        libro.setTitulo("Libro de prueba");
        libro.setPrecioTiempo(new BigDecimal("2.50"));
        libro.setEstado(false);

        detalle = new DetallePrestamo();
        detalle.setIdDetallePrestamo(30);
        detalle.setPrestamo(prestamo);
        detalle.setLibro(libro);
        detalle.setPrecioTiempo(new BigDecimal("2.50"));
        detalle.setTiempo(3);
        detalle.setFechaCompromiso(LocalDate.of(2026, 11, 12));
        detalle.setEstado("prestado");
        detalle.setSubtotal(new BigDecimal("7.50"));
    }

    @Test
    void debeListarPrestamos() throws Exception {
        when(prestamoService.listarPrestamos())
                .thenReturn(List.of(prestamo));

        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPrestamo").value(20))
                .andExpect(jsonPath("$[0].estado").value("abierto"))
                .andExpect(jsonPath("$[0].serie").value("R-2026"))
                .andExpect(jsonPath("$[0].numero").value("000001"))
                .andExpect(jsonPath("$[0].montoTotal").value(7.50));

        verify(prestamoService).listarPrestamos();
    }

    @Test
    void debeBuscarPrestamos() throws Exception {
        when(prestamoService.buscarPrestamos("Jhordy"))
                .thenReturn(List.of(prestamo));

        mockMvc.perform(
                        get("/api/prestamos")
                                .param("buscar", "Jhordy")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPrestamo").value(20));

        verify(prestamoService).buscarPrestamos("Jhordy");
    }

    @Test
    void debeRegistrarPrestamo() throws Exception {
        when(prestamoService.registrarPrestamo(any(NuevoPrestamoDTO.class)))
                .thenReturn(prestamo);

        mockMvc.perform(
                        post("/api/prestamos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "idUsuario": 1,
                                            "idComprobante": 1,
                                            "observaciones": "Préstamo de prueba",
                                            "idsLibros": [10],
                                            "dias": [3]
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPrestamo").value(20))
                .andExpect(jsonPath("$.estado").value("abierto"))
                .andExpect(jsonPath("$.montoTotal").value(7.50));

        verify(prestamoService)
                .registrarPrestamo(any(NuevoPrestamoDTO.class));
    }

    @Test
    void debeBuscarPrestamoPorId() throws Exception {
        when(prestamoService.buscarPorId(20))
                .thenReturn(prestamo);

        mockMvc.perform(
                        get("/api/prestamos/20")
                                .session(sesion("Bibliotecario", 2))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPrestamo").value(20))
                .andExpect(jsonPath("$.serie").value("R-2026"));

        verify(prestamoService).buscarPorId(20);
    }

    @Test
    void debeListarDetallesDelPrestamo() throws Exception {
        when(prestamoService.buscarPorId(20))
                .thenReturn(prestamo);
        when(prestamoService.listarDetalles(20))
                .thenReturn(List.of(detalle));

        mockMvc.perform(
                        get("/api/prestamos/20/detalles")
                                .session(sesion("Bibliotecario", 2))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idDetallePrestamo").value(30))
                .andExpect(jsonPath("$[0].idLibro").value(10))
                .andExpect(jsonPath("$[0].tituloLibro").value("Libro de prueba"))
                .andExpect(jsonPath("$[0].precioTiempo").value(2.50))
                .andExpect(jsonPath("$[0].tiempo").value(3))
                .andExpect(jsonPath("$[0].fechaCompromiso").value("2026-11-12"))
                .andExpect(jsonPath("$[0].estado").value("prestado"))
                .andExpect(jsonPath("$[0].subtotal").value(7.50));

        verify(prestamoService).buscarPorId(20);
        verify(prestamoService).listarDetalles(20);
    }

    @Test
    void debeListarComprobantes() throws Exception {
        when(prestamoService.listarComprobantes())
                .thenReturn(List.of(comprobante));

        mockMvc.perform(get("/api/prestamos/comprobantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idComprobante").value(1))
                .andExpect(jsonPath("$[0].tipo").value("Recibo"));

        verify(prestamoService).listarComprobantes();
    }

    @Test
    void debeObtenerDatosDelComprobante() throws Exception {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("serie", "R-2026");
        datos.put("numero", "000005");

        when(prestamoService.obtenerDatosComprobante(1))
                .thenReturn(datos);

        mockMvc.perform(
                        get("/api/prestamos/comprobante")
                                .param("idComprobante", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serie").value("R-2026"))
                .andExpect(jsonPath("$.numero").value("000005"));

        verify(prestamoService).obtenerDatosComprobante(1);
    }

    @Test
    void debeRegistrarDevolucion() throws Exception {
        detalle.setEstado("devuelto");
        detalle.setFechaDevolucion(LocalDate.of(2026, 11, 9));
        detalle.getLibro().setEstado(true);

        when(prestamoService.registrarDevolucion(30))
                .thenReturn(detalle);

        mockMvc.perform(put("/api/prestamos/devolucion/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDetallePrestamo").value(30))
                .andExpect(jsonPath("$.estado").value("devuelto"))
                .andExpect(jsonPath("$.fechaDevolucion").value("2026-11-09"));

        verify(prestamoService).registrarDevolucion(30);
    }

    @Test
    void debeResponderBadRequestAnteErrorDeValidacion() throws Exception {
        when(prestamoService.registrarPrestamo(any(NuevoPrestamoDTO.class)))
                .thenThrow(
                        new IllegalArgumentException(
                                "Debe seleccionar al menos un libro."
                        )
                );

        mockMvc.perform(
                        post("/api/prestamos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "idUsuario": 1,
                                            "idComprobante": 1,
                                            "idsLibros": [],
                                            "dias": []
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.mensaje")
                                .value("Debe seleccionar al menos un libro.")
                );
    }

    private MockHttpSession sesion(String rol, int idUsuario) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioId", idUsuario);
        session.setAttribute("tipoUsuario", rol);
        return session;
    }

    @Test
    void lectorNoPuedeConsultarPrestamoAjeno() throws Exception {
        when(prestamoService.buscarPorId(20))
                .thenReturn(prestamo);

        mockMvc.perform(
                get("/api/prestamos/20")
                        .session(sesion("Lector", 3))
        ).andExpect(status().isForbidden());
    }

    @Test
    void lectorNoPuedeConsultarDetallesAjenos() throws Exception {
        when(prestamoService.buscarPorId(20))
                .thenReturn(prestamo);

        mockMvc.perform(
                get("/api/prestamos/20/detalles")
                        .session(sesion("Lector", 3))
        ).andExpect(status().isForbidden());
    }

}

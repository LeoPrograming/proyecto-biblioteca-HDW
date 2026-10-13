
package com.biblioteca.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    // 1. Listar todas las categorias
    @Test
    void debeListarCategorias() {
        Categoria categoria1 = new Categoria();
        categoria1.setIdCategoria(1);
        categoria1.setNombre("Programacion");

        Categoria categoria2 = new Categoria();
        categoria2.setIdCategoria(2);
        categoria2.setNombre("Literatura");

        when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria1, categoria2));

        List<Categoria> resultado =
                categoriaService.listarCategorias();

        assertEquals(2, resultado.size());
        assertEquals("Programacion",
                resultado.get(0).getNombre());
        assertEquals("Literatura",
                resultado.get(1).getNombre());

        verify(categoriaRepository).findAll();
    }

    // 2. Buscar categoria existente por ID
    @Test
    void debeBuscarCategoriaPorId() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Tecnologia");

        when(categoriaRepository.findById(1))
                .thenReturn(Optional.of(categoria));

        Optional<Categoria> resultado =
                categoriaService.obtenerCategoriaPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Tecnologia",
                resultado.get().getNombre());

        verify(categoriaRepository).findById(1);
    }

    // 3. Buscar categoria inexistente
    @Test
    void debeRetornarVacioSiCategoriaNoExiste() {
        when(categoriaRepository.findById(999))
                .thenReturn(Optional.empty());

        Optional<Categoria> resultado =
                categoriaService.obtenerCategoriaPorId(999);

        assertTrue(resultado.isEmpty());

        verify(categoriaRepository).findById(999);
    }

    // 4. Registrar una nueva categoria
    @Test
    void debeRegistrarCategoria() {
        Categoria datos = new Categoria();
        datos.setNombre("Programacion");
        datos.setDescripcion("Libros de desarrollo");
        datos.setImagen("programacion.jpg");

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Categoria resultado =
                categoriaService.registrarCategoria(datos);

        assertEquals("Programacion", resultado.getNombre());
        assertEquals("Libros de desarrollo",
                resultado.getDescripcion());
        assertEquals("programacion.jpg",
                resultado.getImagen());
        assertTrue(resultado.getEstado());

        verify(categoriaRepository)
                .save(any(Categoria.class));
    }

    // 5. Actualizar una categoria existente
    @Test
    void debeActualizarCategoria() {
        Categoria existente = new Categoria();
        existente.setIdCategoria(1);
        existente.setNombre("Nombre anterior");
        existente.setEstado(true);

        Categoria datos = new Categoria();
        datos.setNombre("Nombre actualizado");
        datos.setDescripcion("Nueva descripcion");
        datos.setImagen("nueva-imagen.jpg");

        when(categoriaRepository.findById(1))
                .thenReturn(Optional.of(existente));

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Categoria resultado =
                categoriaService.actualizarCategoria(1, datos);

        assertEquals("Nombre actualizado",
                resultado.getNombre());
        assertEquals("Nueva descripcion",
                resultado.getDescripcion());
        assertEquals("nueva-imagen.jpg",
                resultado.getImagen());
        assertTrue(resultado.getEstado());

        verify(categoriaRepository).save(existente);
    }

    // 6. Rechazar actualizacion de categoria inexistente
    @Test
    void debeRechazarActualizacionDeCategoriaInexistente() {
        Categoria datos = new Categoria();
        datos.setNombre("Categoria de prueba");

        when(categoriaRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> categoriaService.actualizarCategoria(
                        999, datos));

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    // 7. Rechazar registro sin nombre
    @Test
    void debeRechazarCategoriaSinNombre() {
        Categoria datos = new Categoria();
        datos.setNombre("   ");

        assertThrows(IllegalArgumentException.class,
                () -> categoriaService.registrarCategoria(datos));

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }

    // 8. Conservar el estado al actualizar sin especificarlo
    @Test
    void debeConservarEstadoAlActualizarCategoria() {
        Categoria existente = new Categoria();
        existente.setIdCategoria(1);
        existente.setNombre("Categoria anterior");
        existente.setEstado(false);

        Categoria datos = new Categoria();
        datos.setNombre("Categoria modificada");

        when(categoriaRepository.findById(1))
                .thenReturn(Optional.of(existente));

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Categoria resultado =
                categoriaService.actualizarCategoria(1, datos);

        assertFalse(resultado.getEstado());

        verify(categoriaRepository).save(existente);
    }
}

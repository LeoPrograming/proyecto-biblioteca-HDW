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

import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.repository.EditorialRepository;

@ExtendWith(MockitoExtension.class)
class EditorialServiceTest {

    @Mock
    private EditorialRepository editorialRepository;

    @InjectMocks
    private EditorialService editorialService;

    // 1. Listar todas las editoriales
    @Test
    void debeListarEditoriales() {

        Editorial editorial1 =
                new Editorial();

        editorial1.setIdEditorial(1);
        editorial1.setNombre(
                "Editorial Planeta");

        Editorial editorial2 =
                new Editorial();

        editorial2.setIdEditorial(2);
        editorial2.setNombre(
                "Editorial Santillana");

        when(editorialRepository.findAll())
                .thenReturn(
                        List.of(
                                editorial1,
                                editorial2
                        )
                );

        List<Editorial> resultado =
                editorialService
                        .listarEditoriales();

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                "Editorial Planeta",
                resultado
                        .get(0)
                        .getNombre()
        );

        assertEquals(
                "Editorial Santillana",
                resultado
                        .get(1)
                        .getNombre()
        );

        verify(
                editorialRepository
        ).findAll();
    }

    // 2. Buscar editorial existente por ID
    @Test
    void debeBuscarEditorialPorId() {

        Editorial editorial =
                new Editorial();

        editorial.setIdEditorial(1);
        editorial.setNombre(
                "Editorial Planeta");
        editorial.setEstado(true);

        when(editorialRepository.findById(1))
                .thenReturn(
                        Optional.of(
                                editorial
                        )
                );

        Optional<Editorial> resultado =
                editorialService
                        .obtenerEditorialPorId(1);

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                "Editorial Planeta",
                resultado
                        .get()
                        .getNombre()
        );

        assertTrue(
                resultado
                        .get()
                        .getEstado()
        );

        verify(
                editorialRepository
        ).findById(1);
    }

    // 3. Buscar editorial inexistente
    @Test
    void debeRetornarVacioSiEditorialNoExiste() {

        when(editorialRepository.findById(999))
                .thenReturn(
                        Optional.empty()
                );

        Optional<Editorial> resultado =
                editorialService
                        .obtenerEditorialPorId(
                                999
                        );

        assertTrue(
                resultado.isEmpty()
        );

        verify(
                editorialRepository
        ).findById(999);
    }

    // 4. Registrar una editorial
    @Test
    void debeRegistrarEditorial() {

        Editorial datos =
                new Editorial();

        datos.setNombre(
                "Editorial Planeta");

        when(
                editorialRepository.save(
                        any(Editorial.class)
                )
        ).thenAnswer(
                inv ->
                        inv.getArgument(0)
        );

        Editorial resultado =
                editorialService
                        .registrarEditorial(
                                datos
                        );

        assertEquals(
                "Editorial Planeta",
                resultado.getNombre()
        );

        assertTrue(
                resultado.getEstado()
        );

        verify(
                editorialRepository
        ).save(
                any(Editorial.class)
        );
    }

    // 5. Actualizar una editorial existente
    @Test
    void debeActualizarEditorial() {

        Editorial existente =
                new Editorial();

        existente.setIdEditorial(1);
        existente.setNombre(
                "Nombre anterior");
        existente.setEstado(true);

        Editorial datos =
                new Editorial();

        datos.setNombre(
                "Editorial actualizada");

        when(
                editorialRepository.findById(1)
        ).thenReturn(
                Optional.of(
                        existente
                )
        );

        when(
                editorialRepository.save(
                        any(Editorial.class)
                )
        ).thenAnswer(
                inv ->
                        inv.getArgument(0)
        );

        Editorial resultado =
                editorialService
                        .actualizarEditorial(
                                1,
                                datos
                        );

        assertEquals(
                "Editorial actualizada",
                resultado.getNombre()
        );

        assertTrue(
                resultado.getEstado()
        );

        verify(
                editorialRepository
        ).save(existente);
    }

    // 6. Rechazar actualizacion de editorial inexistente
    @Test
    void debeRechazarActualizacionDeEditorialInexistente() {

        Editorial datos =
                new Editorial();

        datos.setNombre(
                "Editorial de prueba");

        when(
                editorialRepository.findById(999)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                NoSuchElementException.class,
                () ->
                        editorialService
                                .actualizarEditorial(
                                        999,
                                        datos
                                )
        );

        verify(
                editorialRepository,
                never()
        ).save(
                any(Editorial.class)
        );
    }

    // 7. Rechazar registro sin nombre
    @Test
    void debeRechazarEditorialSinNombre() {

        Editorial datos =
                new Editorial();

        datos.setNombre("   ");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        editorialService
                                .registrarEditorial(
                                        datos
                                )
        );

        verify(
                editorialRepository,
                never()
        ).save(
                any(Editorial.class)
        );
    }

    // 8. Conservar el estado al actualizar
    @Test
    void debeConservarEstadoAlActualizarEditorial() {

        Editorial existente =
                new Editorial();

        existente.setIdEditorial(1);
        existente.setNombre(
                "Editorial anterior");
        existente.setEstado(false);

        Editorial datos =
                new Editorial();

        datos.setNombre(
                "Editorial modificada");

        when(
                editorialRepository.findById(1)
        ).thenReturn(
                Optional.of(
                        existente
                )
        );

        when(
                editorialRepository.save(
                        any(Editorial.class)
                )
        ).thenAnswer(
                inv ->
                        inv.getArgument(0)
        );

        Editorial resultado =
                editorialService
                        .actualizarEditorial(
                                1,
                                datos
                        );

        assertFalse(
                resultado.getEstado()
        );

        assertEquals(
                "Editorial modificada",
                resultado.getNombre()
        );

        verify(
                editorialRepository
        ).save(existente);
    }
}
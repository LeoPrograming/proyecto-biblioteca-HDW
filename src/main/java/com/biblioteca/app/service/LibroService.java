package com.biblioteca.app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.model.Autor;
import com.biblioteca.app.model.Categoria;
import com.biblioteca.app.model.Editorial;
import com.biblioteca.app.model.Libro;

import com.biblioteca.app.repository.AutorRepository;
import com.biblioteca.app.repository.CategoriaRepository;
import com.biblioteca.app.repository.EditorialRepository;
import com.biblioteca.app.repository.LibroRepository;

@Service
@Transactional(readOnly = true)
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;
    private final EditorialRepository editorialRepository;

    public LibroService(
            LibroRepository libroRepository,
            AutorRepository autorRepository,
            CategoriaRepository categoriaRepository,
            EditorialRepository editorialRepository) {

        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
        this.editorialRepository = editorialRepository;
    }


    public List<Libro> listarLibros() {
        List<Libro> libros = libroRepository.findAll();

        libros.forEach(libro -> {
            if (libro.getAutores() != null) {
                libro.getAutores().size();
            }
        });

        return libros;
    }

    public Optional<Libro> obtenerLibroPorId(Integer id) {
        Optional<Libro> resultado = libroRepository.findById(id);

        resultado.ifPresent(libro -> {
            if (libro.getAutores() != null) {
                libro.getAutores().size();
            }
        });

        return resultado;
    }


    // Registrar un nuevo libro
    @Transactional
    public Libro registrarLibro(Libro datos) {
        validarLibro(datos);

        Categoria categoria = resolverCategoria(
                datos.getCategoria()
        );

        Editorial editorial = resolverEditorial(
                datos.getEditorial()
        );

        List<Autor> autores = resolverAutores(
                datos.getAutores()
        );

        Libro libro = new Libro();

        libro.setTitulo(datos.getTitulo().trim());
        libro.setIsbn(datos.getIsbn());
        libro.setStock(datos.getStock());
        libro.setPrecio(datos.getPrecio());

        libro.setEstado(
                datos.getEstado() != null
                        ? datos.getEstado() : true
        );

        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setAutores(autores);

        return libroRepository.save(libro);
    }

    // Actualizar un libro existente
    @Transactional
    public Libro actualizarLibro(Integer id, Libro datos) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Libro con ID " + id + " no encontrado"
                ));

        validarLibro(datos);

        Categoria categoria = resolverCategoria(
                datos.getCategoria()
        );

        libro.setTitulo(datos.getTitulo().trim());
        libro.setIsbn(datos.getIsbn());
        libro.setStock(datos.getStock());
        libro.setPrecio(datos.getPrecio());
        libro.setCategoria(categoria);

        if (datos.getEditorial() != null) {
            libro.setEditorial(
                    resolverEditorial(datos.getEditorial())
            );
        }

        if (datos.getAutores() != null) {
            libro.setAutores(
                    resolverAutores(datos.getAutores())
            );
        }

        if (datos.getEstado() != null) {
            libro.setEstado(datos.getEstado());
        }

        return libroRepository.save(libro);
    }

    // Cambiar el estado de un libro
    @Transactional
    public Libro cambiarEstado(Integer id, Boolean estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del libro es obligatorio"
            );
        }

        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Libro con ID " + id + " no encontrado"
                ));

        libro.setEstado(estado);

        return libroRepository.save(libro);
    }

    // Validar datos principales del libro
    private void validarLibro(Libro libro) {
        if (libro == null
                || libro.getTitulo() == null
                || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException(
                    "El titulo del libro es obligatorio"
            );
        }

        if (libro.getStock() != null
                && libro.getStock() < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo"
            );
        }

        if (libro.getPrecio() != null
                && libro.getPrecio() < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo"
            );
        }
    }

    // Verificar que la categoria exista
    private Categoria resolverCategoria(Categoria categoria) {
        if (categoria == null
                || categoria.getIdCategoria() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar una categoria valida"
            );
        }

        return categoriaRepository.findById(
                categoria.getIdCategoria()
        ).orElseThrow(() -> new NoSuchElementException(
                "La categoria indicada no existe"
        ));
    }

    // Verificar que la editorial exista
    private Editorial resolverEditorial(Editorial editorial) {
        if (editorial == null) {
            return null;
        }

        if (editorial.getIdEditorial() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar una editorial valida"
            );
        }

        return editorialRepository.findById(
                editorial.getIdEditorial()
        ).orElseThrow(() -> new NoSuchElementException(
                "La editorial indicada no existe"
        ));
    }

    // Verificar los autores asociados al libro
    private List<Autor> resolverAutores(List<Autor> autores) {
        List<Autor> resultado = new ArrayList<>();

        if (autores == null) {
            return resultado;
        }

        Set<Integer> idsProcesados = new HashSet<>();

        for (Autor autor : autores) {
            if (autor == null || autor.getIdAutor() == null) {
                throw new IllegalArgumentException(
                        "Debe indicar un autor valido"
                );
            }

            Integer idAutor = autor.getIdAutor();

            if (!idsProcesados.add(idAutor)) {
                continue;
            }

            Autor autorExistente = autorRepository.findById(
                    idAutor
            ).orElseThrow(() -> new NoSuchElementException(
                    "Autor con ID " + idAutor + " no encontrado"
            ));

            resultado.add(autorExistente);
        }

        return resultado;
    }
}

package com.tecsup.service;

import com.tecsup.dto.CategoriaDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Categoria;
import com.tecsup.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");
        categoria.setActivo(true);

        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setNombre("Electrónica");
        categoriaDTO.setDescripcion("Productos electrónicos");
        categoriaDTO.setActivo(true);
    }

    @Test
    void listar_deberiaRetornarListaDeCategorias() {
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepository.findAll()).thenReturn(categorias);

        List<Categoria> resultado = categoriaService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Electrónica", resultado.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void guardar_deberiaCrearCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.guardar(categoriaDTO);

        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.getNombre());
        assertEquals("Productos electrónicos", resultado.getDescripcion());
        assertTrue(resultado.getActivo());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void obtener_deberiaRetornarCategoriaCuandoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.obtener(1L);

        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void obtener_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoriaService.obtener(1L);
        });
    }

    @Test
    void actualizar_deberiaActualizarCategoriaExistente() {
        CategoriaDTO updateDTO = new CategoriaDTO();
        updateDTO.setNombre("Electrónica Premium");
        updateDTO.setDescripcion("Productos electrónicos de alta gama");
        updateDTO.setActivo(true);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria resultado = categoriaService.actualizar(1L, updateDTO);

        assertEquals("Electrónica Premium", resultado.getNombre());
        assertEquals("Productos electrónicos de alta gama", resultado.getDescripcion());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizar_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        CategoriaDTO updateDTO = new CategoriaDTO();
        updateDTO.setNombre("Electrónica Premium");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoriaService.actualizar(1L, updateDTO);
        });
    }

    @Test
    void eliminar_deberiaEliminarCategoriaExistente() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.eliminar(1L);

        verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    void eliminar_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoriaService.eliminar(1L);
        });
    }
}

package com.tecsup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.dto.CategoriaDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Categoria;
import com.tecsup.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;
    private List<Categoria> categorias;

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

        categorias = Arrays.asList(categoria);
    }

    @Test
    void listar_deberiaRetornarListaDeCategorias() throws Exception {
        when(categoriaService.listar()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Electrónica"))
                .andExpect(jsonPath("$[0].descripcion").value("Productos electrónicos"));

        verify(categoriaService, times(1)).listar();
    }

    @Test
    void guardar_deberiaCrearCategoria() throws Exception {
        when(categoriaService.guardar(any(CategoriaDTO.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Electrónica"))
                .andExpect(jsonPath("$.descripcion").value("Productos electrónicos"));

        verify(categoriaService, times(1)).guardar(any(CategoriaDTO.class));
    }

    @Test
    void guardar_deberiaRetornarBadRequestCuandoNombreEstaVacio() throws Exception {
        CategoriaDTO invalidDTO = new CategoriaDTO();
        invalidDTO.setNombre("");

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).guardar(any(CategoriaDTO.class));
    }

    @Test
    void obtener_deberiaRetornarCategoriaCuandoExiste() throws Exception {
        when(categoriaService.obtener(1L)).thenReturn(categoria);

        mockMvc.perform(get("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Electrónica"))
                .andExpect(jsonPath("$.descripcion").value("Productos electrónicos"));

        verify(categoriaService, times(1)).obtener(1L);
    }

    @Test
    void obtener_deberiaRetornarNotFoundCuandoCategoriaNoExiste() throws Exception {
        when(categoriaService.obtener(1L)).thenThrow(new ResourceNotFoundException("Categoria no encontrada con id: 1"));

        mockMvc.perform(get("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(categoriaService, times(1)).obtener(1L);
    }

    @Test
    void actualizar_deberiaActualizarCategoria() throws Exception {
        CategoriaDTO updateDTO = new CategoriaDTO();
        updateDTO.setNombre("Electrónica Premium");
        updateDTO.setDescripcion("Productos electrónicos de alta gama");
        updateDTO.setActivo(true);

        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setId(1L);
        categoriaActualizada.setNombre("Electrónica Premium");
        categoriaActualizada.setDescripcion("Productos electrónicos de alta gama");
        categoriaActualizada.setActivo(true);

        when(categoriaService.actualizar(eq(1L), any(CategoriaDTO.class))).thenReturn(categoriaActualizada);

        mockMvc.perform(put("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Electrónica Premium"))
                .andExpect(jsonPath("$.descripcion").value("Productos electrónicos de alta gama"));

        verify(categoriaService, times(1)).actualizar(eq(1L), any(CategoriaDTO.class));
    }

    @Test
    void eliminar_deberiaEliminarCategoria() throws Exception {
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).eliminar(1L);
    }

    @Test
    void eliminar_deberiaRetornarNotFoundCuandoCategoriaNoExiste() throws Exception {
        doThrow(new ResourceNotFoundException("Categoria no encontrada con id: 1")).when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(categoriaService, times(1)).eliminar(1L);
    }
}

package com.tecsup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Categoria;
import com.tecsup.model.Producto;
import com.tecsup.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto;
    private Categoria categoria;
    private ProductoDTO productoDTO;
    private List<Producto> productos;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop gamer");
        producto.setPrecio(new BigDecimal("1500.00"));
        producto.setStock(10);
        producto.setActivo(true);
        producto.setCategoria(categoria);

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Laptop");
        productoDTO.setDescripcion("Laptop gamer");
        productoDTO.setPrecio(new BigDecimal("1500.00"));
        productoDTO.setStock(10);
        productoDTO.setActivo(true);
        productoDTO.setCategoriaId(1L);

        productos = Arrays.asList(producto);
    }

    @Test
    void listar_deberiaRetornarListaDeProductos() throws Exception {
        when(productoService.listar()).thenReturn(productos);

        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Laptop"))
                .andExpect(jsonPath("$[0].precio").value(1500.00))
                .andExpect(jsonPath("$[0].stock").value(10));

        verify(productoService, times(1)).listar();
    }

    @Test
    void guardar_deberiaCrearProducto() throws Exception {
        when(productoService.guardar(any(ProductoDTO.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.precio").value(1500.00))
                .andExpect(jsonPath("$.stock").value(10));

        verify(productoService, times(1)).guardar(any(ProductoDTO.class));
    }

    @Test
    void guardar_deberiaRetornarBadRequestCuandoNombreEstaVacio() throws Exception {
        ProductoDTO invalidDTO = new ProductoDTO();
        invalidDTO.setNombre("");
        invalidDTO.setPrecio(new BigDecimal("100.00"));
        invalidDTO.setStock(5);
        invalidDTO.setCategoriaId(1L);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).guardar(any(ProductoDTO.class));
    }

    @Test
    void obtener_deberiaRetornarProductoCuandoExiste() throws Exception {
        when(productoService.obtener(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.precio").value(1500.00));

        verify(productoService, times(1)).obtener(1L);
    }

    @Test
    void obtener_deberiaRetornarNotFoundCuandoProductoNoExiste() throws Exception {
        when(productoService.obtener(1L)).thenThrow(new ResourceNotFoundException("Producto no encontrado con id: 1"));

        mockMvc.perform(get("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(productoService, times(1)).obtener(1L);
    }

    @Test
    void actualizar_deberiaActualizarProducto() throws Exception {
        ProductoDTO updateDTO = new ProductoDTO();
        updateDTO.setNombre("Laptop Pro");
        updateDTO.setDescripcion("Laptop gamer premium");
        updateDTO.setPrecio(new BigDecimal("2000.00"));
        updateDTO.setStock(5);
        updateDTO.setActivo(true);
        updateDTO.setCategoriaId(1L);

        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Laptop Pro");
        productoActualizado.setDescripcion("Laptop gamer premium");
        productoActualizado.setPrecio(new BigDecimal("2000.00"));
        productoActualizado.setStock(5);
        productoActualizado.setActivo(true);
        productoActualizado.setCategoria(categoria);

        when(productoService.actualizar(eq(1L), any(ProductoDTO.class))).thenReturn(productoActualizado);

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop Pro"))
                .andExpect(jsonPath("$.precio").value(2000.00));

        verify(productoService, times(1)).actualizar(eq(1L), any(ProductoDTO.class));
    }

    @Test
    void eliminar_deberiaEliminarProducto() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminar(1L);
    }

    @Test
    void eliminar_deberiaRetornarNotFoundCuandoProductoNoExiste() throws Exception {
        doThrow(new ResourceNotFoundException("Producto no encontrado con id: 1")).when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());

        verify(productoService, times(1)).eliminar(1L);
    }
}

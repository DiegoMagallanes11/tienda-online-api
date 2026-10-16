package com.tecsup.service;

import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Categoria;
import com.tecsup.model.Producto;
import com.tecsup.repository.CategoriaRepository;
import com.tecsup.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;
    private ProductoDTO productoDTO;

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
    }

    @Test
    void listar_deberiaRetornarListaDeProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laptop", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void guardar_deberiaCrearProducto() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.guardar(productoDTO);

        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(new BigDecimal("1500.00"), resultado.getPrecio());
        assertEquals(10, resultado.getStock());
        assertTrue(resultado.getActivo());
        assertEquals(categoria, resultado.getCategoria());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void guardar_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.guardar(productoDTO);
        });
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void obtener_deberiaRetornarProductoCuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtener(1L);

        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(new BigDecimal("1500.00"), resultado.getPrecio());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void obtener_deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.obtener(1L);
        });
    }

    @Test
    void actualizar_deberiaActualizarProductoExistente() {
        ProductoDTO updateDTO = new ProductoDTO();
        updateDTO.setNombre("Laptop Pro");
        updateDTO.setDescripcion("Laptop gamer premium");
        updateDTO.setPrecio(new BigDecimal("2000.00"));
        updateDTO.setStock(5);
        updateDTO.setActivo(true);
        updateDTO.setCategoriaId(1L);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.actualizar(1L, updateDTO);

        assertEquals("Laptop Pro", resultado.getNombre());
        assertEquals("Laptop gamer premium", resultado.getDescripcion());
        assertEquals(new BigDecimal("2000.00"), resultado.getPrecio());
        assertEquals(5, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizar_deberiaLanzarExcepcionCuandoProductoNoExiste() {
        ProductoDTO updateDTO = new ProductoDTO();
        updateDTO.setNombre("Laptop Pro");
        updateDTO.setPrecio(new BigDecimal("2000.00"));
        updateDTO.setStock(5);
        updateDTO.setCategoriaId(1L);

        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.actualizar(1L, updateDTO);
        });
    }

    @Test
    void eliminar_deberiaEliminarProductoExistente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.eliminar(1L);

        verify(productoRepository, times(1)).delete(producto);
    }

    @Test
    void eliminar_deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.eliminar(1L);
        });
    }
}

package projetoMockito;

import sistemaProdutoMockito.produto.Produto;
import sistemaProdutoMockito.produto.ProdutoRepository;
import sistemaProdutoMockito.produto.ProdutoService;
import sistemaProdutoMockito.exception.ErroAoSalvarProdutoException;
import sistemaProdutoMockito.exception.ProdutoNaoEncontradoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;


    @Test
    void deveSalvarProdutoComPrecoValido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertEquals("Mouse", resultado.getNome());
        assertEquals(49.90, resultado.getPreco(), 0.001);
        verify(repository).salvar(produto);
    }

    @Test
    void naoDeveSalvarProdutoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(null));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void naoDeveSalvarProdutoComNomeNulo() {
        Produto produto = new Produto(null, 10.0, 2);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void naoDeveSalvarProdutoComNomeVazio() {
        Produto produto = new Produto("", 10.0, 2);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void naoDeveSalvarProdutoComNomeEmBranco() {
        Produto produto = new Produto("   ", 10.0, 2);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void naoDeveSalvarProdutoComPrecoNegativo() {
        Produto produto = new Produto("Mouse", -10.0, 5);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void naoDeveSalvarProdutoComPrecoIgualAZero() {
        Produto produto = new Produto("Mouse", 0.0, 5);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void deveAceitarPrecoMinimoPositivo() {
        Produto produto = new Produto("Mouse", 0.01, 5);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertEquals(0.01, resultado.getPreco(), 0.001);
        verify(repository).salvar(produto);
    }

    @Test
    void naoDeveSalvarProdutoComQuantidadeNegativa() {
        Produto produto = new Produto("Mouse", 10.0, -1);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any(Produto.class));
    }

    @Test
    void deveAceitarProdutoComEstoqueZerado() {
        Produto produto = new Produto("Mouse", 10.0, 0);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertEquals(0, resultado.getQuantidade());
        verify(repository).salvar(produto);
    }

    @Test
    void deveBuscarProdutoExistente() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(repository.buscarPorId(1L)).thenReturn(produto);

        Produto resultado = service.buscarPorId(1L);

        assertSame(produto, resultado);
        verify(repository).buscarPorId(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoForEncontrado() {
        when(repository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.buscarPorId(99L));

        verify(repository).buscarPorId(99L);
    }

    @Test
    void deveLancarErroQuandoRepositoryFalharAoSalvar() {
        Produto produto = new Produto("Teclado", 129.90, 5);

        doThrow(new RuntimeException("Falha na conexao"))
                .when(repository).salvar(any(Produto.class));

        assertThrows(ErroAoSalvarProdutoException.class,
                () -> service.salvar(produto));

        verify(repository).salvar(produto);
    }

}

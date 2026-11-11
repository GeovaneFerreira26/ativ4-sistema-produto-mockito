package projetoMockito;

import sistemaProdutoMockito.produto.Produto;
import sistemaProdutoMockito.produto.ProdutoRepository;
import sistemaProdutoMockito.pedidos.Pedido;
import sistemaProdutoMockito.pedidos.PedidoRepository;
import sistemaProdutoMockito.pedidos.PedidoService;
import sistemaProdutoMockito.exception.EstoqueInsuficienteException;
import sistemaProdutoMockito.exception.ErroAoSalvarPedidoException;
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
class PedidoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService service;


    @Test
    void deveCalcularValorTotalDoPedido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 2);

        assertEquals(99.80, pedido.getValorTotal(), 0.001);
        assertSame(produto, pedido.getProduto());
        assertEquals(2, pedido.getQuantidade());

        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    void naoDeveCriarPedidoComQuantidadeZero() {
        assertThrows(IllegalArgumentException.class,
                () -> service.criarPedido(1L, 0));

        verify(produtoRepository, never()).buscarPorId(any());
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void naoDeveCriarPedidoComQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> service.criarPedido(1L, -3));

        verify(produtoRepository, never()).buscarPorId(any());
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistir() {
        when(produtoRepository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.criarPedido(99L, 1));

        verify(produtoRepository).buscarPorId(99L);
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueForInsuficiente() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class,
                () -> service.criarPedido(1L, 11));

        verify(produtoRepository).buscarPorId(1L);
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void deveAceitarPedidoComQuantidadeIgualAoEstoque() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 10);

        assertEquals(499.00, pedido.getValorTotal(), 0.001);
        verify(pedidoRepository).salvar(any(Pedido.class));
    }

    @Test
    void devePropagarFalhaDoProdutoRepositorySemSalvarPedido() {
        when(produtoRepository.buscarPorId(1L))
                .thenThrow(new RuntimeException("Falha na consulta"));

        assertThrows(RuntimeException.class,
                () -> service.criarPedido(1L, 2));

        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    void devePropagarFalhaDoPedidoRepositoryNaVersaoBase() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        doThrow(new RuntimeException("Falha na conexao"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        assertThrows(RuntimeException.class,
                () -> service.criarPedido(1L, 2));

        verify(pedidoRepository).salvar(any(Pedido.class));
    }

    /*
     * Este teste e para a VERSAO MELHORADA do PedidoService,
     * que captura RuntimeException e lanca ErroAoSalvarPedidoException.
     * Nao execute simultaneamente com o teste de propagacao acima,
     * pois os dois representam implementacoes diferentes.
     */
    @Test
    void deveConverterFalhaAoSalvarPedidoNaVersaoMelhorada() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        doThrow(new RuntimeException("Falha na conexao"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        assertThrows(ErroAoSalvarPedidoException.class,
                () -> service.criarPedido(1L, 2));

        verify(pedidoRepository).salvar(any(Pedido.class));
    }

}

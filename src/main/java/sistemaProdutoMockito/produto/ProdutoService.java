package sistemaProdutoMockito.produto;

import sistemaProdutoMockito.exception.ErroAoSalvarProdutoException;
import sistemaProdutoMockito.exception.ProdutoNaoEncontradoException;

public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto salvar(Produto produto) {

        if (produto == null || produto.getNome() == null
                || produto.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "Nome do produto e obrigatorio");
        }

        if (produto.getPreco() <= 0) {
            throw new IllegalArgumentException(
                    "Preco deve ser maior que zero");
        }

        if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException(
                    "Quantidade nao pode ser negativa");
        }

        try {
            return repository.salvar(produto);
        } catch (RuntimeException e) {
            throw new ErroAoSalvarProdutoException(
                    "Falha ao salvar o produto", e);
        }

    }

    public Produto buscarPorId(Long id) {

        Produto produto = repository.buscarPorId(id);

        if (produto == null) {
            throw new ProdutoNaoEncontradoException(
                    "Produto nao encontrado: " + id);
        }

        return produto;
    }

}

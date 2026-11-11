package sistemaProdutoMockito.produto;

import sistemaProdutoMockito.produto.Produto;

public interface ProdutoRepository {

    Produto salvar(Produto produto);

    Produto buscarPorId(Long id);

}

package filmnow;

import java.util.Objects;

/**
 * Sistema que mantém os seus filmes prediletos. Podem existir 100 filmes. 
 * 
 * @author Luis Gustavo Paiva Alves | 124210973
 *
 */
public class FilmNow {
	
	private static final int TAMANHO = 100;
	private static final int TAMANHOHOTLIST = 10;
	int posicao;
	String nome;
	int ano;
	String local;
	
	private Filme[] filmes;
	private Filme[] hotList;

	/**
	 * Cria o FilmNow.
	 */
	public FilmNow() {
		this.filmes = new Filme[TAMANHO];
		this.hotList = new Filme[TAMANHOHOTLIST];
	}
	
	/**
	 * Acessa a lista de filmes mantida.
	 * @return O array de filmes.
	 */
	public Filme[] getFilmes() {
		return this.filmes.clone();
	}

	/**
	 * Acessa a lista de filmes Hot mantida.
	 * @return O array de filmes Hot.
	 */
	public Filme[] getHotList() {
		return this.hotList;
	}

	/**
	 * Acessa os dados de um filme específico.
	 * @param posicao Posição do filme no sistema.
	 * @return Dados do filme. Null se não há filme na posição.
	 */
	public Filme getFilme(int posicao) {
		return filmes[posicao];
	}

	/**
	 * Adiciona um filme em uma posição. Se já existir filme na posição, sobrescreve o anterior. 
	 * @param posicao Posição do filme.
	 * @param nome Nome do filme.
	 * @param ano Ano de lançamento do filme.
	 * @param local Local onde o filme pode ser assitido.
	 * @return Mensagem indicando o resultado da operação
	 */
	public String cadastraFilme(int posicao, String nome, String ano, String local) {
		if (posicao < 1 || posicao > TAMANHO){
			return "POSIÇÃO INVÁLIDA";
		}
		
		if(nome == null || local == null){
			return "FILME INVALIDO";
		}

		Filme novoFilme = new Filme(nome, ano, local);

		for (Filme f : filmes) {
			if (novoFilme.equals(f)) {
				return "FILME JA ADICIONADO";
			}
		}

		this.filmes[posicao - 1] = novoFilme;
		return "FILME ADICIONADO";
	}
	
	/**
	 * Retorna os detalhes de um filme específico.
	 * @param nome Nome do filme a ser detalhado.
	 * @return String com detalhes do filme ou mensagem de erro.
	 */
	public String detalharFilme(String nome) {
		if(posicao < 1 || posicao > TAMANHO || filmes[posicao - 1] == null) {
			return "POSIÇÃO INVÁLIDA";
		}
		return(filmes[posicao-1].toString());
	}

	/**
	 * Retorna a posição atual no sistema.
	 * @return A posição atual.
	 */
	public int getPosicao() {
		return posicao;
	}

	/**
	 * Define a posição no sistema.
	 * @param posicao Nova posição a ser definida.
	 */
	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	/**
	 * Retorna o nome atual.
	 * @return O nome atual.
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Define o nome.
	 * @param nome Novo nome a ser definido.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Retorna o ano atual.
	 * @return O ano atual.
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * Define o ano.
	 * @param ano Novo ano a ser definido.
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * Retorna o local atual.
	 * @return O local atual.
	 */
	public String getLocal() {
		return local;
	}

	/**
	 * Gera o código hash para o objeto.
	 * @return O código hash gerado.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(nome);
	}

	/**
	 * Compara este objeto com outro para verificar igualdade.
	 * @param obj Objeto a ser comparado.
	 * @return true se os objetos são iguais, false caso contrário.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilmNow other = (FilmNow) obj;
		return Objects.equals(nome, other.nome) && posicao == other.posicao;
	}

	/**
	 * Define o local.
	 * @param local Novo local a ser definido.
	 */
	public void setLocal(String local) {
		this.local = local;
	}

	/**
	 * Retorna o tamanho máximo da lista de filmes.
	 * @return O tamanho máximo da lista.
	 */
	public static int getTamanho() {
		return TAMANHO;
	}

	/**
	 * Define a lista de filmes.
	 * @param filmes Nova lista de filmes.
	 */
	public void setFilmes(Filme[] filmes) {
		this.filmes = filmes;
	}

	/**
	 * Define a lista de filmes Hot.
	 * @param hotList Nova lista de filmes Hot.
	 */
	public void setHotList(Filme[] hotList) {
		this.hotList = hotList;
	}

	/**
	 * Adiciona um filme à HotList em uma posição específica.
	 * @param posicaoFilme Posição do filme na lista principal.
	 * @param posicaoHotList Posição na HotList onde o filme será adicionado.
	 * @return Mensagem indicando o resultado da operação.
	 */
	public String cadastraFilmeHotList(int posicaoFilme, int posicaoHotList) {
		if(posicaoFilme < 1 || posicaoFilme > TAMANHO) {
			return "POSIÇÃO INVÁLIDA";
		}
		
		if(posicaoHotList < 1 || posicaoHotList > TAMANHOHOTLIST) {
			return "POSIÇÃO INVÁLIDA";
		}
		
		for(int i = 0; i < hotList.length; i++) {
			if (hotList[i] != null && hotList[i].equals(filmes[posicaoFilme - 1])) {
				return "FILME JÁ PRESENTE NA LISTA";
			}
		}
			
		this.hotList[posicaoHotList - 1] = this.filmes[posicaoFilme - 1];
		this.hotList[posicaoHotList - 1].setHot(true);
		return "ADICIONADO À HOTLIST NA POSIÇÃO " + posicaoHotList + "!";
	}

	/**
	 * Remove um filme da HotList em uma posição específica.
	 * @param posicaoHotList Posição na HotList a ser removida.
	 * @return Mensagem indicando o resultado da operação.
	 */
	public String removeHotList(int posicaoHotList) {
		if(posicaoHotList < 1 || posicaoHotList > TAMANHOHOTLIST) {
			return "POSIÇÃO INVÁLIDA";
		}
		
		if(hotList[posicaoHotList - 1] == null) {
			return "A POSIÇÃO ESCOLHIDA NÃO POSSUI NENHUM FILME ";
		}
		this.hotList[posicaoHotList - 1] = null;
		return "FILME REMOVIDO";
	}
}
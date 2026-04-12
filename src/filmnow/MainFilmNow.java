package filmnow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Interface com menus texto para manipular o sistema FilmNow.
 * 
 * @author Luis Gustavo Paiva Alves | 124210973
 *
 */
public class MainFilmNow {

	public static void main(String[] args) {
		FilmNow fn = new FilmNow();

		System.out.println("Carregando filmes ...");
		try {
			/*
			 * Essa é a maneira de lidar com possíveis erros por falta do arquivo. 
			 */
			carregaFilmes("filmes_inicial.csv", fn);
		} catch (FileNotFoundException e) {
			System.err.println("Arquivo não encontrado: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Erro lendo arquivo: " + e.getMessage());
		}

		Scanner scanner = new Scanner(System.in);
		String escolha = "";
		while (true) {
			escolha = menu(scanner);
			comando(escolha, fn, scanner);
		}

	}

	/**
	 * Exibe o menu e captura a escolha do/a usuário/a.
	 * 
	 * @param scanner Para captura da opção do usuário/a.
	 * @return O comando escolhido.
	 */
	
	private static String menu(Scanner scanner) {
		System.out.println(
				"\n---\nMENU\n" + 
						"(A)Adicionar filme\n" + 
						"(M)Mostrar todos\n" + 
						"(D)Detalhar filme\n" + 
						"(E)Exibir HotList\n" + 
						"(H)Atribuir Hot\n" + 
						"(R)Remover Hot\n" + 
						"(S)air\n" + 
						"\n" + 
						"Opção> ");
		return scanner.next().toUpperCase();
	}

	/**
	 * Interpreta a opção escolhida por quem está usando o sistema.
	 * 
	 * @param opcao   Opção digitada.
	 * @param fn  O sistema FilmNow que estamos manipulando.
	 * @param scanner Objeto scanner para o caso do comando precisar de mais input.
	 */
	private static void comando(String opcao, FilmNow fn, Scanner scanner) {
		switch (opcao) {
		case "A":
			adicionaFilme(fn, scanner);
			break;
		case "M":
			mostrarFilmes(fn);
			break;
		case "D":
			detalharFilme(fn, scanner);
			break;
		case "H":
			adicionaFilmeHotList(fn, scanner);
			break;
		case "E":
			mostrarHotList(fn);
			break;
		case "R":
			removeFilmeHotList(fn, scanner);
			break;
		case "S":
			sai();
			break;
		default:
			System.out.println("Opção inválida!");
		}
	}

	/**
	 * Imprime lista de filmes.
	 * 
	 * @param fn O sistema FilmNow a ser manipulado.
	 */
	private static void mostrarFilmes(FilmNow fn) {

		Filme[] filmes = fn.getFilmes();
		for (int i = 0; i < filmes.length; i++) {
			if (filmes[i] != null) {
				System.out.println(formataFilme(i, filmes[i].getNome()));
			}
		}
	}

	/**
	 * Imprime os detalhes de um dos filmes. 
	 * 
	 * @param fn O sistema FilmNow a ser manipulado.
	 * @param scanner Scanner para capturar qual contato.
	 */
	private static void detalharFilme(FilmNow fn, Scanner scanner) {
		System.out.print("\nQual filme> ");
		int posicao = scanner.nextInt();
		Filme filme = fn.getFilme(posicao-1); 
		System.out.println("Dados do filme:\n" + filme);
	}

	/**
	 * Formata um filme para impressão. 
	 * 
	 * @param posicao A posição do filme (que é exibida)/
	 * @param filme O filme a ser impresso.
	 * @return A String formatada .. Ksoioioi
	 */
	private static String formataFilme(int posicao, String nome) {
		return (posicao+1) + " - " + nome;
	}

	/**
	 * Cadastra um filme no sistema. 
	 * 
	 * @param fn O sistema FilmNow a ser manipulado.
	 * @param scanner Scanner para pedir informações do contato.
	 */
	private static void adicionaFilme(FilmNow fn, Scanner scanner) {
		System.out.print("\nPosição no sistema> ");
		int posicao = scanner.nextInt();
		System.out.print("\nNome> ");
		String nome = scanner.next();
		System.out.print("\nAno> ");
		String ano = scanner.next();
		System.out.print("\nLocal> ");
		String local = scanner.next();
		String retorno = fn.cadastraFilme(posicao, nome, ano, local);
		System.out.println(retorno);
	}

	/**
	 * Sai da aplicação.
	 */
	private static void sai() {
		System.out.println("\nVlw flw o/");
		System.exit(0);
	}

	/**
	 * Lê carga de filmes de um arquivo csv. 
	 * 
	 * @param arquivoFilmes O caminho para o arquivo.
	 * @param fn O sistema FilmNow a ser populado com os dados.
	 * @throws IOException Caso o arquivo não exista ou não possa ser lido.
	 */
	private static void carregaFilmes(String arquivoFilmes, FilmNow fn) throws FileNotFoundException, IOException {
		LeitorFilmNow leitor = new LeitorFilmNow();
		
		int carregados =  leitor.carregaContatos(arquivoFilmes, fn);
		System.out.println("Carregamos " + carregados + " registros.");
	}


	/**
	 * Adiciona um filme à HotList do sistema.
	 * 
	 * @param fn O sistema FilmNow a ser manipulado.
	 * @param scanner Scanner para capturar qual filme e posição na HotList.
	 */
	private static void adicionaFilmeHotList(FilmNow fn, Scanner scanner) {
		System.out.print("\nFilme> ");
		int posicao = scanner.nextInt();
		System.out.print("\nPosicao> ");
		int posicaoHot = scanner.nextInt();
		String retorno = fn.cadastraFilmeHotList(posicao, posicaoHot);
		System.out.println(retorno);
	}


	/**
	 * Exibe a HotList de filmes.
	 * 
	 * @param fn O sistema FilmNow a ser manipulado.
	 */
	private static void mostrarHotList(FilmNow fn) {

		Filme[] hotList = fn.getHotList();
		for (int i = 0; i < hotList.length; i++) {
			if (hotList[i] != null) {
				System.out.println(formataFilme(i, hotList[i].getNome()));
			}
		}
	}

	/**
	 * Remove um filme da HotList do sistema.
	 * 
	 * @param fn O sistema FilmNow a ser manipulado.
	 * @param scanner Scanner para capturar qual posição da HotList será removida.
	 */
	private static void removeFilmeHotList(FilmNow fn, Scanner scanner) {
		System.out.print("\nPosicao> ");
		int posicaoHotList = scanner.nextInt();
		String retorno = fn.removeHotList(posicaoHotList);
		System.out.println(retorno);
	}


}

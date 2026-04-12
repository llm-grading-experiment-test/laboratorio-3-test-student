package filmnow;

/**
 * Representa um filme no sistema FilmNow.
 * Armazena informações básicas como nome, ano, localização e status Hot.
 * 
 * @author Luis Gustavo Paiva Alves | 124210973
 * 
 */
public class Filme {
    private String nome;
    private String ano;
    private String local;
    private boolean ehHot;

    /**
     * Constrói um novo filme com os dados básicos.
     * 
     * @param nome Nome do filme
     * @param ano Ano de lançamento do filme
     * @param local Localização física do filme
     */
    public Filme(String nome, String ano, String local) {
        this.nome = nome;
        this.ano = ano;
        this.local = local;
    }

    /**
     * Retorna o nome do filme.
     * 
     * @return Nome do filme
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o ano de lançamento do filme.
     * 
     * @return Ano do filme
     */
    public String getAno() {
        return ano;
    }

    /**
     * Retorna a localização física do filme.
     * 
     * @return Localização do filme
     */
    public String getLocal() {
        return local;
    }

    /**
     * Verifica se o filme está marcado como Hot.
     * 
     * @return true se o filme é Hot, false caso contrário
     */
    public boolean setEhHot() {
        return ehHot;
    }

    /**
     * Define o status Hot do filme.
     * 
     * @param isHot true para marcar como Hot, false para remover a marcação
     */
    public void setHot(boolean isHot) {
        this.ehHot = isHot;
    }

    /**
     * Retorna uma representação em string do filme.
     * 
     * @return String formatada com nome, ano e localização do filme
     */
    @Override
    public String toString() {
        return nome + ", "+ ano + "\n" + local;
    }

    /**
     * Compara este filme com outro objeto para verificar igualdade.
     * Dois filmes são considerados iguais se possuem mesmo nome e ano.
     * 
     * @param o Objeto a ser comparado
     * @return true se os filmes são iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Filme)) return false;
        Filme f = (Filme) o;
        return ano == f.ano && nome.equalsIgnoreCase(f.nome);
    }

    /**
     * Retorna um código hash para o filme, baseado no nome (case insensitive).
     * 
     * @return Código hash do filme
     */
    @Override
    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }
}
package br.com.mythe.droid.common.transacao;

public interface Transacao {
        // Executar a transa��o em uma thread separada
        public void executar() throws Exception;

        // Atualizar a view sincronizado com a thread principal
        public void atualizarView();
}
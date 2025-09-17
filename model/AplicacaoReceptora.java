/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: AplicacaoReceptora.java
* Funcao: Simular o funcionamento da aplicacao receptora
********************************************************************/

package model;

import control.ControllerPrincipal;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class AplicacaoReceptora {
  private ControllerPrincipal cP = new ControllerPrincipal();
  private Text texto;
  private ImageView botaoEnviar;
  private ImageView botaoVoltar;
  private ImageView fundo;

  public AplicacaoReceptora(ControllerPrincipal cP, Text texto, ImageView botaoEnviar, ImageView botaoVoltar,
      ImageView fundo) {
    this.cP = cP;
    this.texto = texto;
    this.botaoEnviar = botaoEnviar;
    this.botaoVoltar = botaoVoltar;
    this.fundo = fundo;
  }

  /********************************************************************
   * Metodo: aplicacaoReceptora
   * Funcao: atualiza a mensagem recebida na tela
   * Parametros: mensagem (string)
   * Retorno:
   ********************************************************************/
  public void aplicacaoReceptora(String mensagem) {
    int linhas = (int) Math.ceil(mensagem.length() / 21f);
    int altura = (linhas * 19) + 6;
    fundo.setFitHeight(altura);
    fundo.setLayoutY((fundo.getLayoutY() - altura) + 23);
    texto.setLayoutY(fundo.getLayoutY() + 14);
    texto.setText(mensagem);
    fundo.setVisible(true);
    texto.setVisible(true);
    botaoEnviar.setDisable(false);
    botaoVoltar.setVisible(true);
    botaoVoltar.setDisable(false);
  }
}

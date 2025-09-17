/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: AplicacaoTransmissora.java
* Funcao: Simular o funcionamento da aplicacao transmissora
********************************************************************/

package model;

import control.ControllerPrincipal;

public class AplicacaoTransmissora {

  private ControllerPrincipal cP;

  public AplicacaoTransmissora(ControllerPrincipal cP) {
    this.cP = cP;
  }

  /********************************************************************
   * Metodo: aplicacaoTransmissora
   * Funcao: atualiza a mensagem enviada na tela
   * Parametros: mensagem (string)
   * Retorno: void
   ********************************************************************/
  public void aplicacaoTransmissora(String mensagem) {
    cP.setMensagemTransmissor(mensagem);
    ControllerPrincipal.cAT.camadaAplicacaoTransmissora(mensagem);
  }
}
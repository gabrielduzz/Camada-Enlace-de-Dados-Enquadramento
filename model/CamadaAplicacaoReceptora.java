/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: CamadaAplicacaoReceptora.java
* Funcao: Simular o funcionamento da camada de aplicacao receptora
********************************************************************/

package model;

import control.ControllerPrincipal;

import java.lang.Math;

public class CamadaAplicacaoReceptora {
  private ControllerPrincipal cP = new ControllerPrincipal();

  public CamadaAplicacaoReceptora(ControllerPrincipal cP) {
    this.cP = cP;
  }

  /********************************************************************
   * Metodo: camadaAplicacaoReceptora
   * Funcao: transforma o quadro de bits recebido na mensagem originalmente
   * enviada
   * Parametros: quadro (int[])
   * Retorno:
   ********************************************************************/
  public void camadaAplicacaoReceptora(int[] quadro) {
    int deslocamento = 31;
    int idxChar = 7;
    int idxQuadro = 0;
    int codUnicode = 0;
    int bit;
    int proximoChar;
    StringBuilder mensagem = new StringBuilder();

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadro++;
        deslocamento = 31;
      }
      if (i % 8 == 0 && i != 0) {
        if (codUnicode == 0) {
          break;
        }
        idxChar = 7;
        mensagem.append((char) codUnicode);
        codUnicode = 0;
      }

      bit = (quadro[idxQuadro] & (1 << deslocamento)) >> deslocamento;
      if (bit == -1) {
        bit = 1;
      }
      codUnicode += bit * ((int) Math.pow(2, idxChar));

      deslocamento--;
      idxChar--;
    }
    if (codUnicode != 0) {
      mensagem.append((char) codUnicode);
    }

    ControllerPrincipal.aR.aplicacaoReceptora(mensagem.toString());
  }
}

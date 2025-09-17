/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: CamadaAplicacaoTransmissora.java
* Funcao: Simular o funcionamento da camada de aplicacao transmissora
********************************************************************/

package model;

import control.ControllerPrincipal;
import java.lang.StringBuilder;
import java.lang.Math;

public class CamadaAplicacaoTransmissora {

  private ControllerPrincipal cP;

  public CamadaAplicacaoTransmissora(ControllerPrincipal cP) {
    this.cP = cP;
  }

  /********************************************************************
   * Metodo: camadaAplicacaoTransmissora
   * Funcao: monta o quadro da mensagem manipulando os bits da mensagem
   * Parametros: mensagem (string)
   * Retorno: void
   ********************************************************************/
  public void camadaAplicacaoTransmissora(String mensagem) {
    StringBuilder binario = new StringBuilder();
    int[] quadro = new int[mensagem.length() % 4 == 0 ? mensagem.length() / 4 : mensagem.length() / 4 + 1];
    int idxQuadro = 0;
    int deslocamento = 31;
    quadro[idxQuadro] = 0;

    char[] caracteres = mensagem.toCharArray();
    int mascara = 1 << 7;
    for (char c : caracteres) {
      int val = c;
      for (int i = 0; i < 8; i++) {
        binario.append((val & mascara) == 0 ? 0 : 1);
        val <<= 1;
      }
    }

    for (int j = 0; j < binario.length(); j++) {
      if (j % 32 == 0 && j != 0) {
        deslocamento = 31;
        idxQuadro++;
        quadro[idxQuadro] = 0;
      }
      quadro[idxQuadro] |= Character.getNumericValue(binario.charAt(j)) << deslocamento;
      deslocamento--;
    }

    ControllerPrincipal.cEDT.camadaEnlaceDadosTransmissora(quadro);
  }
}

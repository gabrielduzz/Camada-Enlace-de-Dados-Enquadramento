/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: CamadaFisicaReceptora.java
* Funcao: Simular o funcionamento da camada fisica receptora
********************************************************************/

package model;

import control.ControllerPrincipal;

import java.lang.Math;
import java.util.Arrays;

public class CamadaFisicaReceptora {
  private ControllerPrincipal cP = new ControllerPrincipal();

  public CamadaFisicaReceptora(ControllerPrincipal cP) {
    this.cP = cP;
  }

  /********************************************************************
   * Metodo: camadaFisicaReceptora
   * Funcao: decodifica a mensagem e monta o quadro com base no fluxo bruto
   * de bits recebido
   * Parametros: fluxoBrutoDeBits (int[])
   * Retorno:
   ********************************************************************/
  public void camadaFisicaReceptora(int[] fluxoBrutoDeBits) {
    int codificacao = cP.getCodificacao();
    int[] quadro = new int[codificacao == 0 ? fluxoBrutoDeBits.length : (int) Math.ceil(fluxoBrutoDeBits.length / 2f)];
    if (cP.getEnquadramento() == 3) {
      fluxoBrutoDeBits = desenquadramentoViolacaoCamadaFisica(fluxoBrutoDeBits);
    }

    switch (codificacao) {
      case 0:
        quadro = camadaFisicaReceptoraDecodificacaoBinaria(fluxoBrutoDeBits);
        break;
      case 1:
        quadro = camadaFisicaReceptoraDecodificacaoManchester(fluxoBrutoDeBits);
        break;
      case 2:
        quadro = camadaFisicaReceptoraDecodificacaoManchesterDiferencial(fluxoBrutoDeBits);
        break;
    }

    ControllerPrincipal.cEDR.camadaEnlaceDadosReceptora(quadro);
  }

  public int[] camadaFisicaReceptoraDecodificacaoBinaria(int[] fluxoBrutoDeBits) {
    return fluxoBrutoDeBits;
  }

  public int[] camadaFisicaReceptoraDecodificacaoManchester(int[] fluxoBrutoDeBits) {
    int[] quadroDecodificado = new int[(int) Math.ceil(fluxoBrutoDeBits.length / 2f)];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int proximosBits;

    for (int i = 0; i < fluxoBrutoDeBits.length * 16; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadro++;
        deslocamentoQuadro = 31;
      }
      if (i % 16 == 0 && i != 0) {
        idxFluxo++;
        deslocamentoFluxo = 31;
      }
      if (i % 2 == 0) {
        int bit1 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo);
        int bit2 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo - 1)) >> deslocamentoFluxo - 1);
        proximosBits = bit1 + bit2;
        if (proximosBits == 0) {
          break;
        }
      }

      bit = (fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo;
      bit = Math.abs(bit);
      quadroDecodificado[idxQuadro] |= bit << deslocamentoQuadro;
      deslocamentoQuadro--;
      deslocamentoFluxo -= 2;
    }

    return quadroDecodificado;
  }

  public int[] camadaFisicaReceptoraDecodificacaoManchesterDiferencial(int[] fluxoBrutoDeBits) {
    int[] quadroDecodificado = new int[(int) Math.ceil(fluxoBrutoDeBits.length / 2f)];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int sinalAnterior = 0;
    int proximosBits;

    for (int i = 0; i < fluxoBrutoDeBits.length * 16; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadro++;
        deslocamentoQuadro = 31;
      }
      if (i % 16 == 0 && i != 0) {
        idxFluxo++;
        deslocamentoFluxo = 31;
      }
      if (i % 2 == 0) {
        int bit1 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo);
        int bit2 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo - 1)) >> deslocamentoFluxo - 1);
        proximosBits = bit1 + bit2;
        if (proximosBits == 0) {
          break;
        }
      }

      bit = (fluxoBrutoDeBits[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo;
      bit = Math.abs(bit);
      if (bit != sinalAnterior) {
        quadroDecodificado[idxQuadro] |= 1 << deslocamentoQuadro;
        sinalAnterior = 1 - sinalAnterior;
      }
      deslocamentoQuadro--;
      deslocamentoFluxo -= 2;
    }

    return quadroDecodificado;
  }

  private int[] desenquadramentoViolacaoCamadaFisica(int[] fluxoBrutoDeBits) {
    int deslocamentoFluxoBruto = 31;
    int deslocamentoFluxoDesenquadrado = 31;
    int idxFluxoBruto = 0;
    int idxFluxoDesenquadrado = 0;
    int bit;
    int proximosBits;
    int[] fluxoDesenquadrado = new int[fluxoBrutoDeBits.length];

    for (int i = 0; i < fluxoBrutoDeBits.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxFluxoBruto++;
        deslocamentoFluxoBruto = 31;
      }

      if (deslocamentoFluxoDesenquadrado < 0) {
        idxFluxoDesenquadrado++;
        deslocamentoFluxoDesenquadrado = 31;
      }

      if (i % 2 == 0) {
        int bit1 = Math
            .abs((fluxoBrutoDeBits[idxFluxoBruto] & (1 << deslocamentoFluxoBruto)) >> deslocamentoFluxoBruto);
        int bit2 = Math
            .abs((fluxoBrutoDeBits[idxFluxoBruto] & (1 << deslocamentoFluxoBruto - 1)) >> deslocamentoFluxoBruto - 1);
        proximosBits = bit1 + bit2;
        if (proximosBits == 0) {
          break;
        }
        if (proximosBits == 2) {
          i++;
          deslocamentoFluxoBruto -= 2;
          continue;
        }
      }

      bit = (fluxoBrutoDeBits[idxFluxoBruto] & (1 << deslocamentoFluxoBruto)) >> deslocamentoFluxoBruto;
      bit = Math.abs(bit);
      fluxoDesenquadrado[idxFluxoDesenquadrado] |= bit << deslocamentoFluxoDesenquadrado;
      deslocamentoFluxoDesenquadrado--;
      deslocamentoFluxoBruto--;
    }

    fluxoDesenquadrado = removeIndexVazioHelper(fluxoDesenquadrado);

    return fluxoDesenquadrado;
  }

  private int[] removeIndexVazioHelper(int[] quadro) {
    int novoIndex = 0;
    for (int j = 0; j < quadro.length; j++) {
      if (!(quadro[j] == 0)) {
        quadro[novoIndex++] = quadro[j];
      }
    }
    quadro = Arrays.copyOf(quadro, novoIndex);
    return quadro;
  }

}

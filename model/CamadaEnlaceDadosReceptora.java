/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 28/04/2024
* Ultima alteracao: 05/05/2024
* Nome: CamadaEnlaceDadosReceptora.java
* Funcao: Simular o funcionamento da camada de enlace de dados receptora
********************************************************************/

package model;

import java.util.Arrays;

import control.ControllerPrincipal;

public class CamadaEnlaceDadosReceptora {
  private ControllerPrincipal cP;

  public CamadaEnlaceDadosReceptora(ControllerPrincipal cP) {
    this.cP = cP;
  }

  public void camadaEnlaceDadosReceptora(int[] quadro) {
    camadaDeEnlaceReceptoraEnquadramento(quadro);
  }

  /********************************************************************
   * Metodo: camadaEnlaceDadosReceptoraEnquadramento
   * Funcao: faz o desenquadramento da mensagem com base no método escolhido
   * Parametros: quadro (int[])
   * Retorno: void
   ********************************************************************/
  public void camadaDeEnlaceReceptoraEnquadramento(int[] quadro) {
    int tipoDeEnquadramento = cP.getEnquadramento();
    int[] quadroDesenquadrado;
    switch (tipoDeEnquadramento) {
      case 0: {
        quadroDesenquadrado = camadaDeEnlaceReceptoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      }
      case 1: {
        quadroDesenquadrado = camadaDeEnlaceReceptoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      }
      case 2: {
        quadroDesenquadrado = camadaDeEnlaceReceptoraEnquadramentoInsercaoDeBits(quadro);
        break;
      }
      case 3: {
        quadroDesenquadrado = camadaDeEnlaceReceptoraEnquadramentoViolacaoCamadaFisica(quadro);
        break;
      }
      default: {
        quadroDesenquadrado = new int[quadro.length];
        break;
      }
    }

    ControllerPrincipal.cAR.camadaAplicacaoReceptora(quadroDesenquadrado);
  }

  private int[] camadaDeEnlaceReceptoraEnquadramentoContagemDeCaracteres(int[] quadro) {
    int[] quadroDesenquadrado = new int[quadro.length];
    int deslocamentoQuadroOriginal = 31;
    int deslocamentoQuadroDesenquadrado = 31;
    int idxQuadroOriginal = 0;
    int idxQuadroDesenquadrado = 0;
    int bit;
    int contCaracteres = 1;

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadroOriginal++;
        deslocamentoQuadroOriginal = 31;
      }

      if (deslocamentoQuadroDesenquadrado < 0) {
        idxQuadroDesenquadrado++;
        deslocamentoQuadroDesenquadrado = 31;
      }

      if (i % 8 == 0) {
        contCaracteres--;
        if (contCaracteres == 0) {
          contCaracteres = ((quadro[idxQuadroOriginal] >> (deslocamentoQuadroOriginal - 7)) & 255) - 1;
          if (contCaracteres == -1) {
            break;
          }
          i += 8;
          deslocamentoQuadroOriginal -= 8;
        }
      }

      bit = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
      bit = Math.abs(bit);
      quadroDesenquadrado[idxQuadroDesenquadrado] |= (bit) << deslocamentoQuadroDesenquadrado;

      deslocamentoQuadroDesenquadrado--;
      deslocamentoQuadroOriginal--;
    }

    quadroDesenquadrado = removeIndexVazioHelper(quadroDesenquadrado);

    return quadroDesenquadrado;
  }

  private int[] camadaDeEnlaceReceptoraEnquadramentoInsercaoDeBytes(int[] quadro) {
    final char FLAG = 'F';
    final char ESCAPE = '|';
    int[] quadroDesenquadrado = new int[quadro.length];
    int deslocamentoQuadroOriginal = 31;
    int deslocamentoQuadroDesenquadrado = 31;
    int idxQuadroOriginal = 0;
    int idxQuadroDesenquadrado = 0;
    int proximoChar;
    int bit;
    boolean byteFake = false;
    int idxByteFake = 0;

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadroOriginal++;
        deslocamentoQuadroOriginal = 31;
      }

      if (deslocamentoQuadroDesenquadrado < 0) {
        idxQuadroDesenquadrado++;
        deslocamentoQuadroDesenquadrado = 31;
      }

      if (byteFake && i == idxByteFake + 8) {
        byteFake = false;
      }

      if (i % 8 == 0) {
        proximoChar = (quadro[idxQuadroOriginal] >> (deslocamentoQuadroOriginal - 7)) & 255;
        if (proximoChar == (int) FLAG && !byteFake) {
          i += 7;
          deslocamentoQuadroOriginal -= 8;
          continue;
        }
        if (proximoChar == (int) ESCAPE && !byteFake) {
          deslocamentoQuadroOriginal -= 8;
          byteFake = true;
          idxByteFake = i + 8;
          i += 7;
          continue;
        }
        if (proximoChar == 0) {
          break;
        }
      }

      bit = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
      bit = Math.abs(bit);
      quadroDesenquadrado[idxQuadroDesenquadrado] |= (bit) << deslocamentoQuadroDesenquadrado;

      deslocamentoQuadroDesenquadrado--;
      deslocamentoQuadroOriginal--;
    }

    quadroDesenquadrado = removeIndexVazioHelper(quadroDesenquadrado);

    return quadroDesenquadrado;
  }

  private int[] camadaDeEnlaceReceptoraEnquadramentoInsercaoDeBits(int[] quadro) {
    final char FLAG = '~';
    int[] quadroDesenquadrado = new int[quadro.length];
    int deslocamentoQuadroOriginal = 31;
    int deslocamentoQuadroDesenquadrado = 31;
    int idxQuadroOriginal = 0;
    int idxQuadroDesenquadrado = 0;
    int bit;
    int ponteiroFlag = 7;
    int contBits1 = 0;
    int proximosBits;
    int auxIdx;
    int auxDeslocamento;
    boolean vazio = true;
    StringBuilder sequenciaAtual = new StringBuilder();
    boolean passouFlag = false;

    for (int i = 0; i < quadro.length * 32; i++) {
      // if (i % 32 == 0 && i != 0) {
      // idxQuadroOriginal++;
      // deslocamentoQuadroOriginal = 31;
      // }

      if (deslocamentoQuadroOriginal < 0) {
        deslocamentoQuadroOriginal = 31;
        idxQuadroOriginal++;
      }

      bit = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
      bit = Math.abs(bit);
      deslocamentoQuadroOriginal--;
      contBits1 = bit == 1 ? contBits1 + 1 : 0;

      if (contBits1 == 5) {
        proximosBits = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
        if (proximosBits == 0) {
          i++;
          deslocamentoQuadroOriginal--;
          contBits1 = 0;
          ponteiroFlag = 7;
        }
      }

      if (bit == ((FLAG >> ponteiroFlag) & 1) && ponteiroFlag >= 0) {
        if (ponteiroFlag == 7 && sequenciaAtual.length() != 0) {
          vazio = false;
        }
        sequenciaAtual.append(bit);
        ponteiroFlag--;
        continue;
      }

      if (ponteiroFlag <= 0) {
        passouFlag = !passouFlag;
        ponteiroFlag = 7;
        if (!vazio) {
          quadroDesenquadrado[idxQuadroDesenquadrado] |= sequenciaAtual.charAt(0) == '0' ? 0
              : 1 << deslocamentoQuadroDesenquadrado;
        }
        sequenciaAtual.delete(0, 8);
        vazio = true;
      }

      for (int j = 0; j < sequenciaAtual.length(); j++) {
        if (deslocamentoQuadroDesenquadrado < 0) {
          deslocamentoQuadroDesenquadrado = 31;
          idxQuadroDesenquadrado++;
        }
        quadroDesenquadrado[idxQuadroDesenquadrado] |= sequenciaAtual
            .charAt(j) == '0' ? 0 : 1 << deslocamentoQuadroDesenquadrado;
        deslocamentoQuadroDesenquadrado--;
      }

      sequenciaAtual.delete(0, sequenciaAtual.length());
      ponteiroFlag = bit == 0 ? 6 : 7;
      sequenciaAtual.append(bit);

      if (!passouFlag) {
        int bit1 = Math
            .abs((quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal);

        if ((deslocamentoQuadroOriginal - 2) < 0) {
          auxIdx = idxQuadroOriginal + 1;
          auxDeslocamento = 30;
        } else {
          auxIdx = idxQuadroOriginal;
          auxDeslocamento = deslocamentoQuadroOriginal - 2;
        }
        int bit2 = Math.abs((quadro[auxIdx] & (1 << auxDeslocamento)) >> auxDeslocamento);
        proximosBits = bit1 + bit2;
        if (proximosBits == 0) {
          break;
        }
      }
    }

    quadroDesenquadrado = removeIndexVazioHelper(quadroDesenquadrado);

    return quadroDesenquadrado;
  }

  private int[] camadaDeEnlaceReceptoraEnquadramentoViolacaoCamadaFisica(int[] quadro) {
    return quadro;
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

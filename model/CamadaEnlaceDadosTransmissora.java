/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 28/04/2024
* Ultima alteracao: 05/05/2024
* Nome: CamadaEnlaceDadosTransmissora.java
* Funcao: Simular o funcionamento da camada de enlace de dados transmissora
********************************************************************/

package model;

import java.util.Arrays;

import javafx.scene.control.TextArea;
import javafx.util.Pair;
import control.ControllerPrincipal;

public class CamadaEnlaceDadosTransmissora {
  private ControllerPrincipal cP;
  private TextArea textoEnquadramento;

  public CamadaEnlaceDadosTransmissora(ControllerPrincipal cP, TextArea textoEnquadramento) {
    this.cP = cP;
    this.textoEnquadramento = textoEnquadramento;
  }

  public void camadaEnlaceDadosTransmissora(int[] quadro) {
    camadaEnlaceDadosTransmissoraEnquadramento(quadro);
  }

  /********************************************************************
   * Metodo: camadaEnlaceDadosTransmissoraEnquadramento
   * Funcao: faz o enquadramento da mensagem com base no método escolhido
   * Parametros: quadro (int[])
   * Retorno: void
   ********************************************************************/
  public void camadaEnlaceDadosTransmissoraEnquadramento(int[] quadro) {
    int tipoDeEnquadramento = cP.getEnquadramento();
    int[] quadroEnquadrado;
    switch (tipoDeEnquadramento) {
      case 0: {
        quadroEnquadrado = camadaDeEnlaceTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      }
      case 1: {
        quadroEnquadrado = camadaDeEnlaceTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      }
      case 2: {
        quadroEnquadrado = camadaDeEnlaceTransmissoraEnquadramentoInsercaoDeBits(quadro);
        break;
      }
      case 3: {
        quadroEnquadrado = camadaDeEnlaceTransmissoraEnquadramentoViolacaoCamadaFisica(quadro);
        break;
      }
      default: {
        quadroEnquadrado = new int[quadro.length * 3];
      }
    }

    textoEnquadramento.setText(setStringQuadro(quadroEnquadrado));
    textoEnquadramento.setVisible(true);

    ControllerPrincipal.cFT.camadaFisicaTransmissora(quadroEnquadrado);
  }

  private int[] camadaDeEnlaceTransmissoraEnquadramentoContagemDeCaracteres(int[] quadro) {
    final int TAMANHO_QUADRO = 3;
    int deslocamentoQuadroOriginal = 31;
    int deslocamentoQuadroEnquadrado = 31;
    int idxQuadroOriginal = 0;
    int idxQuadroEnquadrado = 0;
    int bit;
    int proximoChar;
    int idxFim = quadro.length * 32;

    for (int i = 0; i < quadro.length * 32; i += 8) {
      if (i % 32 == 0 && i != 0) {
        idxQuadroOriginal++;
        deslocamentoQuadroOriginal = 31;
      }
      proximoChar = (quadro[idxQuadroOriginal] >> (deslocamentoQuadroOriginal - 7)) & 255;
      if (proximoChar == 0) {
        idxFim = i;
        break;
      }
      deslocamentoQuadroOriginal -= 8;
    }

    int numCaracteresTotal = idxFim / 8;
    int[] quadroEnquadrado;

    quadroEnquadrado = new int[numCaracteresTotal % 3 == 0 ? numCaracteresTotal / 3 : numCaracteresTotal / 3 + 1];

    idxQuadroOriginal = 0;
    deslocamentoQuadroOriginal = 31;

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadroOriginal++;
        deslocamentoQuadroOriginal = 31;
      }

      if (i % 8 == 0) {
        proximoChar = (quadro[idxQuadroOriginal] >> (deslocamentoQuadroOriginal - 7)) & 255;
        if (proximoChar == 0) {
          break;
        }
      }

      if (i % 24 == 0) {
        if (i != 0) {
          idxQuadroEnquadrado++;
          deslocamentoQuadroEnquadrado = 31;
        }

        int valorContagem = (Math.min(TAMANHO_QUADRO, numCaracteresTotal - (i / 8))) + 1;

        for (int j = 7; j >= 0; j--) {
          int aux = valorContagem >> j;
          quadroEnquadrado[idxQuadroEnquadrado] |= (aux & 1) << deslocamentoQuadroEnquadrado;
          deslocamentoQuadroEnquadrado--;
        }
      }

      bit = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
      bit = Math.abs(bit);
      quadroEnquadrado[idxQuadroEnquadrado] |= (bit) << deslocamentoQuadroEnquadrado;

      deslocamentoQuadroEnquadrado--;
      deslocamentoQuadroOriginal--;
    }

    return quadroEnquadrado;
  }

  private int[] camadaDeEnlaceTransmissoraEnquadramentoInsercaoDeBytes(int[] quadro) {
    final int TAMANHO_QUADRO = 3;
    final char FLAG = 'F';
    final char ESCAPE = '|';

    int[] quadroEnquadrado = new int[quadro.length * TAMANHO_QUADRO];
    int deslocamentoQuadroOriginal = 31;
    int deslocamentoQuadroEnquadrado = 31;
    int idxQuadroOriginal = 0;
    int idxQuadroEnquadrado = 0;
    int bit;
    int proximoChar;
    int contCaracteres = 0;
    Pair<Integer, Integer> par_idx_e_quadro;

    par_idx_e_quadro = insereByteHelper(quadroEnquadrado, FLAG, idxQuadroEnquadrado,
        deslocamentoQuadroEnquadrado);
    idxQuadroEnquadrado = par_idx_e_quadro.getKey();
    deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();
    contCaracteres++;

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadroOriginal++;
        deslocamentoQuadroOriginal = 31;
      }

      if (i % 24 == 0 && i != 0) {
        for (int k = 0; k < 2; k++) {
          if (contCaracteres == 4) {
            idxQuadroEnquadrado++;
            deslocamentoQuadroEnquadrado = 31;
            contCaracteres = contCaracteres % 4;
          }
          par_idx_e_quadro = insereByteHelper(quadroEnquadrado, FLAG, idxQuadroEnquadrado,
              deslocamentoQuadroEnquadrado);
          idxQuadroEnquadrado = par_idx_e_quadro.getKey();
          deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();

          contCaracteres++;
        }
      }

      if (i % 8 == 0) {
        proximoChar = (quadro[idxQuadroOriginal] >> (deslocamentoQuadroOriginal - 7)) & 255;
        if (proximoChar == FLAG || proximoChar == ESCAPE) {
          par_idx_e_quadro = insereByteHelper(quadroEnquadrado, ESCAPE, idxQuadroEnquadrado,
              deslocamentoQuadroEnquadrado);
          idxQuadroEnquadrado = par_idx_e_quadro.getKey();
          deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();

          contCaracteres++;
        }

        contCaracteres++;

        if (contCaracteres > 4) {
          idxQuadroEnquadrado++;
          deslocamentoQuadroEnquadrado = 31;
          contCaracteres = contCaracteres % 4;
        }
      }

      bit = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
      bit = Math.abs(bit);
      quadroEnquadrado[idxQuadroEnquadrado] |= (bit) << deslocamentoQuadroEnquadrado;

      deslocamentoQuadroEnquadrado--;
      deslocamentoQuadroOriginal--;
    }

    contCaracteres++;

    if (contCaracteres > 4) {
      idxQuadroEnquadrado++;
      deslocamentoQuadroEnquadrado = 31;
      contCaracteres = contCaracteres % 4;
    }

    par_idx_e_quadro = insereByteHelper(quadroEnquadrado, FLAG, idxQuadroEnquadrado,
        deslocamentoQuadroEnquadrado);
    idxQuadroEnquadrado = par_idx_e_quadro.getKey();
    deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();
    quadroEnquadrado = removeIndexVazioHelper(quadroEnquadrado);

    return quadroEnquadrado;
  }

  private int[] camadaDeEnlaceTransmissoraEnquadramentoInsercaoDeBits(int[] quadro) {
    final char FLAG = '~';
    int[] quadroEnquadrado = new int[quadro.length * 3];
    int deslocamentoQuadroOriginal = 31;
    int deslocamentoQuadroEnquadrado = 31;
    int idxQuadroOriginal = 0;
    int idxQuadroEnquadrado = 0;
    int bit;
    int proximoChar;
    int auxIdx;
    int contBits1 = 0;
    Pair<Integer, Integer> par_idx_e_quadro;

    par_idx_e_quadro = insereByteHelper(quadroEnquadrado, FLAG, idxQuadroEnquadrado,
        deslocamentoQuadroEnquadrado);
    idxQuadroEnquadrado = par_idx_e_quadro.getKey();
    deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 8 == 0) {
        proximoChar = (quadro[idxQuadroOriginal] >> (deslocamentoQuadroOriginal - 7)) & 255;
        if (proximoChar == 0) {
          break;
        }
      }

      if (i % 32 == 0 && i != 0) {
        idxQuadroOriginal++;
        deslocamentoQuadroOriginal = 31;
      }

      if (deslocamentoQuadroEnquadrado < 0) {
        idxQuadroEnquadrado++;
        deslocamentoQuadroEnquadrado = 31;
      }

      if (i % 24 == 0 && i != 0) {
        for (int k = 0; k < 2; k++) {
          par_idx_e_quadro = insereByteHelper(quadroEnquadrado, FLAG, idxQuadroEnquadrado,
              deslocamentoQuadroEnquadrado);
          idxQuadroEnquadrado = par_idx_e_quadro.getKey();
          deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();
        }

        contBits1 = 0;
      }

      bit = (quadro[idxQuadroOriginal] & (1 << deslocamentoQuadroOriginal)) >> deslocamentoQuadroOriginal;
      bit = Math.abs(bit);
      quadroEnquadrado[idxQuadroEnquadrado] |= (bit) << deslocamentoQuadroEnquadrado;
      contBits1 = bit == 1 ? contBits1 + 1 : 0;

      if (contBits1 == 5) {
        deslocamentoQuadroEnquadrado--;
        contBits1 = 0;
      }

      if (deslocamentoQuadroEnquadrado < 0) {
        idxQuadroEnquadrado++;
        deslocamentoQuadroEnquadrado = 31;
      }

      deslocamentoQuadroEnquadrado--;
      deslocamentoQuadroOriginal--;

      if (deslocamentoQuadroEnquadrado < 0) {
        idxQuadroEnquadrado++;
        deslocamentoQuadroEnquadrado = 31;
      }
    }

    auxIdx = idxQuadroEnquadrado;
    contBits1 = 0;
    par_idx_e_quadro = insereByteHelper(quadroEnquadrado, FLAG, idxQuadroEnquadrado,
        deslocamentoQuadroEnquadrado);
    idxQuadroEnquadrado = par_idx_e_quadro.getKey();
    deslocamentoQuadroEnquadrado = par_idx_e_quadro.getValue();

    if (idxQuadroEnquadrado == auxIdx) {
      quadroEnquadrado = removeIndexVazioHelper(quadroEnquadrado);
    }

    return quadroEnquadrado;
  }

  private Pair<Integer, Integer> insereByteHelper(int[] quadroEnquadrado, char byteEscolhido, int idxQuadroEnquadrado,
      int deslocamentoQuadroEnquadrado) {
    int bit;
    for (int j = 7; j >= 0; j--) {
      // int aux = byteEscolhido >> j;
      bit = (byteEscolhido & (1 << j)) >> j;
      bit = Math.abs(bit);
      // quadroEnquadrado[idxQuadroEnquadrado] |= (aux & 1) <<
      // deslocamentoQuadroEnquadrado;
      quadroEnquadrado[idxQuadroEnquadrado] |= bit << deslocamentoQuadroEnquadrado;
      deslocamentoQuadroEnquadrado--;
      if (deslocamentoQuadroEnquadrado < 0) {
        idxQuadroEnquadrado++;
        deslocamentoQuadroEnquadrado = 31;
      }
    }
    return new Pair<Integer, Integer>(idxQuadroEnquadrado, deslocamentoQuadroEnquadrado);
  }

  private int[] removeIndexVazioHelper(int[] quadroEnquadrado) {
    int novoIndex = 0;
    for (int j = 0; j < quadroEnquadrado.length; j++) {
      if (!(quadroEnquadrado[j] == 0)) {
        quadroEnquadrado[novoIndex++] = quadroEnquadrado[j];
      }
    }
    quadroEnquadrado = Arrays.copyOf(quadroEnquadrado, novoIndex);
    return quadroEnquadrado;
  }

  private int[] camadaDeEnlaceTransmissoraEnquadramentoViolacaoCamadaFisica(int[] quadro) {
    return quadro;
  }

  private String setStringQuadro(int[] quadro) {
    final char FLAG = '~';
    int deslocamentoQuadro = 31;
    int idxQuadro = 0;
    int bit;
    int proximosBits;
    int ponteiroFlag = 7;
    int enquadramento = cP.getEnquadramento();
    // int contBits1 = 0;
    boolean passouFlag = false;
    String stringQuadro = "";

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadro++;
        deslocamentoQuadro = 31;
      }

      if (enquadramento != 2) {
        if (i % 8 == 0) {
          proximosBits = (quadro[idxQuadro] >> (deslocamentoQuadro - 7)) & 255;
          if (proximosBits == 0) {
            break;
          }
        }
      }

      bit = (quadro[idxQuadro] & (1 << deslocamentoQuadro)) >> deslocamentoQuadro;
      bit = Math.abs(bit);
      stringQuadro += bit;

      if (enquadramento == 2) {
        if (bit == ((FLAG >> ponteiroFlag) & 1)) {
          ponteiroFlag--;
        } else {
          ponteiroFlag = bit == 0 ? 6 : 7;
        }

        if (ponteiroFlag <= 0) {
          passouFlag = !passouFlag;
          ponteiroFlag = 7;
        }
        if (!passouFlag) {
          int bit1 = Math.abs((quadro[idxQuadro] & (1 << deslocamentoQuadro)) >> deslocamentoQuadro);
          int bit2 = Math.abs((quadro[idxQuadro] & (1 << deslocamentoQuadro - 2)) >> deslocamentoQuadro - 2);
          proximosBits = bit1 + bit2;
          if (proximosBits == 0) {
            break;
          }
        }
      }

      deslocamentoQuadro--;
    }

    return stringQuadro;
  }
}

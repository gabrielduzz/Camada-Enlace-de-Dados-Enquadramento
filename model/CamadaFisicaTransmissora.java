/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 05/05/2024
* Nome: CamadaFisicaTransmissora.java
* Funcao: Simular o funcionamento da camada fisica transmissora
********************************************************************/

package model;

import java.util.Arrays;
import control.ControllerPrincipal;
import javafx.scene.control.TextArea;

public class CamadaFisicaTransmissora {

  private ControllerPrincipal cP = new ControllerPrincipal();
  private TextArea textoFluxo;

  public CamadaFisicaTransmissora(ControllerPrincipal cP, TextArea textoFluxo) {
    this.cP = cP;
    this.textoFluxo = textoFluxo;
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissora
   * Funcao: codifica a mensagem e monta o fluxo bruto de bits com base
   * no quadro da mensagem
   * Parametros: quadro (int[])
   * Retorno: void
   ********************************************************************/
  public void camadaFisicaTransmissora(int[] quadro) {
    int codificacao = cP.getCodificacao();
    int enquadramento = cP.getEnquadramento();
    int[] fluxoBrutoDeBits = new int[codificacao == 0 ? quadro.length : (quadro.length) * 2];
    switch (codificacao) {
      case 0:
        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      case 1:
        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchester(quadro);
        break;
      case 2:
        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        break;
    }

    if (enquadramento != 3) {
      textoFluxo.setText(setStringFluxo(fluxoBrutoDeBits));
      textoFluxo.setVisible(true);
      ControllerPrincipal.mC.meioDeComunicacao(fluxoBrutoDeBits);
      return;
    }

    int[] fluxoEnquadrado = enquadramentoViolacaoCamadaFisica(fluxoBrutoDeBits);

    textoFluxo.setText(setStringFluxo(fluxoEnquadrado));
    textoFluxo.setVisible(true);

    ControllerPrincipal.mC.meioDeComunicacao(fluxoEnquadrado);
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoBinaria
   * Funcao: monta o fluxo bruto de bits com base na codificacao binaria
   * Parametros: quadro (int[])
   * Retorno: quadro (int[])
   ********************************************************************/
  private int[] camadaFisicaTransmissoraCodificacaoBinaria(int[] quadro) {
    return quadro;
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchester
   * Funcao: monta o fluxo bruto de bits com base na codificacao manchester
   * Parametros: quadro (int[])
   * Retorno: fluxoDeBits (int[])
   ********************************************************************/
  private int[] camadaFisicaTransmissoraCodificacaoManchester(int[] quadro) {
    final char FLAG = '~';
    int[] fluxoDeBits = new int[quadro.length * 2];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int ponteiroFlag = 7;
    int bit;
    int proximosBits;
    int enquadramento = cP.getEnquadramento();
    // int contBits1 = 0;
    boolean passouFlag = false;

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadro++;
        deslocamentoQuadro = 31;
      }
      if (i % 16 == 0 && i != 0) {
        idxFluxo++;
        deslocamentoFluxo = 31;
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
      fluxoDeBits[idxFluxo] |= (bit) << deslocamentoFluxo;
      fluxoDeBits[idxFluxo] |= (1 - bit) << deslocamentoFluxo - 1;

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
      deslocamentoFluxo -= 2;
    }

    fluxoDeBits = removeIndexVazioHelper(fluxoDeBits);

    return fluxoDeBits;
  }

  /********************************************************************
   * Metodo: camadaFisicaTransmissoraCodificacaoManchesterDiferencial
   * Funcao: monta o fluxo bruto de bits com base na codificacao manchester
   * diferencial
   * Parametros: quadro (int[])
   * Retorno: fluxoDeBits (int[])
   ********************************************************************/
  private int[] camadaFisicaTransmissoraCodificacaoManchesterDiferencial(int[] quadro) {
    final char FLAG = '~';
    int[] fluxoDeBits = new int[quadro.length * 2];
    int idxQuadro = 0;
    int idxFluxo = 0;
    int deslocamentoQuadro = 31;
    int deslocamentoFluxo = 31;
    int bit;
    int sinalAnterior = 1;
    int ponteiroFlag = 7;
    // int contBits1 = 0;
    int proximosBits;
    int enquadramento = cP.getEnquadramento();
    boolean passouFlag = false;

    for (int i = 0; i < quadro.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxQuadro++;
        deslocamentoQuadro = 31;
      }
      if (i % 16 == 0 && i != 0) {
        idxFluxo++;
        deslocamentoFluxo = 31;
      }

      if (i % 8 == 0) {
        proximosBits = (quadro[idxQuadro] >> (deslocamentoQuadro - 7)) & 255;
        if (proximosBits == 0) {
          break;
        }
      }

      bit = (quadro[idxQuadro] & (1 << deslocamentoQuadro)) >> deslocamentoQuadro;
      bit = Math.abs(bit);
      if (bit == 1) {
        fluxoDeBits[idxFluxo] |= (sinalAnterior) << deslocamentoFluxo;
        fluxoDeBits[idxFluxo] |= (1 - sinalAnterior) << deslocamentoFluxo - 1;
        sinalAnterior = 1 - sinalAnterior;
      } else {
        fluxoDeBits[idxFluxo] |= (1 - sinalAnterior) << deslocamentoFluxo;
        fluxoDeBits[idxFluxo] |= (sinalAnterior) << deslocamentoFluxo - 1;
      }

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
      deslocamentoFluxo -= 2;
    }

    fluxoDeBits = removeIndexVazioHelper(fluxoDeBits);

    return fluxoDeBits;
  }

  private int[] enquadramentoViolacaoCamadaFisica(int[] fluxoBrutoDeBits) {
    int deslocamentoFluxoBruto = 31;
    int deslocamentoFluxoEnquadrado = 31;
    int idxFluxoBruto = 0;
    int idxFluxoEnquadrado = 0;
    int bit;
    int proximosBits;
    int[] fluxoEnquadrado = new int[fluxoBrutoDeBits.length * 3];

    fluxoEnquadrado[idxFluxoEnquadrado] |= 1 << deslocamentoFluxoEnquadrado--;
    fluxoEnquadrado[idxFluxoEnquadrado] |= 1 << deslocamentoFluxoEnquadrado--;

    for (int i = 0; i < fluxoBrutoDeBits.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxFluxoBruto++;
        deslocamentoFluxoBruto = 31;
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
      }

      if (i % 48 == 0 && i != 0) {
        for (int k = 0; k < 2; k++) {
          fluxoEnquadrado[idxFluxoEnquadrado] |= 1 << deslocamentoFluxoEnquadrado--;
          fluxoEnquadrado[idxFluxoEnquadrado] |= 1 << deslocamentoFluxoEnquadrado--;
          if (deslocamentoFluxoEnquadrado < 0) {
            idxFluxoEnquadrado++;
            deslocamentoFluxoEnquadrado = 31;
          }
        }
      }

      bit = (fluxoBrutoDeBits[idxFluxoBruto] & (1 << deslocamentoFluxoBruto)) >> deslocamentoFluxoBruto;
      bit = Math.abs(bit);
      fluxoEnquadrado[idxFluxoEnquadrado] |= (bit) << deslocamentoFluxoEnquadrado;

      deslocamentoFluxoEnquadrado--;
      deslocamentoFluxoBruto--;

      if (deslocamentoFluxoEnquadrado < 0) {
        idxFluxoEnquadrado++;
        deslocamentoFluxoEnquadrado = 31;
      }
    }

    fluxoEnquadrado[idxFluxoEnquadrado] |= 1 << deslocamentoFluxoEnquadrado--;
    fluxoEnquadrado[idxFluxoEnquadrado] |= 1 << deslocamentoFluxoEnquadrado--;

    fluxoEnquadrado = removeIndexVazioHelper(fluxoEnquadrado);

    return fluxoEnquadrado;
  }

  private int[] removeIndexVazioHelper(int[] fluxo) {
    int novoIndex = 0;
    for (int j = 0; j < fluxo.length; j++) {
      if (!(fluxo[j] == 0)) {
        fluxo[novoIndex++] = fluxo[j];
      }
    }
    fluxo = Arrays.copyOf(fluxo, novoIndex);
    return fluxo;
  }

  private String setStringFluxo(int[] fluxo) {
    final char FLAG = '~';
    int deslocamentoFluxo = 31;
    int idxFluxo = 0;
    int bit;
    int proximosBits;
    int codificacao = cP.getCodificacao();
    int enquadramento = cP.getEnquadramento();
    int ponteiroFlag = 7;
    int auxIdx;
    int auxDeslocamento;
    boolean encerrarMeio = false;
    boolean passouFlag = false;
    String stringFluxo = "";

    for (int i = 0; i < fluxo.length * 32; i++) {
      if (i % 32 == 0 && i != 0) {
        idxFluxo++;
        deslocamentoFluxo = 31;
      }

      if (enquadramento == 2 || enquadramento == 3) {
        if (codificacao != 0) {
          if (i % 2 == 0) {
            int bit1 = Math.abs((fluxo[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo);
            int bit2 = Math.abs((fluxo[idxFluxo] & (1 << deslocamentoFluxo - 1)) >> deslocamentoFluxo - 1);
            proximosBits = bit1 + bit2;
            if (proximosBits == 0) {
              encerrarMeio = true;
            }
          }
        }
      }

      if (enquadramento == 0 || enquadramento == 1) {
        if (i % 8 == 0) {
          proximosBits = (fluxo[idxFluxo] >> (deslocamentoFluxo - 7)) & 255;
          if (proximosBits == 0) {
            encerrarMeio = true;
          }
        }
      }

      if (encerrarMeio) {
        break;
      }

      bit = (fluxo[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo;
      bit = Math.abs(bit);
      stringFluxo += bit;

      if (enquadramento == 2 && codificacao == 0) {
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
          int bit1 = Math.abs((fluxo[idxFluxo] & (1 << deslocamentoFluxo)) >> deslocamentoFluxo);
          if ((deslocamentoFluxo - 2) < 0) {
            auxIdx = idxFluxo + 1;
            auxDeslocamento = 30;
          } else {
            auxIdx = idxFluxo;
            auxDeslocamento = deslocamentoFluxo - 2;
          }
          int bit2 = Math.abs((fluxo[auxIdx] & (1 << auxDeslocamento)) >> auxDeslocamento);
          proximosBits = bit1 + bit2;
          if (proximosBits == 0) {
            break;
          }
        }
      }

      deslocamentoFluxo--;
    }

    return stringFluxo;
  }
}

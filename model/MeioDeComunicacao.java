/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: MeioDeComunicacao.java
* Funcao: Simular o funcionamento do meio de comunicacao
********************************************************************/

package model;

import control.ControllerPrincipal;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import java.lang.Thread;

public class MeioDeComunicacao {
  private ControllerPrincipal cP = new ControllerPrincipal();
  private Slider slider;
  private ImageView botaoVoltar;

  public MeioDeComunicacao(ControllerPrincipal cP, Slider slider, ImageView botaoVoltar) {
    this.cP = cP;
    this.slider = slider;
    this.botaoVoltar = botaoVoltar;
  }

  public int getVelocidade() {
    double velAux = slider.getValue();
    int vel = (int) velAux;
    return vel * 50;
  }

  /********************************************************************
   * Metodo: meioDeComunicacao
   * Funcao: simula o comportamento de onda na transmissão do fluxo bruto
   * de bits do ponto A para o ponto B
   * Parametros: fluxoBrutoDeBits (int[])
   * Retorno:
   ********************************************************************/
  public void meioDeComunicacao(int[] fluxoBrutoDeBits) {
    int[] fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length];
    int enquadramento = cP.getEnquadramento();
    int codificacao = cP.getCodificacao();
    fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
    botaoVoltar.setVisible(false);
    botaoVoltar.setDisable(true);
    new Thread(() -> {
      final char FLAG = '~';
      int deslocamento = 31;
      int idxFluxo = 0;
      int bit;
      int ponteiroFlag = 7;
      int ultimoSinal = -1;
      int proximosBits;
      int auxIdx;
      int auxDeslocamento;
      boolean passouFlag = false;
      boolean encerrarMeio = false;

      for (int i = 0; i < fluxoBrutoDeBitsPontoA.length * 32; i++) {
        if (i % 32 == 0 && i != 0) {
          idxFluxo++;
          deslocamento = 31;
        }

        if (enquadramento == 2 || enquadramento == 3) {
          if (codificacao != 0) {
            if (i % 2 == 0) {
              int bit1 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamento)) >> deslocamento);
              int bit2 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamento - 1)) >> deslocamento - 1);
              proximosBits = bit1 + bit2;
              if (proximosBits == 0) {
                encerrarMeio = true;
              }
            }
          }
        }

        if (enquadramento == 0 || enquadramento == 1) {
          if (i % 8 == 0) {
            proximosBits = (fluxoBrutoDeBits[idxFluxo] >> (deslocamento - 7)) & 255;
            if (proximosBits == 0) {
              encerrarMeio = true;
            }
          }
        }

        if (encerrarMeio) {
          break;
        }

        bit = (fluxoBrutoDeBitsPontoA[idxFluxo] & (1 << deslocamento)) >> deslocamento;
        bit = Math.abs(bit);
        fluxoBrutoDeBitsPontoB[idxFluxo] |= (bit) << deslocamento;

        cP.deslocaSinal();
        cP.atualizaSinal(bit, ultimoSinal);
        ultimoSinal = bit;

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
            int bit1 = Math.abs((fluxoBrutoDeBits[idxFluxo] & (1 << deslocamento)) >> deslocamento);
            if ((deslocamento - 2) < 0) {
              auxIdx = idxFluxo + 1;
              auxDeslocamento = 30;
            } else {
              auxIdx = idxFluxo;
              auxDeslocamento = deslocamento - 2;
            }
            int bit2 = Math.abs((fluxoBrutoDeBits[auxIdx] & (1 << auxDeslocamento)) >> auxDeslocamento);
            proximosBits = bit1 + bit2;
            if (proximosBits == 0) {
              break;
            }
          }
        }

        deslocamento--;

        try {
          Thread.sleep(getVelocidade());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      for (int j = 0; j < 8; j++) {
        cP.deslocaSinal();
        cP.removeSinal(j);
        try {
          Thread.sleep(getVelocidade());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      ControllerPrincipal.cFR.camadaFisicaReceptora(fluxoBrutoDeBitsPontoB);
    }).start();
  }
}

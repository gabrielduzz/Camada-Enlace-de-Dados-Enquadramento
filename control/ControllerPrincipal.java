/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: ControllerPrincipal.java
* Funcao: Gerenciar os objetos da cena principal
********************************************************************/

package control;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import model.*;

public class ControllerPrincipal implements Initializable {

  @FXML
  private ImageView telaPrincipal;
  @FXML
  private ImageView botaoEnviar;
  @FXML
  private ImageView botaoVoltar;
  @FXML
  private TextField campoDeTexto;
  @FXML
  private ImageView sinalAlto0;
  @FXML
  private ImageView sinalAlto1;
  @FXML
  private ImageView sinalAlto2;
  @FXML
  private ImageView sinalAlto3;
  @FXML
  private ImageView sinalAlto4;
  @FXML
  private ImageView sinalAlto5;
  @FXML
  private ImageView sinalAlto6;
  @FXML
  private ImageView sinalAlto7;
  @FXML
  private ImageView sinalBaixo0;
  @FXML
  private ImageView sinalBaixo1;
  @FXML
  private ImageView sinalBaixo2;
  @FXML
  private ImageView sinalBaixo3;
  @FXML
  private ImageView sinalBaixo4;
  @FXML
  private ImageView sinalBaixo5;
  @FXML
  private ImageView sinalBaixo6;
  @FXML
  private ImageView sinalBaixo7;
  @FXML
  private ImageView sinalTransicao0;
  @FXML
  private ImageView sinalTransicao1;
  @FXML
  private ImageView sinalTransicao2;
  @FXML
  private ImageView sinalTransicao3;
  @FXML
  private ImageView sinalTransicao4;
  @FXML
  private ImageView sinalTransicao5;
  @FXML
  private ImageView sinalTransicao6;
  @FXML
  private Slider slider;
  @FXML
  private Text textoMensagemReceptor;
  @FXML
  private Text textoMensagemTransmissor;
  @FXML
  private TextArea textoFluxo;
  @FXML
  private TextArea textoEnquadramento;
  @FXML
  private ImageView fundoMensagemReceptor;
  @FXML
  private ImageView fundoMensagemTransmissor;

  private int codificacao = ControllerInicial.getCodificacao();
  private int enquadramento = ControllerInicial.getEnquadramento();
  static ImageView[] sinaisAlto;
  static ImageView[] sinaisBaixo;
  static ImageView[] sinaisTransicao;

  public static AplicacaoTransmissora aT;
  public static CamadaAplicacaoTransmissora cAT;
  public static CamadaEnlaceDadosTransmissora cEDT;
  public static CamadaEnlaceDadosReceptora cEDR;
  public static CamadaFisicaTransmissora cFT;
  public static MeioDeComunicacao mC;
  public static CamadaFisicaReceptora cFR;
  public static CamadaAplicacaoReceptora cAR;
  public static AplicacaoReceptora aR;

  /********************************************************************
   * Metodo: enviar
   * Funcao: iniciar os procedimentos da simulacao do protocolo, chamando
   * a aplicacao transmissora
   * Parametros: event
   * Retorno: void
   ********************************************************************/
  @FXML
  void enviar(MouseEvent event) {
    if (!campoDeTexto.getText().isEmpty()) {
      String mensagem = campoDeTexto.getText();
      int linhas = (int) Math.ceil(mensagem.length() / 21f);
      if (linhas < 16) {
        int altura = (linhas * 19) + 6;
        fundoMensagemReceptor.setLayoutY(443);
        fundoMensagemTransmissor.setLayoutY(446);
        fundoMensagemTransmissor.setFitHeight(altura);
        fundoMensagemTransmissor.setLayoutY(fundoMensagemTransmissor.getLayoutY() - altura + 23);
        textoMensagemTransmissor.setLayoutY(fundoMensagemTransmissor.getLayoutY() + 14);
        textoMensagemReceptor.setVisible(false);
        fundoMensagemReceptor.setVisible(false);
        fundoMensagemTransmissor.setVisible(true);
        campoDeTexto.clear();
        for (int i = 0; i < 8; i++) {
          sinaisAlto[i].setVisible(false);
          sinaisBaixo[i].setVisible(false);
          if (i < 7) {
            sinaisTransicao[i].setVisible(false);
          }
        }
        aT.aplicacaoTransmissora(mensagem);
        botaoEnviar.setDisable(true);
      } else {
        System.out.println("LIMITE DE CARACTERES EXCEDIDO!");
      }
    }
  }

  /********************************************************************
   * Metodo: voltar
   * Funcao: retornar a cena inicial de escolha da codificacao
   * Parametros: event
   * Retorno: void
   ********************************************************************/
  @FXML
  void voltar(MouseEvent event) throws Exception {
    Stage stage = (Stage) botaoVoltar.getScene().getWindow();
    Scene cenaPrincipal = new Scene(createContent());
    stage.setScene(cenaPrincipal);
    textoFluxo.setVisible(false);
    textoEnquadramento.setVisible(false);
    textoMensagemReceptor.setVisible(false);
    textoMensagemTransmissor.setVisible(false);
    fundoMensagemReceptor.setVisible(false);
    fundoMensagemTransmissor.setVisible(false);
    slider.setValue(3.5);
    for (int i = 0; i < 8; i++) {
      sinaisAlto[i].setVisible(false);
      sinaisBaixo[i].setVisible(false);
      if (i < 7) {
        sinaisTransicao[i].setVisible(false);
      }
    }
  }

  public int getCodificacao() {
    return codificacao;
  }

  public int getEnquadramento() {
    return enquadramento;
  }

  public ImageView[] getSinaisAlto() {
    return sinaisAlto;
  }

  public ImageView[] getSinaisBaixo() {
    return sinaisBaixo;
  }

  public ImageView[] getSinaisTransicao() {
    return sinaisTransicao;
  }

  public void setMensagemTransmissor(String mensagem) {
    textoMensagemTransmissor.setText(mensagem);
    textoMensagemTransmissor.setVisible(true);
  }

  /********************************************************************
   * Metodo: deslocaSinal
   * Funcao: move os sinais no meio de comunicação para simular o movimento
   * da onda
   * Parametros:
   * Retorno: void
   ********************************************************************/
  public void deslocaSinal() {
    Platform.runLater(() -> {
      for (int i = 7; i > 0; i--) {
        sinaisAlto[i].setVisible(sinaisAlto[i - 1].isVisible());
        sinaisBaixo[i].setVisible(sinaisBaixo[i - 1].isVisible());
        if (i < 7) {
          sinaisTransicao[i].setVisible(sinaisTransicao[i - 1].isVisible());
        }
      }
    });
  }

  /********************************************************************
   * Metodo: removeSinal
   * Funcao: remove o sinal da posicao j na onda
   * Parametros: j (int)
   * Retorno: void
   ********************************************************************/
  public void removeSinal(int j) {
    Platform.runLater(() -> {
      sinaisAlto[j].setVisible(false);
      sinaisBaixo[j].setVisible(false);
      if (j < 7) {
        sinaisTransicao[j].setVisible(false);
      }
    });
  }

  /********************************************************************
   * Metodo: atualizaSinal
   * Funcao: insere o bit lido na ultima posicao da onda
   * Parametros: bit (int), ultimoSinal (int)
   * Retorno: void
   ********************************************************************/
  public void atualizaSinal(int bit, int ultimoSinal) {
    Platform.runLater(() -> {
      sinaisAlto[0].setVisible(false);
      sinaisBaixo[0].setVisible(false);
      sinaisTransicao[0].setVisible(false);

      if (bit == 0) {
        sinaisBaixo[0].setVisible(true);
      } else {
        sinaisAlto[0].setVisible(true);
      }

      if (bit != ultimoSinal && ultimoSinal != -1) {
        sinaisTransicao[0].setVisible(true);
      }
    });
  }

  @Override
  public void initialize(URL arg0, ResourceBundle rb) {
    sinaisAlto = new ImageView[] { sinalAlto0, sinalAlto1, sinalAlto2, sinalAlto3, sinalAlto4, sinalAlto5, sinalAlto6,
        sinalAlto7 };
    sinaisBaixo = new ImageView[] { sinalBaixo0, sinalBaixo1, sinalBaixo2, sinalBaixo3, sinalBaixo4, sinalBaixo5,
        sinalBaixo6,
        sinalBaixo7 };
    sinaisTransicao = new ImageView[] { sinalTransicao0, sinalTransicao1, sinalTransicao2, sinalTransicao3,
        sinalTransicao4,
        sinalTransicao5, sinalTransicao6 };
    aT = new AplicacaoTransmissora(this);
    cAT = new CamadaAplicacaoTransmissora(this);
    cEDT = new CamadaEnlaceDadosTransmissora(this, textoEnquadramento);
    cFT = new CamadaFisicaTransmissora(this, textoFluxo);
    mC = new MeioDeComunicacao(this, slider, botaoVoltar);
    cFR = new CamadaFisicaReceptora(this);
    cEDR = new CamadaEnlaceDadosReceptora(this);
    cAR = new CamadaAplicacaoReceptora(this);
    aR = new AplicacaoReceptora(this, textoMensagemReceptor, botaoEnviar, botaoVoltar, fundoMensagemReceptor);
  }

  /********************************************************************
   * Metodo: createContent
   * Funcao: carrega a tela inicial
   * Parametros:
   * Retorno: root (Parent)
   ********************************************************************/
  private Parent createContent() throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_inicial.fxml"));
    Pane root = loader.load();
    return root;
  }
}

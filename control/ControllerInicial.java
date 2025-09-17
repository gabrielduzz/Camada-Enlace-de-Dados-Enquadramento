/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: ControllerInicial.java
* Funcao: Gerenciar os objetos da cena inicial
********************************************************************/

package control;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.lang.System;

public class ControllerInicial implements Initializable {

  @FXML
  private ImageView botaoBinaria;
  @FXML
  private ImageView botaoContagemDeCaracteres;
  @FXML
  private ImageView botaoIniciar;
  @FXML
  private ImageView botaoInsercaoDeBits;
  @FXML
  private ImageView botaoInsercaoDeBytes;
  @FXML
  private ImageView botaoManchester;
  @FXML
  private ImageView botaoManchesterDif;
  @FXML
  private ImageView botaoFechar;
  @FXML
  private ImageView botaoViolacaoCamadaFisica;

  private static int codificacao = -1;
  private static int enquadramento = -1;

  public static int getCodificacao() {
    return codificacao;
  }

  public static int getEnquadramento() {
    return enquadramento;
  }

  /********************************************************************
   * Metodo: setCodificacao
   * Funcao: atualiza o valor da codificacao, sendo 0 para binaria, 1 para
   * manchester e 2 para manchester diferencial
   * Parametros: event
   * Retorno: void
   ********************************************************************/
  @FXML
  void setCodificacao(MouseEvent event) {
    String idBotao = event.getPickResult().getIntersectedNode().getId();
    switch (idBotao) {
      case "botaoBinaria": {
        codificacao = 0;
        botaoBinaria.setImage(new Image("/img/selecao binaria.png"));
        botaoManchester.setImage(new Image("/img/botao manchester.png"));
        botaoManchesterDif.setImage(new Image("/img/botao manchester dif.png"));
        break;
      }
      case "botaoManchester": {
        codificacao = 1;
        botaoManchester.setImage(new Image("/img/selecao manchester.png"));
        botaoBinaria.setImage(new Image("/img/botao binaria.png"));
        botaoManchesterDif.setImage(new Image("/img/botao manchester dif.png"));
        break;
      }
      case "botaoManchesterDif": {
        codificacao = 2;
        botaoManchesterDif.setImage(new Image("/img/selecao manchester dif.png"));
        botaoBinaria.setImage(new Image("/img/botao binaria.png"));
        botaoManchester.setImage(new Image("/img/botao manchester.png"));
        break;
      }
    }
  }

  @FXML
  void setEnquadramento(MouseEvent event) {
    String idBotao = event.getPickResult().getIntersectedNode().getId();
    switch (idBotao) {
      case "botaoContagemDeCaracteres": {
        enquadramento = 0;
        botaoContagemDeCaracteres.setImage(new Image("/img/selecao contagem de caracteres.png"));
        botaoInsercaoDeBytes.setImage(new Image("/img/botao insercao de bytes.png"));
        botaoInsercaoDeBits.setImage(new Image("/img/botao insercao de bits.png"));
        botaoViolacaoCamadaFisica.setImage(new Image("/img/botao violacao camada fisica.png"));
        break;
      }
      case "botaoInsercaoDeBytes": {
        enquadramento = 1;
        botaoContagemDeCaracteres.setImage(new Image("/img/botao contagem de caracteres.png"));
        botaoInsercaoDeBytes.setImage(new Image("/img/selecao insercao de bytes.png"));
        botaoInsercaoDeBits.setImage(new Image("/img/botao insercao de bits.png"));
        botaoViolacaoCamadaFisica.setImage(new Image("/img/botao violacao camada fisica.png"));
        break;
      }
      case "botaoInsercaoDeBits": {
        enquadramento = 2;
        botaoContagemDeCaracteres.setImage(new Image("/img/botao contagem de caracteres.png"));
        botaoInsercaoDeBytes.setImage(new Image("/img/botao insercao de bytes.png"));
        botaoInsercaoDeBits.setImage(new Image("/img/selecao insercao de bits.png"));
        botaoViolacaoCamadaFisica.setImage(new Image("/img/botao violacao camada fisica.png"));
        break;
      }
      case "botaoViolacaoCamadaFisica": {
        enquadramento = 3;
        botaoContagemDeCaracteres.setImage(new Image("/img/botao contagem de caracteres.png"));
        botaoInsercaoDeBytes.setImage(new Image("/img/botao insercao de bytes.png"));
        botaoInsercaoDeBits.setImage(new Image("/img/botao insercao de bits.png"));
        botaoViolacaoCamadaFisica.setImage(new Image("/img/selecao violacao camada fisica.png"));
        break;
      }
    }
  }

  /********************************************************************
   * Metodo: fechar
   * Funcao: fecha a tela
   * Parametros: event
   * Retorno: void
   ********************************************************************/
  @FXML
  void fechar(MouseEvent event) {
    System.exit(0);
  }

  /********************************************************************
   * Metodo: iniciar
   * Funcao: chamar a cena principal
   * Parametros: event
   * Retorno: void
   ********************************************************************/
  @FXML
  void iniciar(MouseEvent event) throws Exception {
    if ((codificacao != -1 && enquadramento != -1) && (codificacao != 0 || enquadramento != 3)) {
      Stage stage = (Stage) botaoIniciar.getScene().getWindow();
      Scene cenaPrincipal = new Scene(createContent());
      stage.setScene(cenaPrincipal);
    }
  }

  @Override
  public void initialize(URL arg0, ResourceBundle rb) {
    codificacao = -1;
    enquadramento = -1;
  }

  private Parent createContent() throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_principal.fxml"));
    Pane root = loader.load();
    return root;
  }

}

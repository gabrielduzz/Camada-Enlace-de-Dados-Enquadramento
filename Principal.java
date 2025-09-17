/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/03/2024
* Ultima alteracao: 07/04/2024
* Nome: Principal.java
* Funcao: Carregar a cena inicial
********************************************************************/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import control.ControllerPrincipal;
import control.ControllerInicial;

public class Principal extends Application {

  /********************************************************************
  * Metodo: createContent
  * Funcao: carregar a tela principal da simulacao, apos o usuario 
            clicar no botao iniciar
  * Parametros: nenhum
  * Retorno: root(tipo Parent) = os elementos da cena para ser carregada no stage principal, apos
             ser liberado o inicio da simulacao
  ********************************************************************/
  private Parent createContent() throws Exception{
    //carrega o arquivo FXML da simulacao
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/view_inicial.fxml"));
    //atribui ao elemento principal da cena e o retorna
    Pane root = loader.load();
    return root;
  } //fim do metodo createContent

  /********************************************************************
  * Metodo: start
  * Funcao: metodo padrao que tem a funcao de definir o container da 
            aplicacao
  * Parametros: primaryStage = janela principal
  * Retorno: void
  ********************************************************************/
  @Override
  public void start(Stage primaryStage) throws Exception {
    //cria o elemento principal da cena inicial
    Pane root = new Pane();
    root.setPrefSize(1024, 720);

    Scene scene = new Scene(createContent());

    primaryStage.setScene(scene);
    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setResizable(false);
    primaryStage.show();
  }//fim do metodo start

  public static void main(String[] args) {
    launch(args);
  }

}

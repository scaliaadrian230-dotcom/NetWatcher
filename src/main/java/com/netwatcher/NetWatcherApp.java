package com.netwatcher;
import com.netwatcher.controller.NetWatcherController;
import com.netwatcher.dao.Database;
import javafx.application.Application; import javafx.scene.Scene; import javafx.scene.control.Alert; import javafx.stage.Stage;
public class NetWatcherApp extends Application {
 public void start(Stage stage){try{Database.initialize();Scene scene=new Scene(new NetWatcherController().createView(),1200,720);scene.getRoot().setStyle("-fx-font-family: 'Segoe UI'; -fx-base: #172033; -fx-accent: #36d1b4;");stage.setTitle("NetWatcher | Home Network Monitor");stage.setMinWidth(900);stage.setMinHeight(600);stage.setScene(scene);stage.show();}catch(Exception e){new Alert(Alert.AlertType.ERROR,"Unable to start NetWatcher: "+e.getMessage()).showAndWait();}}
 public static void main(String[] args){launch(args);}
}

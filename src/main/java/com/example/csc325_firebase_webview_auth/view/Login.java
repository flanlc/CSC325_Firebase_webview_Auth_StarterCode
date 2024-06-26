package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;

public class Login extends Application {

    @FXML
    TextField email;
    @FXML
    TextField uid;
    @FXML
    Button loginButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    @FXML
    public void HandleMenuButton() {
        try {
            App.setRoot("/files/AccessFBView.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void HandleLoginButton(ActionEvent event) {
        try {
            UserRecord userEmail = FirebaseAuth.getInstance().getUserByEmail(email.getText());
            UserRecord userID = FirebaseAuth.getInstance().getUser(uid.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/AccessFBView.fxml"));
            Parent root = loader.load();
            AccessFBView controller = loader.getController();
            controller.autheticate(true);

            App.setRoot("/files/AccessFBView.fxml");
        } catch (FirebaseAuthException e) {
            System.out.println("ERROR: User Does Not Exist");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

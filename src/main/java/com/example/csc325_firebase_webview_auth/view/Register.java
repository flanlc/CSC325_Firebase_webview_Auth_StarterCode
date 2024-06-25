package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class Register extends Application {
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Button registerButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    @FXML
    public void HandleCloseButton() {
        try {
            App.setRoot("/files/AccessFBView.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void regRecord(ActionEvent event) {
        registerUser();
    }

    //random number for phone number for testing purposes. cant create two users with the same phone number
    public boolean registerUser() {
        Random ran = new Random();
        long dummyPhoneNumber = ran.nextLong() % 10000000000L;
        if(dummyPhoneNumber < 0) {
            dummyPhoneNumber = dummyPhoneNumber * -1;
        }
        String ranStr = String.format("%010d", dummyPhoneNumber);

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email.getText())
                .setEmailVerified(false)
                .setPassword(password.getText())
                .setPhoneNumber("+1" + ranStr)
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = App.fauth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }
}

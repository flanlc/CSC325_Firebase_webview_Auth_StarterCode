package com.example.csc325_firebase_webview_auth.view;//package modelview;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.cloud.storage.BlobId;
import com.google.cloud .storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.firebase.internal.FirebaseRequestInitializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class AccessFBView {
    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private TextArea outputField;
    @FXML
    private Button Register;
    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;
    private static boolean authentication = false;

    String fileName = null;

    Scanner inputScanner;


    @FXML
    private TableView<Person> table;
    @FXML
    private TableColumn<Person, String> name;
    @FXML
    private TableColumn<Person, String> major;
    @FXML
    private TableColumn<Person, String> age;




    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    @FXML
    void initialize() {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        major.setCellValueFactory(new PropertyValueFactory<>("major"));
        age.setCellValueFactory(new PropertyValueFactory<>("age"));
        table.setItems(listOfUsers);
    }

    public void autheticate(boolean auth) {
        authentication = auth;
        System.out.println(authentication);
    }

    @FXML
    private void addRecord(ActionEvent event) {
        if(authentication) {
            addData();
        }
        else {
            System.out.println("ERROR: User Not Logged In");
        }
    }

    @FXML
    private void readRecord(ActionEvent event) {
        if(authentication) {
            readFirebase();
        }
        else {
            System.out.println("ERROR: User Not Logged In");
        }
    }


    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    public void addData() {
        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }


    public boolean readFirebase() {
        key = false;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if (documents.size() > 0) {
                System.out.println("Outing....");
                for (QueryDocumentSnapshot document : documents) {
                    outputField.setText(outputField.getText() + document.getData().get("Name") + " , Major: " +
                            document.getData().get("Major") + " , Age: " +
                            document.getData().get("Age") + " \n ");
                    System.out.println(document.getId() + " => " + document.getData().get("Name"));
                    person = new Person(String.valueOf(document.getData().get("Name")),
                            document.getData().get("Major").toString(),
                            Integer.parseInt(document.getData().get("Age").toString()));
                    listOfUsers.add(person);
                }
            } else {
                System.out.println("No data");
            }
            key = true;

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }

    public void sendVerificationEmail() {
        try {
            UserRecord user = App.fauth.getUser("name");
            //String url = user.getPassword();

        } catch (Exception e) {
        }
    }

    public boolean registerUser(String email, String password) {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber("+11234567890")
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

    @FXML
    public void HandleRegButton(ActionEvent event) {
        try {
            App.setRoot("/files/Register.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void HandleLoginButton(ActionEvent event) {
        try {
            App.setRoot("/files/Login.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }






    @FXML
    public void handleImage()  {
        FileChooser fileOpener = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("*.jpg", "*.png");
        fileOpener.getExtensionFilters().add(filter);

        File current = null;
        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        fileOpener.setInitialDirectory(current);
        File selectedFile = fileOpener.showOpenDialog(null);


        if (selectedFile == null) {
        } else {
            fileName = selectedFile.getAbsolutePath();
        }

        if (selectedFile != null) {
            FileReader fr = null;
            try {
                fr = new FileReader(fileName);
            } catch (FileNotFoundException ex) {
            }
            inputScanner = new Scanner(fr);
            inputScanner.close();

            String bucket = "mod6csc325firebasejava.appspot.com";
            String blob = selectedFile.getName();
            Storage storage = StorageOptions.getDefaultInstance().getService();
            BlobId blobId = BlobId.of(bucket, blob);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
            try {
                storage.create(blobInfo, new FileInputStream(selectedFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }






















}

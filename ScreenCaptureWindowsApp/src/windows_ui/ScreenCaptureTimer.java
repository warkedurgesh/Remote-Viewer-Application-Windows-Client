/*This is the class that'll run.
* This class contains the UI elements(View of the MVP) */
package windows_ui;

import client.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ImageChunksMetaData;
import javafx.stage.StageStyle;
import utils.PhantomMouseListener;
import utils.Utils;

import java.awt.MenuItem;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ScreenCaptureTimer extends Application implements Client.View,
        ChangeListener<Boolean> {
    private static Client.ClientPresenterImpl clientPresenterImpl = null;
    ObservableList<String> arrRunningAppsList = FXCollections.observableList(new ArrayList<>());
    private TrayIcon trayIcon = null;
    private Stage window = null;
    private SystemTray tray = null;
    private Button buttonStart = null;
    private TextField txtProjectName = null, txtProjectPassword = null,
            txtImagePartition = null;

    public static void main(String[] args) {
        launch(args);
    }

    /*This code is called one for inflate UI and get the running application list*/
    @Override
    public void start(Stage primaryStage) {
        clientPresenterImpl = new Client.ClientPresenterImpl(this);
        clientPresenterImpl.inflateView(primaryStage);
        clientPresenterImpl.getRunningTaskList();
    }

    /*All the UI code sits here
    * from system tray to the window elements*/
    @Override
    public void inflateView(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Phantom Eye");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        //project name
        Label Projlabel = new Label("Project Name:");
        GridPane.setConstraints(Projlabel, 0, 0);
        txtProjectName = new TextField();
        Platform.runLater( () -> window.requestFocus() );
        //project name textfiled validation
        txtProjectName.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtProjectName.getText().matches("[A-Za-z\\s]+")) {
                    txtProjectName.setText("");
                    Utils.showAlert(Alert.AlertType.ERROR, "Error" , "Please insert characters only!");
                }
            }
        });
        txtProjectName.setPrefWidth(20);
        GridPane.setConstraints(txtProjectName, 1, 0);
        Projlabel.setStyle("-fx-text-fill: #ff9a16;");

        //password
        Label password = new Label("Password:");
        GridPane.setConstraints(password, 0, 2);
        password.setStyle("-fx-text-fill: #ff9a16;");
        txtProjectPassword = new PasswordField();
        GridPane.setConstraints(txtProjectPassword, 1, 2);

        //time
        Label time = new Label("Frame rate :");
        GridPane.setConstraints(time, 0, 0);
        time.setStyle("-fx-text-fill: #ff9a16;");
        TextField frames = new TextField("10");
        //frames textfiled validation
        frames.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!frames.getText().matches("[0-9]+")) {
                    frames.setText("");
                    Utils.showAlert(Alert.AlertType.ERROR, "Error" , "Please insert numbers only!");
                }
            }
        });
        GridPane.setConstraints(frames, 1, 0);

        //screen selection
        Label screen_parts = new Label("Screen Partition:");
        screen_parts.setStyle("-fx-text-fill: #ff9a16;");
        GridPane.setConstraints(screen_parts, 0, 1);
        txtImagePartition = new TextField("4");
        //Image partition textfiled validation
        //P.S Note : Only 1,4,9,16,25,36 should be sent
        txtImagePartition.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (!newValue) {
                if (!txtImagePartition.getText().matches("[0-9]+")) {
                    txtImagePartition.setText("");
                    Utils.showAlert(Alert.AlertType.ERROR, "Error" , "Please insert numbers only!");
                }
            }
        });
        GridPane.setConstraints(txtImagePartition, 1, 1);
        //radio for cursor
       /* RadioButton rb1 = new RadioButton("Yes");
        GridPane.setConstraints(rb1, 1, 1);
        rb1.setStyle("-fx-text-fill: #ff9a16;");
        RadioButton rb2 = new RadioButton("No");
        rb2.setStyle("-fx-text-fill: #ff9a16;");
        GridPane.setConstraints(rb2, 1, 2);
        Label mouse = new Label("Cursor Control:");
        mouse.setStyle("-fx-text-fill: #ff9a16;");
        GridPane.setConstraints(mouse, 0, 1);

        ToggleGroup radio = new ToggleGroup();
        rb1.setToggleGroup(radio);
        rb2.setToggleGroup(radio);*/

        // drop down list
        /*need to link with the fetched application list*/
        Label select = new Label("Select Application:");
        select.setStyle("-fx-text-fill: #ff9a16;");
        GridPane.setConstraints(select, 0, 2);
        ChoiceBox<String> choice = new ChoiceBox<>();
        choice.setPrefSize(200.0, 10.0);
        choice.getItems().addAll(arrRunningAppsList);
        GridPane.setConstraints(choice, 1, 2);
        choice.getSelectionModel().selectedItemProperty().addListener((v, oldvalue, newvalue) -> System.out.println(newvalue));

        //mainscreen button on second screen
        Button mainScreen = new Button("Main Screen");
        GridPane.setConstraints(mainScreen, 1, 5);
        GridPane grid2 = new GridPane();
        grid2.setPadding(new Insets(10, 10, 10, 10));
        grid2.setVgap(10);
        grid2.setHgap(10);
        grid2.getChildren().addAll(mainScreen, time, frames, screen_parts, txtImagePartition, select, choice);
        Scene advanceScene = new Scene(grid2, 370, 200);
        Button changescreen = new Button("Advanced");
        GridPane.setConstraints(changescreen, 0, 5);
        changescreen.setOnAction(e -> window.setScene(advanceScene));

        //start button
        buttonStart = new Button("Start");
        buttonStart.setStyle("-fx-text-fill: green;");

        buttonStart.setOnAction(e -> {
            //TODO : The user should not be able to change the text-fields while the app is running.
            //user can start & stop the client app.
            if (buttonStart.getText().equals("Start") && (txtProjectName.getText().trim().isEmpty() || txtProjectPassword.getText().trim().isEmpty())) {
                clientPresenterImpl.setAppRunningStatus(false);
                Utils.showAlert(Alert.AlertType.ERROR, "Error" , "Please insert the username and password!");
            } else if (buttonStart.getText().equals("Start")) {
                buttonStart.setText("Stop");
                clientPresenterImpl.setAppRunningStatus(true);
                clientPresenterImpl.startApp();
            } else {
                buttonStart.setText("Start");
                clientPresenterImpl.setAppRunningStatus(false);
            }
        });

        GridPane.setConstraints(buttonStart, 1, 5);
        grid.getChildren().addAll(Projlabel, txtProjectName, password, txtProjectPassword, buttonStart, changescreen);
        Scene scene = new Scene(grid, 370, 200);
        mainScreen.setOnAction(e -> window.setScene(scene));
        scene.getStylesheets().add("css/phantom.css");
        advanceScene.getStylesheets().add("css/phantom.css");
        window.setResizable(false);
        window.initStyle(StageStyle.UTILITY);
        window.setScene(scene);
        window.show();

        Platform.setImplicitExit(false);
        window.showingProperty().addListener(this);
    }


    /*System tray configuration*/
    @Override
    public void setSystemTray() {

        if (!SystemTray.isSupported()) return;

        tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("C:/Capstone/WindowsApp/src/client/os.jpg");


        PhantomMouseListener mouseListener = new PhantomMouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    openWindow();
                }
            }
        };

        PopupMenu popup = new PopupMenu();
        java.awt.MenuItem openItem = new MenuItem("Open");
        java.awt.MenuItem startItem = new MenuItem(clientPresenterImpl.isScreenCaptureRunning()?"Stop":"Start");
        java.awt.MenuItem defaultItem = new MenuItem("Exit");

        java.awt.Font defaultFont = java.awt.Font.decode(null);
        java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
        openItem.setFont(boldFont);
        defaultItem.setFont(boldFont);
        startItem.setFont(boldFont);

        startItem.addActionListener(e -> {
            if (!clientPresenterImpl.isScreenCaptureRunning()){
                startItem.setLabel("Stop");
                buttonStart.setText("Stop");
                clientPresenterImpl.setAppRunningStatus(true);
                clientPresenterImpl.startApp();
            } else {
                startItem.setLabel("Start");
                buttonStart.setText("Start");
                clientPresenterImpl.setAppRunningStatus(false);
            }
        });

        openItem.addActionListener(e -> openWindow());
        defaultItem.addActionListener(e -> System.exit(0));
        popup.add(openItem);
        popup.add(startItem);
        popup.add(defaultItem);

        trayIcon = new TrayIcon(image, "Phantom", popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> openWindow());
        trayIcon.addMouseListener(mouseListener);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*This piece of code opens the window from system tray
    * Platform.runLater is used to map the output from the worker thread to UI thread.*/
    private void openWindow() {
        Platform.runLater(() -> {
            if (window != null) {
                window.show();
                window.toFront();

                if (tray != null && trayIcon != null)
                    tray.remove(trayIcon);
            }
        });
    }


    /*Callback after the running app list is fetched.
    * here we can map the list to the observableList which can be set to the dropdown UI */
    @Override
    public void onTaskListFetched(ArrayList<String> arrTaskList) {
        arrRunningAppsList.clear();
        arrRunningAppsList = FXCollections.observableList(arrTaskList);
    }

    /*Callback to start client*/
    @Override
    public void startClientInitProcess() {
        clientPresenterImpl.initClient();
    }

    /*callback if the client is initialized successfully
    * After sending the auth credentials to client(Connection request), it waits for the response*/
    @Override
    public void onClientInitializedSuccessfully() {
        int noOfPartitions = (int) Math.sqrt(Integer.valueOf(txtImagePartition.getText().trim()));
        String projectName = txtProjectName.getText().trim();
        String projectPassword = txtProjectPassword.getText().trim();
        clientPresenterImpl.sendConnectionAckToServer(noOfPartitions, projectName, projectPassword);
        clientPresenterImpl.waitingForServerAck();
    }

    /*Callback : if the server successfully responds for the connection request
    * Then client starts to capture the screen*/
    @Override
    public void onConnectEstablishedSuccessfully() {
        clientPresenterImpl.startScreenCapture();
    }

    /*Callback : Screen captured successfully
    * Now the clinet will send the image metadata(no_of_images, size, name, etc)*/
    @Override
    public void onScreenCapturedSuccessfully(ImageChunksMetaData[] arrImageChunksmetaData) {
        clientPresenterImpl.sendMetadataToServer(arrImageChunksmetaData);
        clientPresenterImpl.waitingForServerAck();
    }

    /*Callback : Once the image metadata is sent successfully then the client starts
    * sending the actual images*/
    @Override
    public void onMetaDataSentSuccessfully(ImageChunksMetaData[] arrImageChunks) {
        clientPresenterImpl.sendImageFileToServer(arrImageChunks);
        clientPresenterImpl.waitingForServerAck();
    }

    /*Callback : Once all the images are sent to the server.
    * If the user havent stopped the app then we'll redo the process from screen capturing
    * else we'll just print the disabled status*/
    @Override
    public void onImageSentSuccessfully() {
        //Check if the screen capture is enabled/disabled & then init the process again
        if (clientPresenterImpl.isScreenCaptureRunning()) {
            clientPresenterImpl.startScreenCapture();
        } else {
            System.out.println("Screen capture app is disabled");
        }
    }

    /*Overriden method : Called when you close/open the app
    * If you close the app then system tray is init
    * while the app open it remove the app icon from system tray
    *
    * The app icon should be in tray when the app is closed and this calback takes care of that logic.*/
    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
        if (!t1.booleanValue()) {
            clientPresenterImpl.setSystemTray();
        } else {
            if (tray != null && trayIcon != null)
                tray.remove(trayIcon);
        }
    }
}
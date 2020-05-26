/*Demo class : Just to know the efficency of the screen capture protocol
* Code not used in the project.*/
package windows_ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class ScreenCaptureFast extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        openDialog(primaryStage);
        takeScreenShot();
    }

    private void takeScreenShot() {
        Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            float totalTime = 0;
            Robot robot = new Robot();
            for (int index = 0; index < 10; index++) {
                int count = 0;
                long beforeTime = System.currentTimeMillis();
                while (count < 30) {
                    robot.createScreenCapture(rect);
                    count++;
                }
                totalTime += (float) ((System.currentTimeMillis() - beforeTime) / 1000);
            }
            System.out.println("Avg time it took for 30 screen captures: " + totalTime / 10 + " sec");
        } catch (Exception e) {
        }

    }

    private void openDialog(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Phantom Eye");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

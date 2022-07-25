package mvc;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TranslatorControl {
    @FXML
    private Button loadDictionaryButton;

    @FXML
    private Button loadTextButton;

    @FXML
    private Button openTranslatedTextButton;

    @FXML
    final FileChooser fileChooser = new FileChooser();

    @FXML
    private Desktop desktop = Desktop.getDesktop();

    @FXML
    private final TranslatorModel translation = new TranslatorModel();

    @FXML
    protected void loadDict() {
        loadDictionaryButton.setOnAction(
                 e -> {
                     configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(loadDictionaryButton.getScene().getWindow());
                    if (file != null) {
                        translation.readDictionary(file);
                    }
                }
        );
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Select text file");
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
    }


    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    TranslatorControl.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }

    @FXML
    protected void loadText() {
        loadTextButton.setOnAction(
                e -> {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(loadTextButton.getScene().getWindow());
                    if (file != null) {
                        translation.readText(file);
                    }
                }
        );
    }

    @FXML
    protected void openText() {
        translation.translateText();
        openFile(translation.saveFile());
    }
}
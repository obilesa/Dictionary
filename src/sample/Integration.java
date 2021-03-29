package sample;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


// temporary class needs to be merged with controller
public class Integration{

    private static WebView webviewtest;

    public static WebView getWebviewtest() {
        return webviewtest;
    }

    public static void setWebviewtest(WebView webviewtest) {
        Integration.webviewtest = webviewtest;
    }

    public Integration(){

    }
    public void translate(String fromLanguage, String toLanguage, String inputText){

        Translator t = new Translator();
        System.out.println(inputText);
        List<Pair<String, Set<String>>> translation =t.translate(fromLanguage,toLanguage,inputText);
        processTranslation(translation);

    }
    private void processTranslation(List<Pair<String, Set<String>>> translation){
        WebEngine webEngine = webviewtest.getEngine();
        for(int i=0; i<translation.size();i++) {

            webEngine.executeScript("addTranslation("+i+","+translation.get(i).getKey()+","+ Arrays.deepToString(translation.get(i).getValue().toArray())+")");
        }
    }
    // not an ideal solution but I cant get the clipboard API to work on javafx webview
    // does not work on linux
    public void copy(String text){
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

    }

    public void download(String text) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showSaveDialog(Main.getpStage());
        Files.writeString(Path.of(selectedFile.getPath()), text);
    }

    public void toggleAddNewWord(boolean ToggleValue){
        Controller.setAddNewWord(ToggleValue);
        System.out.println(Controller.isAddNewWord());
    }
    public boolean getAddNewWord(){
        return Controller.isAddNewWord();
    }

    public void readFile() throws IOException {
        WebEngine webEngine = webviewtest.getEngine();
        String[] r  = new String[3];
        DecimalFormat df = new DecimalFormat( "#.##" );
        List<String> text = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(Main.getpStage());
        Files.lines(Path.of(selectedFile.getPath()), StandardCharsets.ISO_8859_1).forEachOrdered(text::add);
        r[0]=text.stream().collect(Collectors.joining(", "));
        r[2]=selectedFile.getName();
        String[] aMultiples = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        double nBytes  = Double.valueOf(selectedFile.length());
        r[1]=String.valueOf(nBytes+" bytes");
        for (int i = 0; nBytes > 1000; i++) {
            nBytes /= Double.valueOf(1000);
            r[1] = String.valueOf(df.format(nBytes)+ " " + aMultiples[i]);
        }
        System.out.println(r[1]);
        webEngine.executeScript("fileupload(\""+r[0]+"\",\""+r[1]+"\",\""+r[2]+"\")");
    }
}
package sample;

import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        new Test();
    }

    private Test() {
        initialize();
        process();
    }

    private void initialize() {

    }

    private void process() {
        testDictionary();
        testTranslator();
    }


    private void testDictionary() {
        Dictionary dictionary = new Dictionary();
        dictionary.add("this is testing phrase", new String[] {"it works", "how unexpected"});
        System.out.println(dictionary.getDict());
        dictionary.add("this is nice dog", new String[] {"holy", "moly"});
        System.out.println(dictionary.getDict());
        try {
            dictionary.remove("this is testing phrase", new String[] {"it works"});
        } catch (NoTranslationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println(dictionary.getDict());
    }

    private void testTranslator() {
        Dictionary testDict = new Dictionary();
        try {
            testDict.generateDictionaryFromCSVFile("test/dutchEnglishSmall.csv");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Translator translator = new Translator(null);
        translator.addDictionary("Dutch", "English", testDict);

        String text = null;
        try {
            text = Files.readString(Path.of("test/dutchSmall.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        List<Pair<String, Set<String>>> translation = translator.translate("Dutch", "English", text);
        printTranslation(translation);

    }

    private void printTranslation(List<Pair<String, Set<String>>> translation) {
        StringBuilder out = new StringBuilder();
        for (Pair<String, Set<String>> pair :translation) {
            if (pair.getValue() == null) {
                out.append("<").append(pair.getKey()).append("> ");
            } else {
                out.append(pair.getValue().iterator().next()).append(" ");
            }
        }
        System.out.println(out);
    }
}
package mvc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class TranslatorModel {
    private final HashMap<String, String> dict = new HashMap<>();
    private String text = "";
    private int maxWordsNum = 0;
    private String fileName;

    public void readDictionary(File file){
        try (Stream<String> lines = Files.lines(Path.of(file.getAbsolutePath()), StandardCharsets.UTF_8)) {
            lines.map(s -> s.split(" - "))
                    .forEach(s -> {
                        String src = removePreposition(s[0]);
                        dict.put(src, s[1].trim());
                        int wordSize = src.length() - src.replaceAll(" ", "").length() + 1;
                        maxWordsNum = wordSize > maxWordsNum ? wordSize : maxWordsNum;
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removePreposition(String word) {
        String res;
        if (word.matches("a\\s(.*)"))
            res = word.substring(2);
        else if (word.matches("the\\s(.*)"))
            res = word.substring(4);
        else res = word;
        return res.trim().toLowerCase();
    }

    public void readText(File file) {
        fileName = file.getAbsolutePath();
        try (Stream<String> lines = Files.lines(Path.of(file.getAbsolutePath()))) {
            lines.forEach(s -> text += s + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void translateText() {
        deletePrepositions("the");
        deletePrepositions("a");

        StringBuilder source = new StringBuilder();
        List<String> textWords = new ArrayList<>();

        //get array words
        Stream.of(text).map(s -> s.split("\\s")).forEach(s -> {
            for (String str : s)
                textWords.add(str.replaceAll("[^a-zA-Z]", "").toLowerCase());
        });

        int wordsCounter = 0;

        for (String str : textWords) {

            source.append(str);
            wordsCounter++;

            String tmp = findKey(source.toString(), wordsCounter);
            String translation = dict.get(tmp);

            if (translation != null) {
                //replace first founded word
                Pattern pattern = Pattern.compile("\\b" + tmp + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                text = matcher.replaceFirst(translation);
                source.delete(0, source.length());
                wordsCounter = 0;
            } else {
                //word with max size doesn't have its translation in dictionary
                if(wordsCounter >= maxWordsNum) {
                    source.delete(0, source.length());
                    wordsCounter = 0;
                } else {
                    //try to check composite word
                    source.append(" ");
                }
            }
        }

    }

    private void deletePrepositions(String preposition) {
        Pattern pattern = Pattern.compile("\\b" + preposition + "\\s\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        text = matcher.replaceAll("");
    }

    private String findKey(String sourceWord, int wordsCounter) {
        String result = sourceWord;
        //if word is composite try to find translation of every part of it in dictionary
        if (wordsCounter > 1 && dict.get(result) == null) {
            for (String s : sourceWord.split(" ")) {
                result = s;
                if (dict.containsKey(result))
                    break;
            }
        }
        return result;
    }

    public void printDict() {
        for (String key : dict.keySet()) {
            System.out.println("{" + key + "-" + dict.get(key) + "}");
        }
    }

    public void printText() {
        System.out.println(text);
    }

    public File saveFile() {
        String outFileName = fileName.substring(0, fileName.lastIndexOf(".txt")) + "_translated.txt";
        File result = new File(outFileName);
        try(PrintWriter pw = new PrintWriter(result, "UTF-8")) {
            pw.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}


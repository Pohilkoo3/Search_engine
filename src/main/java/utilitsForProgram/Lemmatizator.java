package utilitsForProgram;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Lemmatizator
{
    private static final String regex = "ЧАСТ|СОЮЗ|МЕЖД|ПРЕДЛ";
    private static final Pattern pattern = Pattern.compile(regex);
    private static LuceneMorphology luceneMorph;

    static {
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> getLemmas(String someText) {
        if (luceneMorph == null){
            try {
                luceneMorph = new RussianLuceneMorphology();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        List<String> resultListLemmas = new ArrayList<>();
        String[] textString = someText.split("\n");
        for (String textEveryString : textString) {
            String textWithoutSymbols = textEveryString.replaceAll("[^А-я]", " ");
        String[] arrayWords = textWithoutSymbols.split(" ");
        List<String> stringListWords = Arrays.stream(arrayWords)
                .filter(e -> !e.isBlank())
                .map(e -> e.trim().toLowerCase(Locale.ROOT).trim())
                .collect(Collectors.toList());

        for (String word : stringListWords) {
            if (!isServiceWord(word)) {
                resultListLemmas.addAll(luceneMorph.getNormalForms(word));
            }
        }
    }
       return resultListLemmas;
    }

    public static boolean isServiceWord(String word) {
      return luceneMorph.getMorphInfo(word).stream().map(e -> Arrays.stream(e.split("\\|"))
                        .collect(Collectors.toList())
                        .get(1))
                .map(e -> pattern.matcher(e).find())
                .filter(e -> e).count() != 0;
    }

    public static List<String> getInfoAboutWord(String word){//TODO потом удалить, это служебный метод
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    return luceneMorph.getMorphInfo(word);
    }

}

import org.languagetool.JLanguageTool;
import org.languagetool.language.Russian;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String... args) throws IOException {
        //Path is not relative because IDEA can't work with them
        processFile("E:\\Programs\\Projects\\Java\\Search\\src\\main\\resources\\train.csv");
    }

    private static void processFile(String fileName) throws IOException {
        JLanguageTool langTool = new JLanguageTool(new Russian());
        for (Rule rule : langTool.getAllRules()) {
            if (!rule.isDictionaryBasedSpellingRule()) {
                langTool.disableRule(rule.getId());
            }
        }

        InputStreamReader rd = new InputStreamReader(new FileInputStream(fileName));

        String toCheck = readStrings(rd);
        Map<String, Integer> unfixedWords = new HashMap<>();
        Map<String, Integer> fixedWords = new HashMap<>();
        int counter = 0;
        while (!toCheck.isEmpty()) {
            List<RuleMatch> matches = langTool.check(toCheck);
            StringBuilder sb = new StringBuilder();
            Integer lastIndex = 0;

            for (RuleMatch match : matches) {
                if (match.getSuggestedReplacements().size() > 0) {
                    sb.append(toCheck.substring(lastIndex, match.getFromPos()));
                    sb.append(match.getSuggestedReplacements().get(0));
                    lastIndex = match.getToPos();
                }
            }

            sb.append(toCheck.substring(lastIndex, toCheck.length()));
            for (String word : sb.toString().split(" ")) {
                word = word.toLowerCase();
                word = Porter.stem(word);
                if (fixedWords.containsKey(word))
                    fixedWords.put(word, fixedWords.get(word) + 1);
                else
                    fixedWords.put(word, 1);
            }

            for (String word : toCheck.split(" ")) {
                word = word.toLowerCase();
                word = Porter.stem(word);
                if (unfixedWords.containsKey(word))
                    unfixedWords.put(word, unfixedWords.get(word) + 1);
                else
                    unfixedWords.put(word, 1);
            }

            counter++;
            if (counter % 20 == 0) {
                System.out.println(counter * 50);
            }


            toCheck = readStrings(rd);
        }
        int[][] stats = getStats(fixedWords, unfixedWords);
        writeFile("res.csv", generateOutput(stats));
        int a = 0;
    }

    private static void writeFile(String fileName, List<String> strings) {
        try {
            Files.write(Paths.get(fileName), strings, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> generateOutput(int[][] stats) {
        List<String> res = new ArrayList<>();
        for (int[] line : stats) {
            StringBuilder sb = new StringBuilder();
            for (int el : line) {
                sb.append(el);
                sb.append(';');
            }
            res.add(sb.toString());
        }
        return res;
    }


    private static int[][] getStats(Map<String, Integer> fixed, Map<String, Integer> unfixed) {
        int[][] stats = new int[3][6];
        stats[0] = new int[]{1, 10, 50, 100, 250, 500};

        for (Map.Entry<String, Integer> entry : fixed.entrySet()) {
            for (int i = 0; i < stats[0].length; i++) {
                if (entry.getValue() >= stats[0][i]) {
                    stats[1][i]++;
                }
            }
        }

        for (Map.Entry<String, Integer> entry : unfixed.entrySet()) {
            for (int i = 0; i < stats[0].length; i++) {
                if (entry.getValue() >= stats[0][i]) {
                    stats[2][i]++;
                }
            }
        }
        return stats;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private static String readStrings(InputStreamReader rd) throws IOException {
        int c = rd.read();
        Boolean wasSpace = false;
        String symbols = " .,:;\"'-()_?![]{}#*/«»";
        String allowed = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        StringBuilder sb = new StringBuilder();
        int counter = 0;

        while (c != -1 && counter < 50) {
            if (c == '\n') {
                counter++;
            }
            if (symbols.indexOf(c) != -1) {
                if (!wasSpace) {
                    wasSpace = true;
                    sb.append(' ');
                }
            } else {
                if (allowed.indexOf(c) != -1) {
                    sb.append((char) c);
                    wasSpace = false;
                }
            }
            c = rd.read();
        }
        return sb.toString();
    }
}

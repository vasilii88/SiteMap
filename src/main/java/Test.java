import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Test {


    public static void createAndWriteToFile(String pathToFile, String toWriteString) {

        System.out.println("Началась запись в файл...");

        byte[] date = toWriteString.getBytes(StandardCharsets.UTF_8);
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(Path.of(pathToFile), CREATE, TRUNCATE_EXISTING))) {
            out.write(date, 0, date.length);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Запись в файл завершилась успешно! \n" +
                Path.of(pathToFile).toAbsolutePath());
    }

    public static String convertingSetToString (Set<String> setOfLinks) {
        ArrayList<String> arrayList = setOfLinks.stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        }).collect(Collectors.toCollection(ArrayList::new));
        StringBuilder builder = new StringBuilder();
        arrayList.forEach(str -> builder.append(str + "\n"));
        return builder.toString();
    }

    public static void main(String[] args) {
        HashMap<String, String> hashMap = new HashMap<>();
        HashSet <String> hashSet = new HashSet<>();

        String str = "https//";
        hashSet.add(str);


    }
}

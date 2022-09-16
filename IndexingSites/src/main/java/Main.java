
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static String url = "https://skillbox.ru/";
    public static String url2 = "https://dombulgakova.ru/";
    public static String lenta = "https://lenta.ru/";
    public static String playback = "http://www.playback.ru/";

    public static void main(String[] args) throws IOException, SQLException {

        long start = System.currentTimeMillis();
        HTMLParser.setPatternForSearchingSites(url);
        HTMLParser parser = new HTMLParser(url);
        Storage.putDataToList(parser.getPath(),parser.getStatusCode(), parser.getContent());
        NodeProcessor processor = new NodeProcessor(parser.getListOfSubLinks());
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(processor);

        System.out.println("Общее количество уникальных ссылок: " + HTMLParser.getUniqueSetOfLinks().size());
        System.out.println("Количество записей для передачи в БД: " + Storage.getTotalCounter());
        System.out.println("Количество записей в ArrayList для передачи в БД: " + Storage.getListOfData().size());
        DecimalFormat format = new DecimalFormat("##.##");
        double timeWorking = ((System.currentTimeMillis() - start) / (1000 * 60.0));
        System.out.println("Время выполнения программы - " + format.format(timeWorking) + " мин");
        System.out.println(pool);

        /*System.out.println("Происходит работа с БД");
        List<Node> nodeList = Node.getListOfNodes();
        DBConnection.executeQueryMultiInsert(nodeList);
        System.out.println("Записи переданы в БД");*/

    }
}

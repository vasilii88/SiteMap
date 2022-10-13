
import DBcontroller.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.concurrent.ForkJoinPool;


public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);
    private static Marker INFO_MARKER = MarkerManager.getMarker("INFO");
    private static  Marker DB_MARKER= MarkerManager.getMarker("DB_LOG");
    private static  Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTIONS");
    public static ForkJoinPool pool = new ForkJoinPool();

    private static String pathToFileWithLinks = "C:\\Users\\Bot19\\Desktop\\sours.txt";
    private static String pathToFileWithLinksFromDB = "C:\\Users\\Bot19\\Desktop\\dest.txt";

    public static String url = "https://skillbox.ru/";
    public static String url2 = "https://dombulgakova.ru/";
    public static String lenta = "https://lenta.ru/";
    public static String playback = "http://www.playback.ru/";

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {

        DBConnection.getConnectionResetAllSettings();
        long start = System.currentTimeMillis();

        Storage storage = new Storage();

        HTMLParser.setPatternForSearchingSites(url);
        NodeProcessor processor = new NodeProcessor(url, storage);
        pool.invoke(processor);
        Storage.sendDataToDB();
        NodeProcessor.getUniqueSetOfLinks().forEach(System.out::println);
        System.out.println("Общее количество уникальных ссылок: " + NodeProcessor.getUniqueSetOfLinks().size());
        logger.info(INFO_MARKER,"Общее количество уникальных ссылок: " + NodeProcessor.getUniqueSetOfLinks().size());
        System.out.println("Общее количество hashSet в Storage : " + Storage.getUniqueSetOfLinks().size());

        Test.createAndWriteToFile(pathToFileWithLinks, Test.convertingSetToString(NodeProcessor.getUniqueSetOfLinks()));
        Test.createAndWriteToFile(pathToFileWithLinksFromDB, DBConnection.getResultSet());

        int count = DBConnection.getTotalCountOfRecords();

        System.out.println("Количество записей в БД : " + count);

       // System.out.println("Количество записей в HashSet for Parse : " + NodeProcessor.getUniqueSetOfParser().size());
        logger.info(DB_MARKER,"","Количество записей в БД : " + count );
        DecimalFormat format = new DecimalFormat("##.##");

        double timeWorking = ((System.currentTimeMillis() - start) / (1000 * 60.0));
        System.out.println("Время выполнения программы - " + format.format(timeWorking) + " мин");
        logger.info(INFO_MARKER, "Время выполнения программы - " + format.format(timeWorking) + " мин");
        System.out.println(pool);
        try {
            DBConnection.getConnection().close();
        } catch (SQLException e) {
            logger.debug(DB_MARKER,"", e);
            logger.debug(EXCEPTION_MARKER,"",e);
        }

    }



}

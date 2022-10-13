
import DBcontroller.DBConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Storage {

    private static final int countBuffer = 70_000_000;
    private static StringBuffer buffer = new StringBuffer();
    private static AtomicInteger counter = new AtomicInteger(0);
    private static AtomicInteger newCounter = new AtomicInteger(0);
    private static Set<String> uniqueSetOfLinks = Collections.synchronizedSet(new HashSet<>());

    private final static Logger logger = LogManager.getLogger(Storage.class);
    private static Marker INFO_MARKER = MarkerManager.getMarker("INFO");

    public void add(HTMLParser p) {

            String correctedContent = p.getContent().replaceAll("'", "\"");
            buffer.append((buffer.length() == 0 ? "" : ",") + "('" + p.getPath() + "' , " + p.getStatusCode() + " , '" + correctedContent + "')");
            printCountOfLinks();
            collectToBufferBeforeSending();

    }

    public synchronized void putDataToStorage(String uri, int responseCode, String content) throws SQLException, InterruptedException {

            String correctedContent = content.replaceAll("'", "\"");
            buffer.append((buffer.length() == 0 ? "" : ",") + "('" + uri + "' , " + responseCode + " , '" + correctedContent + "')");
            printCountOfLinks();
            collectToBufferBeforeSending();

    }


    public static void sendDataToDB() {
        DBConnection.executeQueryMultiInsert(buffer.toString(), newCounter.get());
    }

    public synchronized static void sendDataToDB(String str) {
        DBConnection.executeQueryMultiInsert(str, newCounter.get());
    }
    public static void sendDataToDB(String str, int count) {
        DBConnection.executeQueryMultiInsert(str, count);
    }

    public static synchronized void collectToBufferBeforeSending() {

        if (buffer.length() > countBuffer){
            DBConnection.executeQueryMultiInsert(buffer.toString(),newCounter.get());
            buffer = new StringBuffer();
        }
    }

    public static int getTCounter() {
        return counter.get();
    }

    public static Set<String> getUniqueSetOfLinks() {
        return uniqueSetOfLinks;
    }


    private static void printCountOfLinks() {

        newCounter.getAndIncrement();
        if (newCounter.get() % 100 == 0) {
            System.out.println("Количестово ссылок: " + newCounter.get());
            logger.info(INFO_MARKER, "Количестово ссылок: " + newCounter.get());
        }
    }

}

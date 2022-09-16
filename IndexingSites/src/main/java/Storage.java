
import java.sql.SQLException;
import java.util.*;

public class Storage {

    private static StringBuilder builder = new StringBuilder();
    private static LinkedList<String> listOfDates = new LinkedList<>();

    private volatile static int counter = 0;
    private volatile static int total = 0;

    public static synchronized void putDataToStringBuffer(String uri, int responseCode, String content) throws SQLException {
        if (counter == 100) {
            sendDataToQuery();
            total += counter;
            counter = 0;
        }
        String correctedContent = content.replaceAll("'", "\"");
        builder.append((builder.length() == 0 ? "" : ",") +
                "('" + uri + "'," + responseCode + ",'" + correctedContent + "')");
        counter++;
    }

    public synchronized static void putDataToList(String uri, int responseCode, String content) throws SQLException {
        String correctedContent = content.replaceAll("'", "\"");
        listOfDates.addLast("('" + uri + "'," + responseCode + ",'" + correctedContent + "')");
        total++;
    }

    public static void sendDataToQuery() throws SQLException {

        DBConnection.executeQueryMultiInsert(builder.toString(), counter);
        builder = new StringBuilder();

    }

    public static LinkedList<String> getListOfData() {
        return listOfDates;
    }

    public static String getDataFromStorage() {
        return builder.toString();
    }

    public static int getTotalCounter() {
        return total;
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class DBConnection {

    private static Connection connection;

    private static final String dbName = "searching_index";
    private static final String tableName = "page";
    private static final String dbUser = "root";  //"user";
    private static final String dbPass = "Akv-jA247-8thA247-8";  //"01-07-2022";
    private static final String SET_MAX_ALLOWED_PACKET = "SET GLOBAL max_allowed_packet=30000000";
    private static StringBuilder insertQuery = new StringBuilder();
    private static int count = 0;
    private static int totalCount = 0;
    private static LinkedList<String> linkedListOfQuery = new LinkedList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    private static final String sqlCreateDataBase = "CREATE SCHEMA `searching_index` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
    private static final String sqlCreateTable = "CREATE TABLE `page` (" +
            "  `id` int NOT NULL auto_increment," +
            "  `path` TEXT NOT NULL," +
            "  `code` int NOT NULL," +
            "  `content`  MEDIUMTEXT NOT NULL," +
            "PRIMARY KEY(id), KEY(path(100)))";

    public static void executeQueryMultiInsert(LinkedList<String> listOfData) throws SQLException {
       ArrayList<String> newListOfData = new ArrayList<>(listOfData);

       int size = newListOfData.size();
        for (int i = 0; i < size; i++) {
            newListOfData.get(i);
            newListOfData.remove(i);
            size--;
            if(i== 100) {

                newListOfData.trimToSize();
            }
        }
    }

    public static void executeQueryMultiInsert() throws SQLException {
        // INSERT INTO searching_index.page (`path`, `code`, `content`)
        //   VALUES('newTest', 200 , 'newTest');
        //  setDataForInsertQuery(nodeList);
        String query = "INSERT INTO page (`path`, `code`, `content`) VALUES";
        insertQuery.append(query);
        while (linkedListOfQuery.descendingIterator().hasNext()) {
            if (count == 100) {
                System.out.println("Количество записей для передачи в БД - " + count);
                long start = System.currentTimeMillis();
                DBConnection.getConnection().prepareStatement(insertQuery.toString()).execute();
                insertQuery = new StringBuilder();
                insertQuery.append(query);
                System.out.println("Время записи данных в БД - " + decimalFormat.format((System.currentTimeMillis() - start) / 1000.0) + " секунд");
                totalCount += count;
                System.out.println("Общее количество записей в БД - " + totalCount);
                count = 0;
                System.out.println("-------------------------------------------");

            }
            insertQuery.append((insertQuery.length() == query.length() ? "" : ",") + linkedListOfQuery.pollLast());
            count++;
        }
        System.out.println("Количество записей для передачи в БД - " + count);
        long start = System.currentTimeMillis();
        DBConnection.getConnection().prepareStatement(insertQuery.toString()).execute();
        System.out.println("Время записи данных в БД - " + decimalFormat.format((System.currentTimeMillis() - start) / 1000.0) + " секунд");
        totalCount += count;
        System.out.println("Общее количество записей в БД - " + totalCount);
        System.out.println("-------------------------------------------");
    }

    public static void executeQueryMultiInsert(String string, int counter) throws SQLException {
        insertQuery = new StringBuilder();
        String query = "INSERT INTO page (`path`, `code`, `content`) VALUES";
        insertQuery.append(query);
        insertQuery.append(string);
        System.out.println("Количество записей для передачи в БД - " + counter);
        long start = System.currentTimeMillis();
        DBConnection.getConnection().prepareStatement(insertQuery.toString()).execute();
        insertQuery = new StringBuilder();
        System.out.println("Время записи данных в БД - " + decimalFormat.format((System.currentTimeMillis() - start) / 1000.0) + " секунд");
        totalCount += counter;
        System.out.println("Общее количество записей в БД - " + totalCount);

    }


    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass);
                System.out.println("Соединение с БД установено...");
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute(SET_MAX_ALLOWED_PACKET);
                connection.createStatement().execute(sqlCreateTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


   /* private static void setDataForInsertQuery(List<Node> listOfObject) throws SQLException {

        for (Node node : listOfObject) {
            String correctedContent = node.getContent().replaceAll("'", "\"");
            linkedListOfQuery.addFirst(
                    "('" + node.getPath() + "'," + node.getStatusCode() + ",'" + correctedContent + "')");

        }
    }*/

    public static List<String> getLinkedListOfQuery() {
        return linkedListOfQuery;
    }
}

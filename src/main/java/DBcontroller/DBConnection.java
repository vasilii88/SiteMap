package DBcontroller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

public class DBConnection {

    private static Connection connection;

    private static final String DB_NAME = "searching_index";
    private static final String dbUser = "root";  //"user";
    private static final String dbPass = "Akv-jA247-8thA247-8";  //"01-07-2022";
    private static final String SET_MAX_ALLOWED_PACKET = "SET GLOBAL max_allowed_packet=900000000";
    private static StringBuilder insertQuery = new StringBuilder();
    private static int count = 0;
    private static int totalCount = 0;

    private final static Logger logger = LogManager.getLogger(DBConnection.class);

    private static Marker DB_MARKER = MarkerManager.getMarker("DB_LOG");
    private static Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTIONS");

    private static LinkedList<String> linkedListOfQuery = new LinkedList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    private static final String SQL_CREATE_DB_SEARCHING_INDEX = "CREATE SCHEMA `searching_index` DEFAULT CHARACTER SET utf8mb4";


    private static final String SQL_CREATE_TABLE_FIELD = "CREATE TABLE `field` (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(255) NOT NULL,\n" +
            "  `selector` VARCHAR(255) NOT NULL,\n" +
            "  `weight` FLOAT NOT NULL,\n" +
            "  PRIMARY KEY (`id`))";

    private static final String SQL_CREATE_TABLE_PAGE = "CREATE TABLE `page` (" +
            "  `id` int NOT NULL auto_increment," +
            "  `path` TEXT NOT NULL," +
            "  `code` int NOT NULL," +
            "  `content`  MEDIUMTEXT NOT NULL," +
            "PRIMARY KEY(id), UNIQUE KEY(path(100)))";

    public static void initDB() throws SQLException {

        connection.createStatement().execute(SQL_CREATE_TABLE_FIELD);
        logger.info(DB_MARKER, "Команда " + SQL_CREATE_TABLE_FIELD);
        connection.createStatement().execute("INSERT INTO `field` (`name`, `selector`, `weight`) VALUES\n" +
                "('title', 'title', 1),\n" +
                "('body', 'body', 0.8)");
        logger.info(DB_MARKER, "Команда " + "INSERT INTO `field` (`name`, `selector`, `weight`) VALUES\n" +
                "('title', 'title', 1),\n" +
                "('body', 'body', 0.8)");

        connection.createStatement().execute(SQL_CREATE_TABLE_PAGE);
        logger.info(DB_MARKER, "Команда " + SQL_CREATE_TABLE_PAGE);

    }

    public static int getTotalCountOfRecords() {
        String str = "SELECT count(*) FROM searching_index.page";
        ResultSet resultSet = null;
        int number = 0;
        try {
            getConnection();
            resultSet = connection.createStatement().executeQuery(str);
            logger.info(DB_MARKER, "Выполнен запрос " + str);
            resultSet.next();
            number = resultSet.getInt(1);
        } catch (SQLException e) {
            logger.debug(DB_MARKER, "", e);
            logger.debug(EXCEPTION_MARKER, "", e);
            e.printStackTrace();
        }
        return number;
    }


    public static void executeQueryMultiInsert(String buffer, int counter) {
        String query = "INSERT IGNORE INTO page (`path`, `code`, `content`) VALUES";
        insertQuery.append(query).append(buffer);


        long start = System.currentTimeMillis();
        try {
            if (DBConnection.getConnection().prepareStatement(insertQuery.toString()).execute()) {
                System.out.println("Количество записей для передачи в БД - " + counter);
                logger.info(DB_MARKER, "Количество записей для передачи в БД - " + counter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug(DB_MARKER, "", e);
            logger.debug(EXCEPTION_MARKER, "", e);
        }
        insertQuery = new StringBuilder();
        System.out.println("Время записи данных в БД - " + decimalFormat.format((System.currentTimeMillis() - start) / 1000.0) + " секунд");
        logger.info(DB_MARKER, "Время записи данных в БД - " + decimalFormat.format((System.currentTimeMillis() - start) / 1000.0) + " секунд");
        totalCount += counter;
        System.out.println("Общее количество записей в БД - " + totalCount);
        logger.info(DB_MARKER, "Общее количество записей в БД - " + totalCount);

    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                logger.info(DB_MARKER, "Идет подключение к БД...");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + DB_NAME +
                                "?user=" + dbUser + "&password=" + dbPass);
                logger.info(DB_MARKER, "Соединение с БД установено...");
            } catch (SQLException e) {
                e.printStackTrace();
                logger.debug(DB_MARKER, "", e);
                logger.debug(EXCEPTION_MARKER, "", e);
            }

        }
        return connection;
    }


    public static Connection getConnectionResetAllSettings() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" +
                                "?user=" + dbUser + "&password=" + dbPass);
                if (connection != null) {
                    logger.info(DB_MARKER, "Соединение установлено");
                }
                connection.createStatement().execute("DROP SCHEMA IF EXISTS " + DB_NAME);
                connection.createStatement().execute(SQL_CREATE_DB_SEARCHING_INDEX);
                logger.info(DB_MARKER, "Команда " + SQL_CREATE_DB_SEARCHING_INDEX);
                connection.createStatement().execute(SET_MAX_ALLOWED_PACKET);
                logger.info(DB_MARKER, "Команда " + SET_MAX_ALLOWED_PACKET);
                connection.close();
                logger.info(DB_MARKER, "Соединение сброшено...");
                connection = null;
                connection = getConnection();
                initDB();

            } catch (SQLException e) {
                e.printStackTrace();
                logger.debug(DB_MARKER, "", e);
                logger.debug(EXCEPTION_MARKER, "", e);
            }
        }
        return connection;
    }

    public static String getResultSet() throws SQLException {
        ArrayList <String> arrayList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

            getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT path FROM searching_index.page");
        while(resultSet.next()) {
            arrayList.add(resultSet.getString("path"));
        }

        arrayList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return   o1.compareTo(o2);
            }
        });

        for (String str : arrayList) {
            builder.append(str).append("\n");
        }

        return builder.toString();
    }

}

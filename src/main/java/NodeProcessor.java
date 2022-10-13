
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.RecursiveAction;


public class NodeProcessor extends RecursiveAction {

    private final static Logger logger = LogManager.getLogger(NodeProcessor.class);
    private static Marker DB_MARKER = MarkerManager.getMarker("DB_LOG");
    private static Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTIONS");
    private static Marker INFO_MARKER = MarkerManager.getMarker("INFO");
    private static Set<String> uniqueSetOfLinks = Collections.synchronizedSet(new HashSet<>());

    private Storage storage;
    private String link;


    public NodeProcessor(String link, Storage storage) {
        this.link = link;
        this.storage = storage;
    }

    @Override
    protected void compute() {

        try {
            HTMLParser parser = new HTMLParser(link);
            storage.add(parser);
            Set<String> setOfSubLinks = parser.getSetOfSubLinks();
            List<NodeProcessor> subTasks = new ArrayList<>();
            for (String link : setOfSubLinks) {
                randomDelayForThread();
                NodeProcessor task = new NodeProcessor(link, storage);
                subTasks.add(task);
                task.fork();
            }

            for (NodeProcessor task : subTasks) {
                task.join();
            }

        } catch (IOException ex) {
            logger.debug(EXCEPTION_MARKER, "", ex);
        } catch (SQLException ex) {
            logger.debug(EXCEPTION_MARKER, "", ex);
            logger.debug(DB_MARKER, "", ex);
        }
    }

    private static void randomDelayForThread() {
        int random = (int) (Math.floor(Math.random() * (150 - 100)) + 100);
        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getUniqueSetOfLinks() {
        return uniqueSetOfLinks;
    }

}



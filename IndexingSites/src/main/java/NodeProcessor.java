
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.RecursiveAction;


public class NodeProcessor extends RecursiveAction {

    private List<String> listOfSubLinks;

    public NodeProcessor(Set<String> setOfSubLinks) throws IOException {
        this.listOfSubLinks = new ArrayList<>(setOfSubLinks);

    }

    @Override
    protected void compute() {

        try {
            List<NodeProcessor> subTasks = new ArrayList<>();
            for (String link : listOfSubLinks) {
                randomDelayForThread();
                    HTMLParser parser = new HTMLParser(link);
                    synchronized (Storage.class) {
                        Storage.putDataToList(parser.getPath(), parser.getStatusCode(), parser.getContent());
                    }
                    NodeProcessor task = new NodeProcessor(parser.getListOfSubLinks());
                    subTasks.add(task);
                    task.fork();

            }

            for (NodeProcessor task : subTasks) {
                task.join();
            }

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
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


}



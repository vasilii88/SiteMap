
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLParser {

    private static String patternForSearchingSites = "";
    public static final String patternForURI = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?";
    private static Set<String> uniqueSetOfLinks = Collections.synchronizedSet(new TreeSet<>());

    private static int count;
    private final String url;
    private final Document document;


    public HTMLParser(String url) throws IOException, SQLException {
        this.url = url;
        this.document = initDocument();
    }

    private Document initDocument() throws IOException {

        if (document == null) {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();
        } else return document;
    }


    public Set<String> getListOfSubLinks() throws IOException {

        Set<String> listOfSubLinks = document.select("a")
                .eachAttr("abs:href")
                .stream()
                .filter(s -> s.startsWith(patternForSearchingSites))
                .filter(s ->  s.matches(patternForURI)) //s.endsWith("/") || s.endsWith(".pdf") )
                .filter(s -> uniqueSetOfLinks.add(s))
                .peek(s -> {
                    count++;
                    printCountOfLinks();
                })
                .collect(Collectors.toCollection(HashSet::new));
        listOfSubLinks.remove(url);
        return listOfSubLinks;
    }

    public synchronized String getPath() {
        String path = null;
        try {
            URI uri = new URI(url);
            path = uri.getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return path;
    }

    public synchronized int getStatusCode() {
        return document.connection().response().statusCode();
    }

    public synchronized String getContent() {
        return document.toString();
    }

    public Document getDocument() {
        return document;
    }

    private static void printCountOfLinks() {
        if (count % 100 == 0) {
            System.out.println("Количестово ссылок: " + count);
        }
    }


    public static void setPatternForSearchingSites(String pattern) {
        patternForSearchingSites = pattern;
    }

    public static Set<String> getUniqueSetOfLinks() {
        return uniqueSetOfLinks;
    }


}

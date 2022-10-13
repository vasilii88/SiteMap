
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLParser {

    private static String patternForSearchingSites = "";
    public static final String patternForURI = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?";


    private final String url;
    private final Document document;
    private final static Logger logger = LogManager.getLogger(HTMLParser.class);

    private static  Marker DB_MARKER= MarkerManager.getMarker("DB_LOG");
    private static  Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTIONS");


    public HTMLParser(String url) throws IOException, SQLException {
        this.url = url;
        this.document = initAndGetDocument();
    }

    public HTMLParser(HTMLParser parser) throws IOException, SQLException {
        this.url = parser.url;
        this.document = parser.document;
    }

    private Document initAndGetDocument() throws IOException {

        if (document == null) {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();
        } else return document;
    }


    public Set<String> getSetOfSubLinks() throws IOException {

        Elements elements = document.select("a");
        if (elements.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> setOfSubLinks = elements.eachAttr("abs:href")
                .stream()
                .filter(s -> s.startsWith(patternForSearchingSites))
                .filter(s -> s.matches(patternForURI)) //s.endsWith("/") || s.endsWith(".pdf") )
                .filter(s -> NodeProcessor.getUniqueSetOfLinks().add(s))
                .collect(Collectors.toCollection(HashSet::new));
        setOfSubLinks.remove(url);
        return setOfSubLinks;
    }

    public String getPath() {
        String path = null;
        try {
            URI uri = new URI(url);
            path = uri.getPath();
        } catch (URISyntaxException e) {
            logger.debug(EXCEPTION_MARKER,"",e);
        }
        return path;
    }

    public int getStatusCode() {
        return document.connection().response().statusCode();
    }

    public String getContent() {
        return document.toString();
    }

    public static void setPatternForSearchingSites(String pattern) {
        patternForSearchingSites = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HTMLParser parser = (HTMLParser) o;
        return url.equals(parser.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }


}

package indexWeb.models;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import indexWeb.services.HandlerDoc;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Node
{
    private Logger loggerNewNode = LogManager.getLogger("NewNode");


    private static String PATH_TO_ROOT_PAGE;
    public static final int maxLengthPath = 199;
    private static HashSet<String> listRefs = new HashSet<>();

    private final String path;
    private List<String> child;


    public Node(String address) {
        this.path = address;
        child = new ArrayList<>();
           try {
              makeChildren();
           }catch (Exception ex){
               ex.printStackTrace();
           }
    }

    private void makeChildren () {
        Document doc = getDoc();
        if (doc != null){
            saveChildes(doc);
            HandlerDoc handlerDoc = new HandlerDoc(doc);
            handlerDoc.createPage();
        }
    }

    private void saveChildes(Document doc){
        Elements elements = doc.select("a[href]");
        for (Element element : elements) {
            String ref = element.attr("href");

            if (ref.startsWith("/") && ref.length() > 1 && !ref.endsWith(".jpg") && !ref.endsWith(".png")
                    && !ref.endsWith(".jpeg") && !ref.endsWith(".JPG") && !ref.endsWith(".webp")) {
                String refRight =  ref.startsWith("//") ? ref.substring(1) : ref;
                if (!listRefs.contains(refRight)) {
                    listRefs.add(refRight);
                    String absoluteAddress = PATH_TO_ROOT_PAGE + refRight;
                    child.add(absoluteAddress);
                }
            }
        }
    }

    public List<String> getChild() {
        return child;
    }

    private Document getDoc() {
         Document doc = null;
        try {
            doc = Jsoup.connect(path).maxBodySize(0).get();
        }catch (HttpStatusException e) {
            HandlerDoc.createAndSavePageError(e.getUrl(), (e).getStatusCode());
        }
        catch (Exception exception) {
            System.out.println();
        }
        return doc;
    }

    public static void setPathToRootPage(String pathToRootPage) {
        PATH_TO_ROOT_PAGE = pathToRootPage;
    }

    public static String getPathToRootPage() {
        return PATH_TO_ROOT_PAGE;
    }
}




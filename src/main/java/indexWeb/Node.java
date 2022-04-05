package indexWeb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import indexWeb.services.HandlerDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Node
{
    private Logger loggerNewNode = LogManager.getLogger("NewNode");
    private Logger loggerNodeNot200 = LogManager.getLogger("NodeNot200");

    public static void setPathToRootPage(String pathToRootPage) {
        PATH_TO_ROOT_PAGE = pathToRootPage;
    }

    private static String PATH_TO_ROOT_PAGE;

    private final String path;
    private int responseStatus;
    private List<String> child;

    private static HashSet<String> listRefs = new HashSet<>();



    public Node(String address, Session session) {
        this.path = address;
        child = new ArrayList<>();
           try {
               makeChildren(session);
           }catch (Exception ex){
               ex.printStackTrace();
           }


    }



    private void makeChildren (Session session){
        System.out.println(path);
        Document doc = null;
        try {

            doc = Jsoup.connect(path).maxBodySize(0).get();
//           doc = Jsoup.connect(path)
//                    .data("query", "Java")
//                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
//                    .timeout(3000)
//                    .maxBodySize(0)
//                    .get();

        } catch (IOException e) {//TODO Здесь надо создать страницу, с кодом 404 и без поиска детей
            responseStatus = ((HttpStatusException) e).getStatusCode();
            loggerNodeNot200.info("Create new node → " + path + " => " + responseStatus);
            e.printStackTrace();
//            indexWeb.dao.Page page = new indexWeb.dao.Page();//Необходимо записывать страницу, но только страницу без текса
            return;
        }
        responseStatus = doc.connection().response().statusCode();
        loggerNewNode.info("Create new node → " + path + " => " + responseStatus);
        HandlerDoc handlerDoc = new HandlerDoc(doc, responseStatus);
        handlerDoc.createPage(session);



        Elements elements = doc.select("a");

        for (Element element : elements) {
            String ref = element.attr("href");

            if (ref.startsWith("/") && ref.length() > 1 && !ref.endsWith(".jpg") && !ref.endsWith(".png")
                    && !ref.endsWith(".jpeg") && !ref.endsWith(".JPG") && !ref.endsWith(".webp")) {
                if (!listRefs.contains(ref)) {
                    listRefs.add(ref);//TODO Надо ли добавлять до проверки, того, что адрес существует
                    String absoluteAddress = PATH_TO_ROOT_PAGE + ref;
                    child.add(absoluteAddress);
                }
            }
        }
    }



    public String getPath() {
        return path;
    }

    public List<String> getChild() {
        return child;
    }

    public void addChild(List<String> childAddress) {
        this.child = childAddress;
    }
}


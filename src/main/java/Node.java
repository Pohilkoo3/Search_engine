import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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



    public Node(String address, Session session, Transaction transaction) {
        this.path = address;
        child = new ArrayList<>();
           try {
               makeChildren(session, transaction);
           }catch (Exception ex){
               ex.printStackTrace();
           }


    }



    private void makeChildren (Session session, Transaction transaction){
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
//            Page page = new Page();//Необходимо записывать страницу, но только страницу без текса
            return;
        }
        responseStatus = doc.connection().response().statusCode();
        loggerNewNode.info("Create new node → " + path + " => " + responseStatus);
        HandlerDoc handlerDoc = new HandlerDoc(doc, responseStatus);
        handlerDoc.createPage(session, transaction);




//        if (responseStatus != 200){//TODO записать страницу код ответа, но не вытаскивать из дока больше ничего
//            System.out.println("++++++++++++++++>>>>>>>>>>>>>>>>>" + " → " + path + "  " + responseStatus);
//            loggerNodeNot200.info("Create new node → " + path + " => " + responseStatus);
//            return;
//        }
//        if (doc == null){
//            System.out.println("++++++++++++++++>>>>>>>>>>>>>>>>>" + " → " + path + "  " + responseStatus);
//            loggerNodeNot200.info("Create new node → " + path + " => " + responseStatus);
//            throw new NullPointerException("Пустая страница");
//        }

//        staticAttribute.info("create new page for doc → " + doc.location());


        Elements elements = doc.select("a");

        for (Element element : elements) {
            String ref = element.attr("href");

            if (ref.startsWith("/") && ref.length() > 1 && !ref.endsWith(".jpg") && !ref.endsWith(".png")
                    && !ref.endsWith(".jpeg") && !ref.endsWith(".JPG") && !ref.endsWith(".webp")) {
                if (!listRefs.contains(ref)) {
                    listRefs.add(ref);//TODO Надо ли добавлять до проверки, того, что адрес существует
                    String absoluteAddress = PATH_TO_ROOT_PAGE + ref;
                    child.add(absoluteAddress);



//                    if (isExistAddress(absoluteAddress)){
//                        continue;
//                    }

//                    makeChildren(absoluteAddress);
//                    Runnable task = () -> {
//                        try {
//                            makeChildren(absoluteAddress);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    };
//                    Thread thread = new Thread(task);
//                    thread.start();
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

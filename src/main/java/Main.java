import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main
{
    private static String PATH_TO_ROOT_PAGE = "http://www.playback.ru";
    private Logger loggerroot = LogManager.getRootLogger();
    private  static Logger staticAttribute = LogManager.getLogger("StaticAttribute");

    private static HashSet<String> listRefs = new HashSet<>();


    public static void main(String[] args) {
        Node.setPathToRootPage(PATH_TO_ROOT_PAGE);
//        Node node = new Node(PATH_TO_ROOT_PAGE);
//        HandlerDoc.fillField();
       new ForkJoinPool().invoke(new GetAllRefers(PATH_TO_ROOT_PAGE));
//       HandlerDoc.changeLemmas("метро", 25);

//        System.exit(-1);
//
////        Lemmatizator.getInfoAboutWord("ее").forEach(System.out::println);
//
//        try {
//        makeChildren(PATH_TO_ROOT_PAGE);//TODO пишем то что хотим проверить
//    } catch (Exception exception){
//        exception.printStackTrace();
//    }

    }

    private static void makeChildren (String path) throws IOException {
//        Document doc = Jsoup.connect(path).maxBodySize(0).get();

            Document doc = Jsoup.connect(path)
                    .data("query", "Java")
                    .userAgent("Chrome")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .maxBodySize(0)
                    .get();

        int responseStatus = doc.connection().response().statusCode();
        if (responseStatus != 200){//TODO записать страницу код ответа, но не вытаскивать из дока больше ничего
            return;
        }
        responseStatus = doc.connection().response().statusCode();
        HandlerDoc handlerDoc = new HandlerDoc(doc, responseStatus);
//        handlerDoc.createPage();
        staticAttribute.info("create new page for doc → " + doc.location());


        Elements elements = doc.select("a");

        for (Element element : elements) {
            String ref = element.attr("href");

            if (ref.startsWith("/") && ref.length() > 1 && !ref.endsWith(".jpg")) {
                if (!listRefs.contains(ref)) {
                    listRefs.add(ref);//TODO Надо ли добавлять до проверки, того, что адрес существует
                    String absoluteAddress = PATH_TO_ROOT_PAGE + ref;
//                    if (isExistAddress(absoluteAddress)){
//                        continue;
//                    }

                    makeChildren(absoluteAddress);
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



    private static boolean isExistAddress(String linkChildren){
        URL url;
        HttpURLConnection huc;
        int responseCode = 404;
        try {
            url = new URL(linkChildren);
            huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            responseCode = huc.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode == 404;
    }


}

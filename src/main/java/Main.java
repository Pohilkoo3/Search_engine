import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.SessionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Main
{
    private static String PATH_TO_ROOT_PAGE = "http://www.playback.ru";
    private Logger loggerroot = LogManager.getRootLogger();
    private static Logger loggerNods = LogManager.getLogger("LoggerNods");
    private static HashSet<String> listRefs = new HashSet<>();
    private static Session session;
    private static Transaction transaction;

    public static void main(String[] args) {
        session = SessionFactory.getSession().openSession();
        transaction = session.beginTransaction();

//        Page page = new Page();
//        page.setCode(200);
//        page.setPath("path");
//        page.setMediumText("Привет");
//        session.save(page);
//        transaction.commit();
//        session.close();

//        Document doc = null;
//        try {
//            doc = Jsoup.connect("http://www.lenta.ru")
//                    .data("query", "Java")
//                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
//                    .timeout(3000)
//                    .maxBodySize(0)
//                    .post();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        makeChildren(PATH_TO_ROOT_PAGE);//TODO пишем то что хотим проверить


//        Elements elements = doc.select("a");
//        Set<String> setElements =  elements.stream().map(e -> e.attr("href"))
//                .filter(e -> (e.startsWith("/") && e.length() > 1)).collect(Collectors.toSet()) ;
//        setElements.forEach(System.out::println);


    }

    private static void makeChildren(String path) {
        Document doc = null;
        try {
            doc = Jsoup.connect(path).maxBodySize(0).get();

//            doc = Jsoup.connect("https://dombulgakova.ru/")
//                    .data("query", "Java")
//                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
//                    .timeout(3000)
//                    .maxBodySize(0)
//                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseStatus = doc.connection().response().statusCode();
        Page page = new Page();
        page.setCode(responseStatus);
        page.setPath(path);
        page.setMediumText(doc.html());
        session.save(page);

        Elements elements = doc.select("a");

        System.out.println("Ответ на наш запрос ---------- " + path + " ==>> " +  responseStatus);
        for (Element element : elements) {
            String ref = element.attr("href");
            System.out.println(ref);
            if (ref.startsWith("/") && ref.length() > 1 && !ref.endsWith(".jpg")) {
                if (!listRefs.contains(ref)) {
                    listRefs.add(ref);//TODO Надо ли добавлять до проверки, того, что адрес существует
                    String absoluteAddress = PATH_TO_ROOT_PAGE + ref;
                    if (isExistAddress(absoluteAddress)){
                        continue;
                    }
                    System.out.println("Записано" +ref);
                    loggerNods.info("Записали ссылку => " + ref);
                    makeChildren(absoluteAddress);




                }
            }
        }
    }





    static void printResult(Node node){
        System.out.println("\t".repeat(node.getLevel()) + node.getAddress() + " → " + node.getLevel());
        try {
            Files.writeString((Paths.get("D:\\skillBox\\java_basics\\Multithreading\\Parsing_Lenta\\data\\map_site.txt")),
                    ("\t".repeat(node.getLevel()) + node.getAddress() + " → " + node.getLevel() + "\n"),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Node.staticListAllNods.get(node.getAddress()).getChildNods().size() == 0){
            return;
        }
        for (Node element : Node.staticListAllNods.get(node.getAddress()).getChildNods()) {
            printResult(element);
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

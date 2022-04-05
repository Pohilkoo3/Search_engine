import indexWeb.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import indexWeb.services.HandlerDoc;
import search.SearchStringService;
import utilitsForProgram.Lemmatizator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main
{
    private static String PATH_TO_ROOT_PAGE = " https://ipfran.ru";
    private Logger loggerroot = LogManager.getRootLogger();
    private  static Logger staticAttribute = LogManager.getLogger("StaticAttribute");

    private static HashSet<String> listRefs = new HashSet<>();


    public static void main(String[] args) {

//        indexWeb.services.ClearBase.clearDataBase();//запуск индексации сайта. Задаем путь, update in hibernate
//        Node.setPathToRootPage(PATH_TO_ROOT_PAGE);
//        new ForkJoinPool().invoke(new indexWeb.GetAllRefers(PATH_TO_ROOT_PAGE));



//        String search = "исследовательский центр";// запуск поисковой строки
//        SearchStringService searchStringService = new SearchStringService(search);
//        System.out.println("Количество найденных страниц → " + searchStringService.getListDisplayPagesResult().size());


    }



    //        Document doc = null;
//        try {
//            doc = Jsoup.connect(PATH_TO_ROOT_PAGE).maxBodySize(0).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    String text = "фузтмл";
//
//    Lemmatizator lemmatizator = new Lemmatizator();
//    List<String> words = Lemmatizator.getLemmas(text);
//        System.out.println(Lemmatizator.getInfoAboutWord(text));
//        words.forEach(System.out::println);






//        String text = "> Магаз наш < >asdfjasldjfha наш и еще раз < >   и последнмий Наш разочеппк<";
//        String one = "наш";
//        String one1 = "раз";
//        String one2 = "Наш";
//        String result = text.replaceAll(one + "|" + one2, "<b>" + one + "</b>") ;
//        System.out.println(result);
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()){
//            int start = matcher.start();
//            int stopSub = text.indexOf('<', start);
//            int startSub  = text.lastIndexOf('>', start);
//            System.out.println(text.substring(startSub, stopSub));
//        }
//
//        String text = "работает";
//        utils.Lemmatizator.getInfoAboutWord(text).forEach(System.out::println);
//        String text = "смартфон";
//        String text2 = "выбор";
//        System.out.println(text + " → " + utils.Lemmatizator.isServiceWord(text));
//        System.out.println(text2 + " → " + utils.Lemmatizator.isServiceWord(text2));

//        indexWeb.Node node = new indexWeb.Node(PATH_TO_ROOT_PAGE);

//       indexWeb.services.HandlerDoc.changeLemmas("метро", 25);


//
//        try {
//        makeChildren(PATH_TO_ROOT_PAGE);//TODO пишем то что хотим проверить
//    } catch (Exception exception){
//        exception.printStackTrace();
//    }



//    private static String markTextFromList(List<String> result, List<String> wordReplace, int count){    //TODO может билдер использовать
//            for (int i = count; i < wordReplace.size(); i++) {
//            String text1 = result.get(i).replaceAll(wordReplace.get(i),"<b>" + wordReplace.get(i) + "</b>");
//            result.add(text1);
//        }
//        count++;
//        if (count != wordReplace.size()-1) {
//            markTextFromList(result, wordReplace, count);
//        }
//
//        return result.get(result.size()-1);
//
//    }



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


    private static String getMarkText(String text, String wordReplace){    //TODO может билдер использовать
        String word2 = wordReplace.substring(0,1)
                .toUpperCase(Locale.ROOT)
                + wordReplace.substring(1);
        System.out.println(word2);
        String result = text.replaceAll(wordReplace,"<b>" + wordReplace + "</b>");
        String resultEnd = result.replaceAll(word2,"<b>" + word2 + "</b>");
        return resultEnd;
    }




}

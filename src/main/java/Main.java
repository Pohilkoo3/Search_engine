import indexWeb.models.Node;
import indexWeb.dao.CRUDService;
import indexWeb.services.PrepareBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import indexWeb.services.HandlerDoc;
import search.SearchStringService;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
//      private static String PATH_TO_ROOT_PAGE = "https://volochek.life";
    private static String PATH_TO_ROOT_PAGE = "https://www.adrsnab.ru";
//    private static String PATH_TO_ROOT_PAGE = "https://www.ivi.ru/2";
//    private static String PATH_TO_ROOT_PAGE = "http://www.playback.ru";
//    private static String PATH_TO_ROOT_PAGE = "https://dimonvideo.ru";

    private Logger loggerroot = LogManager.getRootLogger();
    private static Logger staticAttribute = LogManager.getLogger("StaticAttribute");

    private static HashSet<String> listRefs = new HashSet<>();


    public static void main(String[] args) {

        Node.setPathToRootPage(PATH_TO_ROOT_PAGE);
        try {
            PrepareBase.createAndPrepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long start = System.currentTimeMillis();
        new ForkJoinPool().invoke(new indexWeb.GetAllRefers(PATH_TO_ROOT_PAGE));
        try {
            CRUDService.savePageHibernate2();
            CRUDService.multiInsertLemmas(HandlerDoc.getSetAllLemmas());
            CRUDService.executeMultiInsertIndex();
            CRUDService.setIndexPathPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long during = (System.currentTimeMillis() - start) / 1000;
        System.out.println("====================================>>> Time processing " + during);


//        String search = "Фонарь аварийной остановки";// запуск поисковой строки
//        SearchStringService searchStringService = new SearchStringService(search);
//        System.out.println("Количество найденных страниц → " + searchStringService.getListDisplayPagesResult().size());





    }

}


/**

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

 //        indexWeb.models.Node node = new indexWeb.models.Node(PATH_TO_ROOT_PAGE);

 //       indexWeb.services.HandlerDoc.changeLemmas("метро", 25);


 //



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






 private static String getMarkText(String text, String wordReplace){    //TODO может билдер использовать
 String word2 = wordReplace.substring(0,1)
 .toUpperCase(Locale.ROOT)
 + wordReplace.substring(1);
 System.out.println(word2);
 String result = text.replaceAll(wordReplace,"<b>" + wordReplace + "</b>");
 String resultEnd = result.replaceAll(word2,"<b>" + word2 + "</b>");
 return resultEnd;
 }
 */

package indexWeb.services;
import indexWeb.models.Node;
import indexWeb.dao.CRUDService;
import indexWeb.models.Lemma;
import indexWeb.models.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import utilitsForProgram.Lemmatizator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerDoc
{

    private static CopyOnWriteArraySet<Lemma> setAllLemmas = new CopyOnWriteArraySet<>();
    private static AtomicInteger idLemma = new AtomicInteger(1);
    private static AtomicInteger idPage = new AtomicInteger(0);

    private Document doc;
    private static int count;

    private static Logger loggerNods = LogManager.getLogger("LoggerNods");
    private static Logger loggerNodeNot200 = LogManager.getLogger("NodeNot200");

    public HandlerDoc(Document doc) {
        this.doc = doc;
    }

    public void createPage(){
        Page page = createAndSavePage(doc);
        CRUDService.addPageHibernate(page);


        List<String> titleWords = Lemmatizator.getLemmas(doc.title());
        List<String> bodyWords = Lemmatizator.getLemmas(doc.body().html());

        HashSet<String> uniqueLemmasInPage = new HashSet<>(titleWords);
        uniqueLemmasInPage.addAll(bodyWords);
        for (String word : uniqueLemmasInPage) {
            Lemma lemma = getLemmaByName(word);
            if (lemma != null){
                lemma.setFrequency(lemma.getFrequency() + 1);
            }
            else {
                lemma = new Lemma();
                lemma.setId(idLemma.incrementAndGet());
                lemma.setLemma(word);
                lemma.setFrequency(1);
                setAllLemmas.add(lemma);
            }


            HashMap<String, Integer> titleWordsMap = getMapLemmas(titleWords);
            HashMap<String, Integer> bodyWordsMap = getMapLemmas(bodyWords);
            float rank = 0;
            rank = titleWordsMap.containsKey(word) ? titleWordsMap.get(word) * 1 : rank;
            rank = bodyWordsMap.containsKey(word) ? rank + bodyWordsMap.get(word) * 0.8f : rank;

            CRUDService.countIndex(lemma.getId(), page.getId(), rank);

        }
//        System.out.println(setAllLemmas.size());
    }

    public static Page createAndSavePage(Document doc){
        String path = doc.location().replaceAll(Node.getPathToRootPage(), "");
        Page page = new Page();
        int responseStatus = doc.connection().response().statusCode();
        page.setId(idPage.incrementAndGet());
        page.setCode(responseStatus);
        page.setPath(path);
        page.setContent(doc.html());
        count++;
//        System.out.println("pages " + count);
//        loggerNods.info("Сохранили страницу => " + path);
        return page;
    }
    public static void createAndSavePageError(String path, int statusCode){
        Page page = new Page();
        page.setId(idPage.incrementAndGet());
        page.setCode(statusCode);
        page.setPath(path.replaceAll(Node.getPathToRootPage(), ""));
        page.setContent("");
        CRUDService.addPageHibernate(page);
//        loggerNodeNot200.info("Сохранили страницу с ошибкой " + statusCode + ". → " + path);
    }

    private static Lemma getLemmaByName(String word){
        return setAllLemmas.stream().filter(l -> l.getLemma().equals(word)).findFirst().orElse(null);
    }
    public static HashMap<String, Integer> getMapLemmas(List<String> result){
        Collections.sort(result);
        HashMap<String, Integer> wordsInPartPage = new HashMap<>();
        int count = 1;
        for (int i = 1; i < result.size(); i++) {
            if (!result.get(i).equals(result.get(i-1)) || i == (result.size()-1)){
                wordsInPartPage.put(result.get(i-1), count);
                count = 0;
            }
            count++;
        } return wordsInPartPage;
    }

    public static CopyOnWriteArraySet<Lemma> getSetAllLemmas() {
        return setAllLemmas;
    }
}

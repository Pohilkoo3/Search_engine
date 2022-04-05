package indexWeb.services;

import indexWeb.dao.PageDao;
import indexWeb.models.IndexSearch;
import indexWeb.models.Lemma;
import indexWeb.models.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.jsoup.nodes.Document;
import utilitsForProgram.Lemmatizator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HandlerDoc
{


    private static HashSet<String> setAllLemmas = new HashSet<>();

    private Document doc;
    private int responseStatus;

    private static Logger loggerNods = LogManager.getLogger("LoggerNods");


    public HandlerDoc(Document doc, int responseStatus) {
        this.doc = doc;
        this.responseStatus = responseStatus;

    }

    public void createPage(Session session){

        String path = doc.location();

        Page page = new Page();//Создаю страницу и записываю
        PageDao pageDao = new PageDao(session);

            page.setCode(responseStatus);
            page.setPath(path);
            page.setContent(doc.html());
            pageDao.savePage(page);


        loggerNods.info("Сохранили страницу => " + path);


        List<String> titleWords = Lemmatizator.getLemmas(doc.title());//Создаю и записывю объект индекс
        List<String> bodyWords = Lemmatizator.getLemmas(doc.body().html());

        HashSet<String> uniqueLemmasInPage = new HashSet<>(titleWords);//Список всех лемм на странцице
        uniqueLemmasInPage.addAll(bodyWords);


        HashMap<String, Integer> titleWordsMap = getMapLemmas(titleWords);
        HashMap<String, Integer> bodyWordsMap = getMapLemmas(bodyWords);
        for (String word : uniqueLemmasInPage) {
            Lemma lemma = pageDao.getLemmaByName(word);//Обрабатываем леммы на странице
            if (lemma!=null){
                lemma.setFrequency(lemma.getFrequency() + 1);

            } else {
              lemma = new Lemma();
               lemma.setLemma(word);
               lemma.setFrequency(1);
            }

            pageDao.saveLemma(lemma);




            float rank = 0;
                rank = titleWordsMap.containsKey(word) ? titleWordsMap.get(word) * 1 : rank;
                rank = bodyWordsMap.containsKey(word) ? rank + bodyWordsMap.get(word) * 0.8f : rank;
            IndexSearch indexSearch = new IndexSearch();
            indexSearch.setPage_id(page.getId());
            indexSearch.setLemma_id(lemma.getId());
            indexSearch.setRankIndex(rank);

            pageDao.saveIndex(indexSearch);



        }
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


}

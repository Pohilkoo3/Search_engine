import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Document;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HandlerDoc
{

//    private Session session = SessionFactory.getSession().openSession();
//    private Transaction transaction = session.beginTransaction();
    private static HashSet<String> setAllLemmas = new HashSet<>();

    private Document doc;
    private int responseStatus;

    private static Logger loggerNods = LogManager.getLogger("LoggerNods");


    public HandlerDoc(Document doc, int responseStatus) {
        this.doc = doc;
        this.responseStatus = responseStatus;

    }

    public void createPage(Session session, Transaction transaction){

        String path = doc.location();

        Page page = new Page();//Создаю страницу и записываю
        PageDao pageDao = new PageDao(session, transaction);

            page.setCode(responseStatus);
            page.setPath(path);
            page.setMediumText(doc.html());
            pageDao.savePage(page);




        loggerNods.info("Сохранили страницу => " + path);


//TODO надо переделать чтобы он текст получал не из всего хеда, а из тайтла.
        List<String> headWords = Lemmatizator.getLemmas(doc.head().html());//Создаю и записывю объект индекс
        List<String> bodyWords = Lemmatizator.getLemmas(doc.body().html());

        HashSet<String> uniqueLemmasInPage = new HashSet<>(headWords);//Список всех лемм на странцице
        uniqueLemmasInPage.addAll(bodyWords);



        HashMap<String, Integer> headWordsMap = getMapLemmas(headWords);
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

            //TODO вытащиьть из БД лемму и если есть, то прибывить к повторящке 1, а если нет, то создать новую


            float rank = 0;
                rank = headWordsMap.containsKey(word) ? headWordsMap.get(word) * 1 : rank;
                rank = bodyWordsMap.containsKey(word) ? rank + bodyWordsMap.get(word) * 0.8f : rank;
            IndexSearch indexSearch = new IndexSearch();
            indexSearch.setPage_id(page.getId());//Может быть ошибка, так как page еще не создана в БД. Тогда надо будет доставать из БД.
            indexSearch.setLemma_id(lemma.getId());//TODO надо забрать ID для леммы из БД
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

//    public void fillField(){
//        Field fieldTitle = new Field("title", "title", 1f);
//        Field fieldHead = new Field("head", "head", 0.8f);
//        session.save(fieldTitle);
//        session.save(fieldHead);
//    }




//    public void changeLemmas(String name, int frequency){
//        int id =  (int) session.createSQLQuery("SELECT id FROM search_engine.lemma where lemma =\"" + name + "\"" )
//                .stream().findFirst().orElse(0);
//        Lemma lemma = session.get(Lemma.class, id);
//        System.out.println(lemma.getId() + " → " + lemma.getLemma()+ " → " + lemma.getFrequency() + " раз." );
//        lemma.setFrequency(frequency);
//
//        transaction.commit();
//        System.out.println("After change ------------------------->>>>>>>>>>>>>>>>>>>");
//        lemma = session.get(Lemma.class, id);
//        System.out.println(lemma.getId() + " → " + lemma.getLemma()+ " → " + lemma.getFrequency() + " раз." );
//    }



}

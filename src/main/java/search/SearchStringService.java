package search;

import indexWeb.models.IndexSearch;
import indexWeb.models.Lemma;
import indexWeb.models.Page;
import utilitsForProgram.Lemmatizator;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utilitsForProgram.HibernateSessionFactory;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class SearchStringService {

    private Session session;

    private final List<Lemma> lemmaInRequest;
    private final List<Integer> listResultPageId;
    private final List<DisplayPage> displayPages;


    public SearchStringService(String request) {

        lemmaInRequest = new ArrayList<>();
        listResultPageId = new ArrayList<>();
        displayPages = new ArrayList<>();
        sessionInit();
        List<Lemma> list = getSortedListLemmas(Lemmatizator.getLemmas(request));
        getUniquePageForStringSearch(list);
        getDisplayPages();
        getSnippet();

        session.close();         /** Смотреть внимательно. Здесь закрываю сессию после обработки **/

    }

    private String getSnippet(){
        for (DisplayPage displayPage : displayPages) {
            System.out.println(displayPage.getUri());


            List<String> sortedRanksLemmasPage = displayPage.getMapLemmasAndRankOnPage().entrySet().stream()
                   .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                    .map(e -> e.getKey())
                    .collect(Collectors.toList());

            //TODO работать отсюда, теперь посмотреть в каком фрагменте встречается больше всего слов из запроса и промаркировать этот текст и вывести его на печать
            //TODO еще в фрагмента когда матчер ищет, то наш и Наш для него разные слова. Надо доделать
            List<String> fragmentText = new ArrayList<>();
            for (String word : sortedRanksLemmasPage) {
                fragmentText = getFragmentTextWithLemma(displayPage.getContent(), word);
                if (fragmentText.size() == 0){
                    continue;
                }
            }

            //TODO получил несколько текстов

            HashMap<String, Integer> res = getRepeatWordInPage(fragmentText);
            String maxRepeats =res.entrySet().stream().max((s1,s2) ->s1.getValue().compareTo(s2.getValue())).get().getKey();
            String markedText = markTextFromList(maxRepeats);
            System.out.println(markedText);


        }


        return null;
    }

    private HashMap<String, Integer> getRepeatWordInPage(List<String> fragmentText){
        HashMap<String, Integer> result = new HashMap<>();
        for (String fragmentsText : fragmentText) {
            int countRepeats = 0;
            for (Lemma lemma : lemmaInRequest) {
                countRepeats = fragmentsText.contains(lemma.getLemma()) ? countRepeats + 1 : countRepeats;
            }
            result.put(fragmentsText, countRepeats);
        }
        return result;
    }

    private void sessionInit() {
        org.hibernate.SessionFactory sessionFactory = HibernateSessionFactory.getSession();
       session = sessionFactory.openSession();

    }

     public Lemma getLemmaByNameHQL(String name) {
        String hql = "FROM " + Lemma.class.getSimpleName() +  " WHERE lemma='" + name + "'";
        return  (Lemma) session.createQuery(hql).stream()
                .findAny().orElse(null);
    }


    private String markTextFromList(String text){    //TODO может билдер использовать
        List<String> wordReplace = lemmaInRequest.stream().map(l -> l.getLemma()).collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        result.add(text);
        int count = 0;
        for (int i = 0; i < wordReplace.size(); i++) {
            if (!text.contains(wordReplace.get(i))){
                continue;
            }
            String text1 = markText(result.get(count), wordReplace.get(i));
            result.add(text1);
            count++;
        }
        return result.get(result.size()-1);
    }
    private static String markText(String text, String wordReplace){    //TODO может билдер использовать
        String word2 = wordReplace.substring(0,1)
                .toUpperCase(Locale.ROOT)
                + wordReplace.substring(1);
//        System.out.println(word2);
        String result = text.replaceAll(wordReplace,"<b>" + wordReplace + "</b>");
        String resultEnd = result.replaceAll(word2,"<b>" + word2 + "</b>");
        return resultEnd;
    }
    private List<String> getFragmentTextWithLemma(String text, String word){
       String word2 = word.substring(0,1)
                .toUpperCase(Locale.ROOT)
                + word.substring(1);
       List<String> someFragments = new ArrayList<>();
        String regex = word + "|" + word2;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            int start = matcher.start();
            int stopSub = text.indexOf('<', start);
            int startSub  = text.lastIndexOf('>', start);
            someFragments.add(text.substring(startSub + 1, stopSub));
        }

        return someFragments;
    }

    public List<Lemma> getSortedListLemmas(List<String> wordsInRequest){
        for (String word : wordsInRequest) {
            Lemma lemma1 = getLemmaByNameHQL(word);
            if (lemma1!=null){
                lemmaInRequest.add(lemma1);
            }
        }
        return lemmaInRequest.stream().sorted(Comparator.comparing(Lemma::getFrequency)).toList();
    }

    private void getDisplayPages(){


        for (Integer idPage : listResultPageId) {
            Page page = getPage(idPage);

            String content = page.getContent();
            Document doc = Jsoup.parse(content);



            DisplayPage displayPage = new DisplayPage(idPage, page.getPath(), doc.title());
            displayPage.setContent(content);
            float sumRank = 0;
            for (Lemma lemma : lemmaInRequest) {
                int lemmaId = lemma.getId();
                String hql = "SELECT rankIndex FROM " +IndexSearch.class.getSimpleName() +" where lemma_id="
                        + lemmaId + " and page_id=" + idPage;
                float rankLemmas = (Float) session.createQuery(hql).stream().findAny().orElse(0);
                sumRank+=rankLemmas;
                displayPage.putLemmasInDisplayPage(lemma.getLemma(), rankLemmas);
            }
            displayPage.setAbsRelevance(sumRank);
            displayPages.add(displayPage);

        }

        float maxRank = displayPages.stream().map(p -> p.getAbsRelevance()).max((p1, p2) -> p1.compareTo(p2)).orElse(1f);
        displayPages.stream().forEach(p -> p.setRelevance(getMathRound(p.getAbsRelevance()/maxRank, 2)));


    }
    public void getUniquePageForStringSearch(List<Lemma> list){
        listResultPageId.addAll(getIdPagesForLemmas(list.get(0)));
        list.stream().skip(0).forEach(l -> listResultPageId.retainAll(getIdPagesForLemmas(l)));
    }

    private Page getPage(int idPage){
        String hql = "FROM " + Page.class.getSimpleName() +  " WHERE id=" + idPage;
        return (Page) session.createQuery(hql).stream().findAny().orElse(null);
    }

    public List<Integer> getIdPagesForLemmas(Lemma lemma){
        List<Integer> pageId = new ArrayList<>();
        String hql = "FROM " + IndexSearch.class.getSimpleName() +  " WHERE lemma_id=" + lemma.getId();
        List<IndexSearch> indexSearchList = session.createQuery(hql).list();
        indexSearchList.forEach(e -> pageId.add(e.getPage_id()));
        return pageId;
    }
    private float getMathRound(float input, int afterComma){
        float tmp = (float) (input * Math.pow(10, afterComma));
        return  ((float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / 100);
    }

    public List<DisplayPage> getListDisplayPagesResult(){
        List<DisplayPage> result = new ArrayList<>();
        if (displayPages.size() == 0){
            return result;
        }
        result.addAll(displayPages);
        return result;
    }
}



package search;

import java.util.HashMap;

public class DisplayPage
{

    private String uri;
    private int idPage;
    private String title;
    private String snippet;
    private float relevance;
    private float AbsRelevance;
    private final HashMap<String, Float> mapLemmasAndRankOnPage;
    private String content;

    public DisplayPage(int idPage, String path, String title) {
        this.idPage = idPage;
        this.uri = path;
        this.title = title;
        mapLemmasAndRankOnPage = new HashMap<>();
    }

    public HashMap<String, Float> getMapLemmasAndRankOnPage() {
        return mapLemmasAndRankOnPage;
    }

    public void putLemmasInDisplayPage(String word, float rank){
        mapLemmasAndRankOnPage.put(word, rank);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getIdPage() {
        return idPage;
    }

    public void setIdPage(int idPage) {
        this.idPage = idPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public float getRelevance() {
        return relevance;
    }

    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }
    public static int compareByRelevance(DisplayPage dp1, DisplayPage dp2) {

        return dp1.getRelevance() > dp2.getRelevance() ? -1 : (dp1.getRelevance() == dp2.getRelevance() ? 0 : 1);
    }

    public float getAbsRelevance() {
        return AbsRelevance;
    }

    public void setAbsRelevance(float absRelevance) {
        this.AbsRelevance = absRelevance;
    }

    @Override
    public String toString() {
        return "indexWeb.dao.Page " + uri + " => " + title + " , id: " + idPage +
                ", relevance=" + relevance + " Абсолютная релевантность: " + AbsRelevance;
    }
}

package indexWeb.dao;

import indexWeb.models.IndexSearch;
import indexWeb.models.Lemma;
import indexWeb.models.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PageDao
{
    private  Session session;
    private Transaction transaction;

    public PageDao(Session session) {
        this.session = session;
        this.transaction = session.beginTransaction();
    }

    public void savePage(Page page){

      try {
          session.save(page);

      }catch (Exception ex){

          session.clear();
      }
    }

    public Lemma getLemmaByName(String name){//Пишет что не инт нужен а интегер
        int id =  (int) session.createSQLQuery("SELECT id FROM search_engine.lemma where lemma =\"" + name + "\"" )
                .stream().findFirst().orElse(0);
        return  id == 0 ? null : session.get(Lemma.class, id);
    }

    public void saveLemma(Lemma lemma){
        try {
            session.save(lemma);
            transaction.commit();
            transaction = session.beginTransaction();
        }catch (Exception ex){

            session.clear();
        }

    }
    public void saveIndex(IndexSearch indexSearch){
        try {
            session.save(indexSearch);
        }catch (Exception ex){
         session.clear();
        }
    }


}

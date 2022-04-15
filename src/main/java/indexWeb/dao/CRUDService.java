package indexWeb.dao;

import indexWeb.models.Node;
import indexWeb.models.Lemma;
import indexWeb.models.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utilitsForProgram.HibernateSessionFactory;
import utilitsForProgram.SQLConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class CRUDService
{
    private static Connection connection = SQLConnection.getConnectionDataBase();
    private static Session session = HibernateSessionFactory.getSession().openSession();

    private static CopyOnWriteArrayList<Page> listPages = new CopyOnWriteArrayList<>();
    private static int BATCH_SIZE = 100;

    private static StringBuffer stringBufferLemmas = new StringBuffer();
    private static StringBuffer stringBufferIndex = new StringBuffer();


    public static void setIndexPathPage() throws SQLException {
        if (connection == null) {
           connection =   SQLConnection.getConnectionDataBase();
        }
        connection.createStatement().execute("CREATE INDEX page_index ON page(path)");
    }


    /**   Lemma  **/
    public static void multiInsertLemmas(CopyOnWriteArraySet<Lemma> lemmasFrequency) throws SQLException {
        for (Lemma lemma : lemmasFrequency) {
            stringBufferLemmas.append((stringBufferLemmas.length() == 0 ? "" : ",")
                    + ("(" +lemma.getId() + ", " + lemma.getFrequency() + ", '" + lemma.getLemma() + "')"));
            if (stringBufferLemmas.length() > 3_000_000) {
                executeMultiInsertLemmas();
                stringBufferLemmas = new StringBuffer();
            }
        }
        executeMultiInsertLemmas();
    }

    public static void executeMultiInsertLemmas() throws SQLException {
        if (stringBufferLemmas.length() == 0){
            return;
        }
        if (connection == null) {
            connection =   SQLConnection.getConnectionDataBase();
        }
        String sql = "INSERT INTO lemma(id, frequency, lemma) VALUES"+ stringBufferLemmas;
        connection.createStatement().execute(sql);
    }

    /**   Index  **/
    public static void countIndex(int lemmaId, int pageId, float rank)  {
        stringBufferIndex.append((stringBufferIndex.length() == 0 ? "" : ",")
                + ("(" + lemmaId + ", " + pageId + ", " + rank + ")"));
        if (stringBufferIndex.length() > 3_000_000) {
            executeMultiInsertIndex();
            stringBufferIndex = new StringBuffer();
        }
    }
    public static void  executeMultiInsertIndex() {
        if (stringBufferIndex.length() == 0){
            return;
        }
        if (connection == null) {
            connection = SQLConnection.getConnectionDataBase();
        }
        String sql = "INSERT INTO index_search(lemma_id, page_id, rankIndex) VALUES"+ stringBufferIndex;
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**   Page  **/
    public static void addPageHibernate(Page page) {
        if (page.getPath().length() < Node.maxLengthPath){
            listPages.add(page);
        }
        if (listPages.size() == BATCH_SIZE){
            savePageHibernate2();
            listPages = new CopyOnWriteArrayList<>();
        }
    }

    public static void savePageHibernate2() {
        if (listPages.size() == 0){
            return;
        }
        if (session == null){
            session = HibernateSessionFactory.getSession().openSession();
        }
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for ( int i=0; i< listPages.size(); i++ ) {
                session.save(listPages.get(i));
                if( i % 50 == 0 || i == listPages.size()-1) {
                    session.flush();
                    session.clear();
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            System.out.println("Ошибка при добавлении данных страницы");
            e.printStackTrace();
        }
    }

}

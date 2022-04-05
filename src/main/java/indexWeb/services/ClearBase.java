package indexWeb.services;

import indexWeb.models.Field;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utilitsForProgram.HibernateSessionFactory;


import javax.persistence.Query;

public class ClearBase {

    private static Session session = HibernateSessionFactory.getSession().openSession();

    public static void clearDataBase(){
        Transaction transaction = session.beginTransaction();
        String sqlDeletePage = "DROP TABLE IF EXISTS page";
        String sqlDeleteLemma = "DROP TABLE IF EXISTS lemma";
        String sqlDeleteField = "DROP TABLE IF EXISTS field";
        String sqlDeleteIndexSearch = "DROP TABLE IF EXISTS index_search";
        Query query = session.createSQLQuery(sqlDeletePage);
        Query query2 = session.createSQLQuery(sqlDeleteLemma);
        Query query3 = session.createSQLQuery(sqlDeleteField);
        Query query4 = session.createSQLQuery(sqlDeleteIndexSearch);
        query.executeUpdate();
        query2.executeUpdate();
        query3.executeUpdate();
        query4.executeUpdate();
        transaction.commit();
        session.close();
        session = HibernateSessionFactory.getSession().openSession();
        indexWeb.models.Field fieldTitle = new Field("title", "title", 1f);
        Field fieldHead = new Field("head", "head", 0.8f);
        session.save (fieldTitle);
        session.save(fieldHead);
        session.close();
    }

}

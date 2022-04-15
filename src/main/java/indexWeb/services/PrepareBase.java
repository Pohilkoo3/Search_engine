package indexWeb.services;
import org.hibernate.Session;
import utilitsForProgram.HibernateSessionFactory;
import utilitsForProgram.SQLConnection;
import java.sql.*;



public class PrepareBase {


    public static void createAndPrepare() throws SQLException {
               Connection connection = SQLConnection.getConnectionServer();
                connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS " + SQLConnection.getDbName());
                connection.createStatement().execute("USE " + SQLConnection.getDbName());
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("DROP TABLE IF EXISTS index_search");
                Session session = HibernateSessionFactory.getSession().openSession();
                connection.createStatement().execute("INSERT INTO field(name, selector, weight) " +
                        "VALUES('title', 'title', 1), ('head', 'head', 0.8)");
    }
}

package utils;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactory
{
    private static org.hibernate.SessionFactory sessionFactory;

    public SessionFactory() {}

    public static org.hibernate.SessionFactory getSession(){
        if (sessionFactory == null) {

            try {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
                Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }


}
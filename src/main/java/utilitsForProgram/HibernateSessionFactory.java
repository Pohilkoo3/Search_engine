package utilitsForProgram;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSessionFactory {

        private static org.hibernate.SessionFactory sessionFactory;

        public HibernateSessionFactory() {}

        public static org.hibernate.SessionFactory getSession() {
            if (sessionFactory == null) {
                try {
                    StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
                    Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
                    sessionFactory = metadata.getSessionFactoryBuilder().build();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return sessionFactory;
        }

}

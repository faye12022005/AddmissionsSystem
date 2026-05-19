package org.AdmissionsSystem.util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.AdmissionsSystem.config.AppConfig;
import org.AdmissionsSystem.models.Users;
import org.AdmissionsSystem.models.XtBangquydoi;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;
import org.AdmissionsSystem.models.XtDiemthixettuyen;
import org.AdmissionsSystem.models.XtDiemVsat;
import org.AdmissionsSystem.models.XtNganh;
import org.AdmissionsSystem.models.XtNganhTohop;
import org.AdmissionsSystem.models.XtNguyenvongxettuyen;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;
import org.AdmissionsSystem.models.XtTohopMonthi;
import org.AdmissionsSystem.models.XtUutien;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    /**
     * Thread-safe singleton SessionFactory getter
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    try {
                        LOGGER.log(Level.INFO, "Initializing Hibernate SessionFactory...");
                        LOGGER.log(Level.INFO, "Database URL: " + AppConfig.getJdbcUrl());

                        Configuration configuration = new Configuration();
                        configuration.setProperties(buildProperties());
                        registerAnnotatedClasses(configuration);

                        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                                .applySettings(configuration.getProperties())
                                .build();

                        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                        registerShutdownHook();
                        LOGGER.log(Level.INFO, "✓ SessionFactory initialized successfully");
                    } catch (Throwable ex) {
                        LOGGER.log(Level.SEVERE, "✗ SessionFactory creation failed: " + ex.getMessage(), ex);
                        throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex.getMessage());
                    }
                }
            }
        }
        return sessionFactory;
    }

    private static Properties buildProperties() {
        Properties properties = new Properties();

        properties.put("hibernate.connection.driver_class", AppConfig.getDbDriver());
        properties.put("hibernate.connection.url", AppConfig.getJdbcUrl());
        properties.put("hibernate.connection.username", AppConfig.getDbUser());
        properties.put("hibernate.connection.password", AppConfig.getDbPassword());

        properties.put("hibernate.dialect", AppConfig.getHibernateDialect());
        properties.put("hibernate.show_sql", String.valueOf(AppConfig.isHibernateShowSql()));
        properties.put("hibernate.format_sql", String.valueOf(AppConfig.isHibernateFormatSql()));
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.hbm2ddl.auto", AppConfig.getHibernateHbm2ddlAuto());
        properties.put("hibernate.globally_quoted_identifiers", "true");

        properties.put("hibernate.c3p0.min_size", String.valueOf(AppConfig.getC3p0MinSize()));
        properties.put("hibernate.c3p0.max_size", String.valueOf(AppConfig.getC3p0MaxSize()));
        properties.put("hibernate.c3p0.timeout", String.valueOf(AppConfig.getC3p0Timeout()));
        properties.put("hibernate.c3p0.max_statements", String.valueOf(AppConfig.getC3p0MaxStatements()));
        properties.put("hibernate.c3p0.idle_test_period", String.valueOf(AppConfig.getC3p0IdleTestPeriod()));
        // Use a simple query to validate connections
        properties.put("hibernate.c3p0.preferredTestQuery", "SELECT 1");

        properties.put("hibernate.jdbc.batch_size", String.valueOf(AppConfig.getJdbcBatchSize()));
        properties.put("hibernate.jdbc.fetch_size", String.valueOf(AppConfig.getJdbcFetchSize()));
        properties.put("hibernate.cache.use_second_level_cache",
                String.valueOf(AppConfig.isHibernateUseSecondLevelCache()));
        properties.put("hibernate.cache.use_query_cache", String.valueOf(AppConfig.isHibernateUseQueryCache()));
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");

        // Enable backtick quoting for all identifiers to handle reserved keywords as
        // column names
        properties.put("hibernate.globally_quoted_identifiers",
                String.valueOf(AppConfig.getGloballyQuotedIdentifiers()));
        return properties;
    }

    private static void registerAnnotatedClasses(Configuration configuration) {
        configuration.addAnnotatedClass(Users.class);
        configuration.addAnnotatedClass(XtBangquydoi.class);
        configuration.addAnnotatedClass(XtDiemcongxetuyen.class);
        configuration.addAnnotatedClass(XtDiemthixettuyen.class);
        configuration.addAnnotatedClass(XtDiemVsat.class);
        configuration.addAnnotatedClass(XtNganh.class);
        configuration.addAnnotatedClass(XtNganhTohop.class);
        configuration.addAnnotatedClass(XtNguyenvongxettuyen.class);
        configuration.addAnnotatedClass(XtThisinhxettuyen25.class);
        configuration.addAnnotatedClass(XtTohopMonthi.class);
        configuration.addAnnotatedClass(XtUutien.class);
        configuration.addAnnotatedClass(XtDiemVsat.class);
    }

    /**
     * Auto-cleanup on JVM shutdown
     */
    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.log(Level.INFO, "Shutting down SessionFactory...");
            closeSessionFactory();
        }));
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            try {
                sessionFactory.close();
                LOGGER.log(Level.INFO, "✓ SessionFactory closed successfully");
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Error closing SessionFactory: " + ex.getMessage(), ex);
            } finally {
                sessionFactory = null;
            }
        }
    }
}

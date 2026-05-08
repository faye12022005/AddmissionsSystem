package org.AdmissionsSystem.util;

import java.util.Properties;
import org.AdmissionsSystem.config.AppConfig;
import org.AdmissionsSystem.models.ToHopMon;
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
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.setProperties(buildProperties());
                registerAnnotatedClasses(configuration);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Throwable ex) {
                throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex.getMessage());
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
        // Force disable SQL console noise ("Hibernate: ...") for normal runtime.
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "false");
        properties.put("hibernate.use_sql_comments", "false");
        properties.put("hibernate.hbm2ddl.auto", AppConfig.getHibernateHbm2ddlAuto());

        properties.put("hibernate.c3p0.min_size", String.valueOf(AppConfig.getC3p0MinSize()));
        properties.put("hibernate.c3p0.max_size", String.valueOf(AppConfig.getC3p0MaxSize()));
        properties.put("hibernate.c3p0.timeout", String.valueOf(AppConfig.getC3p0Timeout()));
        properties.put("hibernate.c3p0.max_statements", String.valueOf(AppConfig.getC3p0MaxStatements()));
        properties.put("hibernate.c3p0.idle_test_period", String.valueOf(AppConfig.getC3p0IdleTestPeriod()));

        properties.put("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        properties.put("hibernate.jdbc.batch_size", String.valueOf(AppConfig.getJdbcBatchSize()));
        properties.put("hibernate.jdbc.fetch_size", String.valueOf(AppConfig.getJdbcFetchSize()));

        // Enable backtick quoting for all identifiers to handle reserved keywords as
        // column names
        properties.put("hibernate.globally_quoted_identifiers",
                String.valueOf(AppConfig.getGloballyQuotedIdentifiers()));
        return properties;
    }

    private static void registerAnnotatedClasses(Configuration configuration) {
        configuration.addAnnotatedClass(Users.class);
        configuration.addAnnotatedClass(ToHopMon.class);
        configuration.addAnnotatedClass(XtBangquydoi.class);
        configuration.addAnnotatedClass(XtDiemcongxetuyen.class);
        configuration.addAnnotatedClass(XtDiemthixettuyen.class);
        configuration.addAnnotatedClass(XtDiemVsat.class);
        configuration.addAnnotatedClass(XtNganh.class);
        configuration.addAnnotatedClass(XtNganhTohop.class);
        configuration.addAnnotatedClass(XtNguyenvongxettuyen.class);
        configuration.addAnnotatedClass(XtThisinhxettuyen25.class);
        configuration.addAnnotatedClass(XtTohopMonthi.class);
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}

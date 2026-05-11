package org.AdmissionsSystem.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    private AppConfig() {
    }

    public static String getDbHost() {
        return getEnv("DB_HOST", "");
    }

    public static String getDbPort() {
        return getEnv("DB_PORT", "");
    }

    public static String getDbName() {
        return getEnv("DB_NAME", "");
    }

    public static String getDbUser() {
        return getEnv("DB_USER", "");
    }

    public static String getDbPassword() {
        return getEnv("DB_PASSWORD", "");
    }

    public static String getDbDriver() {
        return getEnv("DB_DRIVER", "com.mysql.cj.jdbc.Driver");
    }

    public static String getHibernateDialect() {
        return getEnv("HIBERNATE_DIALECT", "org.hibernate.dialect.MySQL8Dialect");
    }

    public static boolean isHibernateShowSql() {
        return Boolean.parseBoolean(getEnv("HIBERNATE_SHOW_SQL", "false"));
    }

    public static boolean isHibernateFormatSql() {
        return Boolean.parseBoolean(getEnv("HIBERNATE_FORMAT_SQL", "false"));
    }

    public static String getHibernateHbm2ddlAuto() {
        return getEnv("HIBERNATE_HBM2DDL_AUTO", "update");
    }

    public static int getC3p0MinSize() {
        return Integer.parseInt(getEnv("C3P0_MIN_SIZE", "5"));
    }

    public static int getC3p0MaxSize() {
        return Integer.parseInt(getEnv("C3P0_MAX_SIZE", "20"));
    }

    public static int getC3p0Timeout() {
        return Integer.parseInt(getEnv("C3P0_TIMEOUT", "300"));
    }

    public static int getC3p0MaxStatements() {
        return Integer.parseInt(getEnv("C3P0_MAX_STATEMENTS", "50"));
    }

    public static int getC3p0IdleTestPeriod() {
        return Integer.parseInt(getEnv("C3P0_IDLE_TEST_PERIOD", "3000"));
    }

    public static int getJdbcBatchSize() {
        return Integer.parseInt(getEnv("HIBERNATE_JDBC_BATCH_SIZE", "20"));
    }

    public static int getJdbcFetchSize() {
        return Integer.parseInt(getEnv("HIBERNATE_JDBC_FETCH_SIZE", "50"));
    }

    public static boolean getGloballyQuotedIdentifiers() {
        return Boolean.parseBoolean(getEnv("HIBERNATE_GLOBALLY_QUOTED_IDENTIFIERS", "true"));
    }

    public static String getJdbcUrl() {
        return String.format(
                "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                getDbHost(),
                getDbPort(),
                getDbName());
    }

    private static String getEnv(String key, String defaultValue) {
        String value = DOTENV.get(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}

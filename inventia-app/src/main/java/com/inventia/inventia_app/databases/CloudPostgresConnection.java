package com.inventia.inventia_app.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * CloudPostgresConnection
 */
@Configuration
public class CloudPostgresConnection {

    private HikariDataSource connectionPool;

    public CloudPostgresConnection() {
    }

    //@Bean
    public DataSource cloudPostgresConnection() {
        System.out.println("Connectando a la base postgresql");
        HikariConfig config = new HikariConfig();
        String jdbcUrl = "jdbc:postgresql:///base-inventia";
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.postgresql.Driver");

        Properties connProps = new Properties();
        connProps.setProperty("user","services-admin@inventia-app.iam");
        connProps.setProperty("password", "password");
        connProps.setProperty("sslmode","disable");
        connProps.setProperty("socketFactory","com.google.cloud.sql.postgres.SocketFactory");
        connProps.setProperty("cloudSqlInstance","inventia-app:us-central1:base-inventia");
        connProps.setProperty("enableIamAuth","true");
        connProps.setProperty("cloudSqlTargetPrincipal","services-admin@inventia-app.iam.gserviceaccount.com");
        connProps.setProperty("ipTypes", "PUBLIC");

        config.setDataSourceProperties(connProps);
        config.setConnectionTimeout(10000); // 10s

        return new HikariDataSource(config);
    }

    public boolean pooledConnectionTest() throws SQLException{

        List<Timestamp> rows = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection()) {
            try (PreparedStatement selectStmt = conn.prepareStatement("SELECT NOW() as TS")) {
                ResultSet rs = selectStmt.executeQuery();
                while (rs.next()) {
                    rows.add(rs.getTimestamp("TS"));
                }
            }
        }
        return true;
    }
}

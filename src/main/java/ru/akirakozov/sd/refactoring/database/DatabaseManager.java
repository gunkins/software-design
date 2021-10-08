package ru.akirakozov.sd.refactoring.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final String connectionUrl;

    public DatabaseManager(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public void executeUpdate(String sql) throws SQLException {
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        }
    }

    public <T> List<T> executeQuery(String sql, RowMapper<T> rowMapper) throws SQLException, IOException {
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()
        ) {
            ResultSet rs = statement.executeQuery(sql);
            List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
            return result;
        }
    }
}

package ru.akirakozov.sd.refactoring.util;

import java.net.CacheRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ru.akirakozov.sd.refactoring.model.Product;

public class DbUtils {
    private static final String CREATE_PRODUCT = "" +
            "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    private static final String DELETE_PRODUCTS = "" +
            "DELETE FROM PRODUCT";

    public static final String DATABASE_URL = "jdbc:sqlite:test.db";

    public static void initProductsTable() throws SQLException {
        executeUpdate(CREATE_PRODUCT);
    }

    public static void clearProductsTable() throws SQLException {
        executeUpdate(DELETE_PRODUCTS);
    }

    public static void executeUpdate(String sql) throws SQLException {
        try (Statement statement = DriverManager.getConnection(DATABASE_URL).createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public static void insertProducts(List<Product> products) throws SQLException {
        for (Product product : products) {
            DbUtils.executeUpdate("" +
                    "INSERT INTO PRODUCT " +
                    "(NAME, PRICE) " +
                    "VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")"
            );
        }
    }

    public static <T> List<T> executeQuery(String sql, RowMapper<T> rowMapper) throws SQLException {
        try (
                Statement statement = DriverManager.getConnection(DATABASE_URL).createStatement();
                ResultSet rs = statement.executeQuery(sql);
        ) {
            List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
            return result;
        }
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }
}

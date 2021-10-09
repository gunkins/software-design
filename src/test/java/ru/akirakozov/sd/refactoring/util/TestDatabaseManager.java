package ru.akirakozov.sd.refactoring.util;

import java.util.List;

import ru.akirakozov.sd.refactoring.dao.model.Product;
import ru.akirakozov.sd.refactoring.database.DatabaseManager;

public class TestDatabaseManager extends DatabaseManager {
    private static final String TEST_DATABASE_URL = "jdbc:sqlite:test.db";

    private static final String CREATE_PRODUCT = "" +
            "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    private static final String DELETE_PRODUCTS = "" +
            "DELETE FROM PRODUCT";

    public TestDatabaseManager() {
        super(TEST_DATABASE_URL);
    }

    public void initProductsTable() {
        executeUpdate(CREATE_PRODUCT);
    }

    public void clearProductsTable() {
        executeUpdate(DELETE_PRODUCTS);
    }

    public void insertProducts(List<Product> products) {
        for (Product product : products) {
            executeUpdate("" +
                    "INSERT INTO PRODUCT " +
                    "(NAME, PRICE) " +
                    "VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")"
            );
        }
    }
}

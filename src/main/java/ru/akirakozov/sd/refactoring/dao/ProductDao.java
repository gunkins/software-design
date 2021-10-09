package ru.akirakozov.sd.refactoring.dao;

import java.util.List;
import java.util.Optional;

import ru.akirakozov.sd.refactoring.dao.model.Product;
import ru.akirakozov.sd.refactoring.database.DatabaseManager;
import ru.akirakozov.sd.refactoring.database.RowMapper;

public class ProductDao {
    private static final String INIT_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    private static final String INSERT_PRODUCT = "" +
            "INSERT INTO PRODUCT " +
            "(NAME, PRICE) VALUES (\"%s\", %s)";

    private static final String SELECT_ALL = "SELECT * FROM PRODUCT";
    private static final String SELECT_WITH_MAX_PRICE = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
    private static final String SELECT_WITH_MIN_PRICE = "SELECT * FROM PRODUCT ORDER BY PRICE ASC LIMIT 1";
    private static final String SELECT_PRICE_SUM = "SELECT SUM(price) FROM PRODUCT";
    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM PRODUCT";

    private final RowMapper<Product> PRODUCT_MAPPER =
            rs -> {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                return new Product(name, price);
            };

    private final DatabaseManager databaseManager;

    public ProductDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initTable() {
        databaseManager.executeUpdate(INIT_TABLE);
    }

    public void insert(Product product) {
        String sql = String.format(INSERT_PRODUCT, product.getName(), product.getPrice());
        databaseManager.executeUpdate(sql);
    }

    public List<Product> getAllProducts() {
        return databaseManager.executeQuery(SELECT_ALL, PRODUCT_MAPPER);
    }

    public Optional<Product> getProductWithMaximumPrice() {
        return databaseManager.
                executeQuery(SELECT_WITH_MAX_PRICE, PRODUCT_MAPPER)
                .stream()
                .findFirst();
    }

    public Optional<Product> getProductWithMinimumPrice() {
        return databaseManager.
                executeQuery(SELECT_WITH_MIN_PRICE, PRODUCT_MAPPER)
                .stream()
                .findFirst();
    }

    public long getProductPriceSum() {
        return databaseManager.executeQueryForObject(SELECT_PRICE_SUM, rs -> rs.getLong(1));
    }

    public int getProductCount() {
        return databaseManager.executeQueryForObject(SELECT_COUNT, rs -> rs.getInt(1));
    }
}

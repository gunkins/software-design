package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.dao.model.Product;
import ru.akirakozov.sd.refactoring.util.TestDatabaseManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddProductServletTest {
    private static final TestDatabaseManager dbManager = new TestDatabaseManager();
    private static final ProductDao productDao = new ProductDao(dbManager);
    private static final AddProductServlet addProductServlet = new AddProductServlet(productDao);

    private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    @BeforeAll
    public static void setupDb() throws SQLException {
        dbManager.initProductsTable();
        dbManager.clearProductsTable();
    }

    @BeforeEach
    public void setup() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @AfterEach
    public void clear() {
        dbManager.clearProductsTable();
    }

    @Test
    public void testAddOneProduct() throws IOException {
        Product product = new Product("milk", 80);

        when(request.getParameter(eq("name"))).thenReturn(product.getName());
        when(request.getParameter(eq("price"))).thenReturn(String.valueOf(product.getPrice()));

        addProductServlet.doGet(request, response);

        verify(request, atLeastOnce()).getParameter("name");
        verify(request, atLeastOnce()).getParameter("price");

        List<Product> products = getProducts();

        assertThat(products).containsOnly(product);
    }

    @Test
    public void testAddMultipleProducts() throws IOException {
        List<Product> products = new ArrayList<>();
        products.add(new Product("chicken", 300));
        products.add(new Product("beef", 600));
        products.add(new Product("squid", 1030));

        for (Product product : products) {
            when(request.getParameter(eq("name"))).thenReturn(product.getName());
            when(request.getParameter(eq("price"))).thenReturn(String.valueOf(product.getPrice()));

            addProductServlet.doGet(request, response);
        }

        List<Product> actualProducts = getProducts();

        assertThat(actualProducts).containsExactlyInAnyOrderElementsOf(products);
    }

    @Test
    public void testNullNumberThrowsNumberFormatException() {
        when(request.getParameter(eq("name"))).thenReturn("p");
        when(request.getParameter(eq("price"))).thenReturn(null);

        Assertions.assertThrows(
                NumberFormatException.class,
                () -> addProductServlet.doGet(request, response)
        );
    }

    private List<Product> getProducts() {
        return dbManager.executeQuery(
                "SELECT * FROM PRODUCT",
                rs -> new Product(rs.getString("name"), rs.getLong("price"))
        );
    }
}

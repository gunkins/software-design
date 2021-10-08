package ru.akirakozov.sd.refactoring.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.util.TestDatabaseManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class QueryServletTest {
    private static final TestDatabaseManager dbManager = new TestDatabaseManager();
    private static final QueryServlet queryServlet = new QueryServlet(dbManager);

    private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    private StringWriter stringWriter;

    private final List<Product> products = Arrays.asList(
            new Product("laptop", 1000),
            new Product("table", 500),
            new Product("chair", 10)
    );

    @BeforeAll
    public static void setupDb() throws SQLException {
        dbManager.initProductsTable();
        dbManager.clearProductsTable();
    }

    @BeforeEach
    public void setup() throws Exception {
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @AfterEach
    public void clear() throws SQLException {
        dbManager.clearProductsTable();
    }

    @Test
    public void testMaxQueryEmptyTable() throws Exception {
        testCommandOnData("max", Collections.emptyList(), "<h1>Product with max price: </h1>");
    }

    @Test
    public void testMaxQueryOnMultipleProducts() throws Exception {
        testCommandOnData("max", products, "<h1>Product with max price: </h1>laptop\t1000</br>");
    }

    @Test
    public void testMinQueryEmptyTable() throws Exception {
        testCommandOnData("min", Collections.emptyList(), "<h1>Product with min price: </h1>");
    }

    @Test
    public void testMinQueryOnMultipleProducts() throws Exception {
        testCommandOnData("min", products, "<h1>Product with min price: </h1>chair\t10</br>");
    }

    @Test
    public void testSumQueryEmptyTable() throws Exception {
        testCommandOnData("sum", Collections.emptyList(), "Summary price: 0");
    }

    @Test
    public void testSumQueryOnMultiplyProducts() throws Exception {
        testCommandOnData("sum", products, "Summary price: 1510");
    }

    @Test
    public void testCountQueryEmptyTable() throws Exception {
        testCommandOnData("count", Collections.emptyList(), "Number of products: 0");
    }

    @Test
    public void testCountQueryOnMultiplyProducts() throws Exception {
        testCommandOnData("count", products, "Number of products: 3");
    }

    @Test
    public void testUnknownCommand() throws Exception {
        mockCommand("some-command");
        queryServlet.doGet(request, response);

        String responseString = stringWriter.getBuffer().toString();

        assertThat(responseString).isEqualToIgnoringNewLines("Unknown command: some-command");
    }

    private void testCommandOnData(String command, List<Product> data, String expectedResponseBody) throws Exception {
        mockCommand(command);
        dbManager.insertProducts(data);
        queryServlet.doGet(request, response);

        String responseString = stringWriter.getBuffer().toString();

        assertThat(responseString)
                .isEqualToIgnoringNewLines(toHtmlResponse(expectedResponseBody));
    }

    private void mockCommand(String command) {
        when(request.getParameter(eq("command"))).thenReturn(command);
    }

    private String toHtmlResponse(String responseBody) {
        return "<html><body>" + responseBody + "</body></html>";
    }
}

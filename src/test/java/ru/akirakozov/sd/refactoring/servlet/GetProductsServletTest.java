package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.dao.model.Product;
import ru.akirakozov.sd.refactoring.util.TestDatabaseManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GetProductsServletTest {
    private static final TestDatabaseManager dbManager = new TestDatabaseManager();
    private static final ProductDao productDao = new ProductDao(dbManager);
    private static final GetProductsServlet getProductServlet = new GetProductsServlet(productDao);

    private static final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private static final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    private StringWriter stringWriter;

    @BeforeAll
    public static void setupDb() {
        dbManager.initProductsTable();
        dbManager.clearProductsTable();
    }

    @BeforeEach
    public void setup() throws IOException {
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @AfterEach
    public void clear() {
        dbManager.clearProductsTable();
    }

    @Test
    public void testEmptyResponse() throws IOException {
        getProductServlet.doGet(request, response);

        String responseString = stringWriter.getBuffer().toString();
        String expectedString = "" +
                "<html><body>\n" +
                "</body></html>\n";

        assertThat(responseString).isEqualTo(expectedString);
    }

    @Test
    public void testResponseWithMultipleProducts() throws IOException {
        List<Product> products = Arrays.asList(
                new Product("laptop", 1000),
                new Product("table", 500),
                new Product("chair", 10)
        );

        dbManager.insertProducts(products);

        getProductServlet.doGet(request, response);

        String responseString = stringWriter.getBuffer().toString();
        List<String> lines = Arrays.stream(responseString.split("\n")).collect(Collectors.toList());
        List<String> expectedProductLines = products.stream()
                .map(p -> String.format("%s\t%s</br>", p.getName(), p.getPrice()))
                .collect(Collectors.toList());

        assertThat(lines).hasSize(5);
        assertThat(lines.get(0)).isEqualTo("<html><body>");
        assertThat(lines.get(4)).isEqualTo("</body></html>");

        assertThat(lines.subList(1, 4))
                .containsExactlyInAnyOrderElementsOf(expectedProductLines);
    }
}

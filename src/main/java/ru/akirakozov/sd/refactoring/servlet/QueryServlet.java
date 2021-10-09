package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.br;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.h1;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.htmlBody;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.ifPresent;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.join;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.string;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    private final ProductDao productDao;

    public QueryServlet(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        PrintWriter responseWriter = response.getWriter();

        if ("max".equals(command)) {
            htmlBody(
                    h1("Product with max price: "),
                    ifPresent(productDao.getProductWithMaximumPrice(), product ->
                            join(
                                    string(product.getName() + "\t" + product.getPrice()),
                                    br()
                            )
                    )
            ).render(responseWriter);
        } else if ("min".equals(command)) {
            htmlBody(
                    h1("Product with min price: "),
                    ifPresent(productDao.getProductWithMinimumPrice(), product ->
                            join(
                                    string(product.getName() + "\t" + product.getPrice()),
                                    br()
                            )
                    )
            ).render(responseWriter);
        } else if ("sum".equals(command)) {
            htmlBody(string("Summary price: " + productDao.getProductPriceSum())).render(responseWriter);
        } else if ("count".equals(command)) {
            htmlBody(string("Number of products: " + productDao.getProductCount())).render(responseWriter);
        } else {
            responseWriter.println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}

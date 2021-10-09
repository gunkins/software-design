package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

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
            responseWriter.println("<html><body>");
            responseWriter.println("<h1>Product with max price: </h1>");

            productDao.getProductWithMaximumPrice()
                    .ifPresent(product ->
                            responseWriter.println(product.getName() + "\t" + product.getPrice() + "</br>")
                    );

            responseWriter.println("</body></html>");
        } else if ("min".equals(command)) {
            responseWriter.println("<html><body>");
            responseWriter.println("<h1>Product with min price: </h1>");

            productDao.getProductWithMinimumPrice()
                    .ifPresent(product ->
                            responseWriter.println(product.getName() + "\t" + product.getPrice() + "</br>")
                    );

            responseWriter.println("</body></html>");
        } else if ("sum".equals(command)) {
            responseWriter.println("<html><body>");

            responseWriter.println("Summary price: ");
            responseWriter.println(productDao.getProductPriceSum());

            responseWriter.println("</body></html>");
        } else if ("count".equals(command)) {
            responseWriter.println("<html><body>");

            responseWriter.println("Number of products: ");
            responseWriter.println(productDao.getProductCount());

            responseWriter.println("</body></html>");
        } else {
            responseWriter.println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}

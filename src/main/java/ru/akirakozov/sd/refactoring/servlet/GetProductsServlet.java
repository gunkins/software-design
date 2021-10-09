package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.dao.model.Product;

import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.br;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.each;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.htmlBody;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.join;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.string;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {
    private final ProductDao productDao;

    public GetProductsServlet(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productDao.getAllProducts();

        htmlBody(
                each(products, product ->
                        join(
                                string(product.getName() + "\t" + product.getPrice()),
                                br()
                        )
                )
        ).render(response.getWriter());

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

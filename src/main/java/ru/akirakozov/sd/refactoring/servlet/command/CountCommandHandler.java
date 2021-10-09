package ru.akirakozov.sd.refactoring.servlet.command;

import java.io.PrintWriter;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.htmlBody;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.string;

public class CountCommandHandler implements CommandHandler {
    private final ProductDao productDao;

    public CountCommandHandler(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void writeResult(PrintWriter writer) {
        htmlBody(
                string("Number of products: " + productDao.getProductCount())
        ).render(writer);
    }
}

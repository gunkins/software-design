package ru.akirakozov.sd.refactoring.servlet.command;

import java.io.PrintWriter;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.br;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.h1;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.htmlBody;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.ifPresent;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.join;
import static ru.akirakozov.sd.refactoring.html.HtmlBuilder.string;

public class MinCommandHandler implements CommandHandler {
    private final ProductDao productDao;

    public MinCommandHandler(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void writeResult(PrintWriter writer) {
        htmlBody(
                h1("Product with min price: "),
                ifPresent(productDao.getProductWithMinimumPrice(), product ->
                        join(
                                string(product.getName() + "\t" + product.getPrice()),
                                br()
                        )
                )
        ).render(writer);
    }
}

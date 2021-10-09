package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.servlet.command.CommandFactory;
import ru.akirakozov.sd.refactoring.servlet.command.CommandHandler;

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

        CommandHandler commandHandler = CommandFactory.getCommandHandler(command, productDao);
        commandHandler.writeResult(response.getWriter());

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}

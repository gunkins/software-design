package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.database.DatabaseManager;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

/**
 * @author akirakozov
 */
public class Main {
    private static final String INIT_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    public static void main(String[] args) throws Exception {
        DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:sqlite.db");
        databaseManager.executeUpdate(INIT_TABLE);

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(databaseManager)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(databaseManager)), "/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(databaseManager)), "/query");

        server.start();
        server.join();
    }
}

package ru.akirakozov.sd.refactoring.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.database.DatabaseManager;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {
    private final DatabaseManager databaseManager;

    public GetProductsServlet(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().println("<html><body>");

            databaseManager.executeQuery("SELECT * FROM PRODUCT", rs -> {
                String name = rs.getString("name");
                int price = rs.getInt("price");
                response.getWriter().println(name + "\t" + price + "</br>");
                return null;
            });

            response.getWriter().println("</body></html>");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

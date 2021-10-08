package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.database.DatabaseManager;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    private final DatabaseManager databaseManager;

    public QueryServlet(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            try {
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Product with max price: </h1>");

                databaseManager.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1", (rs) -> {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    response.getWriter().println(name + "\t" + price + "</br>");
                    return null;
                });

                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("min".equals(command)) {
            try {
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Product with min price: </h1>");

                databaseManager.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", rs -> {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    response.getWriter().println(name + "\t" + price + "</br>");
                    return null;
                });

                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("sum".equals(command)) {
            try {
                response.getWriter().println("<html><body>");
                response.getWriter().println("Summary price: ");

                databaseManager.executeQuery("SELECT SUM(price) FROM PRODUCT", rs -> {
                    response.getWriter().println(rs.getInt(1));
                    return null;
                });

                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("count".equals(command)) {
            try {
                response.getWriter().println("<html><body>");
                response.getWriter().println("Number of products: ");

                databaseManager.executeQuery("SELECT COUNT(*) FROM PRODUCT", rs -> {
                    response.getWriter().println(rs.getInt(1));
                    return null;
                });

                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}

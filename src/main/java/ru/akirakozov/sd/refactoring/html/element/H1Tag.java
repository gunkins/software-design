package ru.akirakozov.sd.refactoring.html.element;

import java.io.PrintWriter;

public class H1Tag extends HtmlElement {
    private final String content;

    public H1Tag(String content) {
        super();
        this.content = content;
    }

    @Override
    public void render(PrintWriter printWriter) {
        printWriter.println("<h1>" + content + "</h1>");
    }
}

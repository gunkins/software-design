package ru.akirakozov.sd.refactoring.html.element;

import java.io.PrintWriter;

public class BrTag extends HtmlElement {
    public BrTag() {
    }

    @Override
    public void render(PrintWriter printWriter) {
        printWriter.println("</br>");
    }
}

package ru.akirakozov.sd.refactoring.html.element;

import java.io.PrintWriter;

public class HtmlString extends HtmlElement {
    private final String content;

    public HtmlString(String value) {
        super();
        this.content = value;
    }

    @Override
    public void render(PrintWriter writer) {
        writer.print(content);
    }
}

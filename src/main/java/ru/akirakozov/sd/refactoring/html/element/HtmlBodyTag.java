package ru.akirakozov.sd.refactoring.html.element;

import java.io.PrintWriter;
import java.util.Arrays;

public class HtmlBodyTag extends HtmlContainer {

    public HtmlBodyTag(HtmlElement... htmlElements) {
        super();
        children.addAll(Arrays.asList(htmlElements));
    }

    @Override
    public void render(PrintWriter writer) {
        writer.println("<html><body>");
        super.render(writer);
        writer.println("</body></html>");
    }
}

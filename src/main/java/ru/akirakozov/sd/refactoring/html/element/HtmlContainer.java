package ru.akirakozov.sd.refactoring.html.element;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HtmlContainer extends HtmlElement {
    protected List<HtmlElement> children;

    public HtmlContainer() {
        super();
        this.children = new ArrayList<>();
    }

    public HtmlContainer addChildren(Collection<HtmlElement> elements) {
        children.addAll(elements);
        return this;
    }

    @Override
    public void render(PrintWriter writer) {
        for (HtmlElement child : children) {
            child.render(writer);
        }
    }
}

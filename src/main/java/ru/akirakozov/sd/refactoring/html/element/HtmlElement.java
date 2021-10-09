package ru.akirakozov.sd.refactoring.html.element;

import java.io.PrintWriter;

public abstract class HtmlElement {
    public abstract void render(PrintWriter printWriter);
}

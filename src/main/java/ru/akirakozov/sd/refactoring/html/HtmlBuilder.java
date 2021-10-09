package ru.akirakozov.sd.refactoring.html;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.akirakozov.sd.refactoring.html.element.H1Tag;
import ru.akirakozov.sd.refactoring.html.element.HtmlElement;
import ru.akirakozov.sd.refactoring.html.element.HtmlString;
import ru.akirakozov.sd.refactoring.html.element.HtmlBodyTag;
import ru.akirakozov.sd.refactoring.html.element.BrTag;
import ru.akirakozov.sd.refactoring.html.element.HtmlContainer;

public class HtmlBuilder {
    public static HtmlBodyTag htmlBody(HtmlElement... htmlElements) {
        return new HtmlBodyTag(htmlElements);
    }

    public static HtmlString string(String value) {
        return new HtmlString(value);
    }

    public static BrTag br() {
        return new BrTag();
    }

    public static H1Tag h1(String content) {
        return new H1Tag(content);
    }

    public static <T> HtmlContainer each(Collection<T> elements, Function<T, HtmlElement> mapper) {
        return new HtmlContainer()
                .addChildren(
                        elements.stream()
                                .map(mapper)
                                .collect(Collectors.toList())
                );
    }

    public static HtmlContainer join(HtmlElement... htmlElements) {
        return new HtmlContainer().addChildren(Arrays.asList(htmlElements));
    }

    public static <T> HtmlElement ifPresent(Optional<T> optional, Function<T, HtmlElement> mapper) {
        return optional.map(mapper).orElse(new HtmlContainer());
    }
}

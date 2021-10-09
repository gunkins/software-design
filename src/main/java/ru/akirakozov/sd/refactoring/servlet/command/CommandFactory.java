package ru.akirakozov.sd.refactoring.servlet.command;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

public class CommandFactory {
    public static CommandHandler getCommandHandler(String command, ProductDao productDao) {
        if ("max".equals(command)) {
            return new MaxCommandHandler(productDao);
        } else if ("min".equals(command)) {
            return new MinCommandHandler(productDao);
        } else if ("sum".equals(command)) {
            return new SumCommandHandler(productDao);
        } else if ("count".equals(command)) {
            return new CountCommandHandler(productDao);
        } else {
            return new UnknownCommandHandler(command);
        }
    }
}

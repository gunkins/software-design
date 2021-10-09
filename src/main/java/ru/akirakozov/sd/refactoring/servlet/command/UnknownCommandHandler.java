package ru.akirakozov.sd.refactoring.servlet.command;

import java.io.PrintWriter;

public class UnknownCommandHandler implements CommandHandler {
    private final String unknownCommand;

    public UnknownCommandHandler(String unknownCommand) {
        this.unknownCommand = unknownCommand;
    }

    @Override
    public void writeResult(PrintWriter writer) {
        writer.println("Unknown command: " + unknownCommand);
    }
}

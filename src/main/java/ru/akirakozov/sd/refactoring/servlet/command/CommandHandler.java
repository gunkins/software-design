package ru.akirakozov.sd.refactoring.servlet.command;

import java.io.PrintWriter;

public interface CommandHandler {
    void writeResult(PrintWriter writer);
}

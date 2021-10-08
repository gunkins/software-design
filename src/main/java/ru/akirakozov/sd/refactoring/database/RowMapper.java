package ru.akirakozov.sd.refactoring.database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException, IOException;
}

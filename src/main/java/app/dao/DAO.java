package app.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<A> {
    List<A> getAll() throws SQLException;

    Optional<A> getById(int id) throws SQLException;

    void add(A a) throws SQLException;

    void update(A a) throws SQLException;

    void delete(int id) throws SQLException;
}
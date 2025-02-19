package Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface IService <T>{
    public void add(T t) throws SQLException;
    public void update(T t) throws SQLException;
    public void delete(T t) throws SQLException;
    public List<T> getAll() throws SQLException;
    public T getById(int id) throws SQLException;
}

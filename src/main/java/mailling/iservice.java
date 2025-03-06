package mailling;

import java.sql.SQLException;
import java.util.List;

public interface iservice <T> {
    public void ajouter(T p) throws SQLException;
    public void modifier(T p) throws SQLException;
    public void supprimer(int id) throws SQLException;
    public T getOneById(int id) throws SQLException;
    //public Set<T> getAll() throws SQLException;
    public List<T> getAll() throws SQLException;
}
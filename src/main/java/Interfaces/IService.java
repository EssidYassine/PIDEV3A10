package Interfaces;

import Models.Pack;

import java.util.List;
import java.util.Set;

public interface IService <T>{
    public void ajouter (T p);
    public void modifier (T p);
    public void supprimer(int id);
    public T getOneById(int id);
    public List<Pack> getAll();
}

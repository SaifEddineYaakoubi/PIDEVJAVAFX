package org.example.pidev.interfaces;

import java.util.List;

public interface IService<T> {

    boolean add(T t);

    void update(T t);

    boolean delete(int id);

    T getById(int id);

    List<T> getAll();
}

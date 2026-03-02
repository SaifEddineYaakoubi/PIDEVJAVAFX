package org.example.pidev.interfaces;

import java.util.List;

public interface IService<T> {

    boolean add(T t);

    void update(T t);

    default boolean delete(int id) {
        throw new UnsupportedOperationException("delete not implemented");
    }

    T getById(int id);

    List<T> getAll();
}

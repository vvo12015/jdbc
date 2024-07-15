package net.vrakin.dao;

import java.util.List;

public interface Dao<T> {
    T save(T t);
    T getById(Long id);

    List<T> getAll();
    void delete(Long id);
}

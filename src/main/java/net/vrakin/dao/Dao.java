package net.vrakin.dao;

import net.vrakin.dto.Apartment;

import java.util.List;

public interface Dao<T> {
    T add(T t);

    Apartment update(Apartment apartment);

    T getById(Long id);

    List<T> getAll();
    void delete(Long id);
}

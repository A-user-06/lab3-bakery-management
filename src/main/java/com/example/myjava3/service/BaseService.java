package com.example.myjava3.service;

import com.example.myjava3.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;

public abstract class BaseService<T, ID extends Serializable> {

    protected abstract JpaRepository<T, ID> getRepository();

    public List<T> findAll() {
        return getRepository().findAll();
    }

    public T findById(ID id) {
        return getRepository().findById(id)
                .orElseThrow(() -> new NotFoundException("Entity not found with id: " + id));
    }

    @Transactional
    public T create(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public T update(ID id, T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public void delete(ID id) {
        getRepository().deleteById(id);
    }
}
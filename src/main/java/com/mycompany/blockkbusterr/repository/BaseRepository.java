package com.mycompany.blockkbusterr.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class BaseRepository<T, ID extends Serializable> {
    
    @PersistenceContext(unitName = "blockkbusterr_pu")
    protected EntityManager entityManager;
    
    private final Class<T> entityClass;
    
    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Persist a new entity
     */
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }
    
    /**
     * Update an existing entity
     */
    public T update(T entity) {
        return entityManager.merge(entity);
    }
    
    /**
     * Save or update entity
     */
    public T saveOrUpdate(T entity) {
        if (getId(entity) == null) {
            return save(entity);
        } else {
            return update(entity);
        }
    }
    
    /**
     * Find entity by ID
     */
    public Optional<T> findById(ID id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }
    
    /**
     * Find all entities
     */
    public List<T> findAll() {
        String queryName = entityClass.getSimpleName() + ".findAll";
        TypedQuery<T> query = entityManager.createNamedQuery(queryName, entityClass);
        return query.getResultList();
    }
    
    /**
     * Delete entity by ID
     */
    public boolean deleteById(ID id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
            return true;
        }
        return false;
    }
    
    /**
     * Delete entity
     */
    public void delete(T entity) {
        if (entityManager.contains(entity)) {
            entityManager.remove(entity);
        } else {
            entityManager.remove(entityManager.merge(entity));
        }
    }
    
    /**
     * Count all entities
     */
    public long count() {
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }
    
    /**
     * Check if entity exists by ID
     */
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
    
    /**
     * Execute named query
     */
    protected TypedQuery<T> createNamedQuery(String queryName) {
        return entityManager.createNamedQuery(queryName, entityClass);
    }
    
    /**
     * Execute JPQL query
     */
    protected TypedQuery<T> createQuery(String jpql) {
        return entityManager.createQuery(jpql, entityClass);
    }
    
    /**
     * Get entity manager
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    /**
     * Flush pending changes
     */
    public void flush() {
        entityManager.flush();
    }
    
    /**
     * Refresh entity from database
     */
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }
    
    /**
     * Detach entity from persistence context
     */
    public void detach(T entity) {
        entityManager.detach(entity);
    }
    
    /**
     * Abstract method to get entity ID
     */
    protected abstract ID getId(T entity);
}
package com.ayush.ayush.repository;

import com.ayush.ayush.model.Seller;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SellerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Seller seller){
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(seller);
            transaction.commit();
        }catch (Exception e){
            if (transaction.isActive())
                transaction.rollback();
            throw e;
        }
    }

    public Optional<Seller> getById(int id){
        EntityTransaction transaction = entityManager.getTransaction();
        Seller seller = null;
        try {
            transaction.begin();
            seller = entityManager.find(Seller.class,id);
            transaction.commit();
        }catch (Exception e){
            if (transaction.isActive())
                transaction.rollback();
            throw e;
        }
        return Optional.ofNullable(seller);
    }
    public List<Seller> getAllSellers(){
        EntityTransaction transaction = entityManager.getTransaction();
        List<Seller> list = new ArrayList<>();
        try {
            String jpql = "SELECT s FROM Seller s";
            TypedQuery<Seller> query = entityManager.createQuery(jpql, Seller.class);
            transaction.begin();
            list = query.getResultList();
            transaction.commit();
        }catch (Exception e){
            if (transaction.isActive())
                transaction.rollback();
            throw e;
        }
        return list;
    }

    public List<Seller> getSellers(int page,int pageSize){
        if(page<0){
            throw new IllegalArgumentException("Page must be greater than or equal to zero");
        }
        EntityTransaction transaction = entityManager.getTransaction();
        List<Seller> list = new ArrayList<>();
        try {
            String jpql = "SELECT s FROM Seller s";
            TypedQuery<Seller> query = entityManager.createQuery(jpql, Seller.class);
            int firstResult = page * pageSize;
            query.setFirstResult(firstResult);
            query.setMaxResults(pageSize);
            transaction.begin();
            list = query.getResultList();
            transaction.commit();
        }catch (Exception e){
            if (transaction.isActive())
                transaction.rollback();
            throw e;
        }
        return list;
    }
    public void update(Seller seller){
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(seller);
            transaction.commit();
        }catch (Exception e){
            if (transaction.isActive())
                transaction.rollback();
            throw e;
        }
    }
    public void deleteById(int id){
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            String jpql = "DELETE FROM Seller s where s.id = :id";
            TypedQuery<Seller> query = entityManager.createQuery(jpql, Seller.class).setParameter("id",id);
            transaction.begin();
            query.executeUpdate();
            transaction.commit();
        }catch (Exception e){
            if (transaction.isActive())
                transaction.rollback();
            throw e;
        }
    }

}

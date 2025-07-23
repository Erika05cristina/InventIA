package com.inventia.inventia_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventia.inventia_app.entities.ProductRecord;

/**
 * RecordRepository
 */
@Repository
public interface RecordRepository extends JpaRepository<ProductRecord, Integer> {

    List<ProductRecord> findByProductId(Integer productId);
    List<ProductRecord> findByDt(String dt);

}

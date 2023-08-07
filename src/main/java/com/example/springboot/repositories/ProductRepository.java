package com.example.springboot.repositories;

import com.example.springboot.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

//ProductModel is the path from Model; @Repo is redundant because it has JpaRepo...
@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {


}

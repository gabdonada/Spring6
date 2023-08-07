package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@RestCon instead of @Controller due rest...
@RestController
public class ProductController {

    //Instead of constructor
    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products/register")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        try {
            ProductModel productModel = new ProductModel();
            BeanUtils.copyProperties(productRecordDto, productModel);
            //Returning status and also the info that was saved (which is optional)
            return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
        } catch (Exception e) {
            throw new ExceptionHandlerController("Failed to save product: " + e.getMessage());
        }
    }

    @GetMapping("/products/listAll")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        List<ProductModel> productModelList = productRepository.findAll();
        if(!productModelList.isEmpty()){
            for(ProductModel product : productModelList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModelList);
    }

    @GetMapping("/products/get/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> productReceived = productRepository.findById(id);
        if(productReceived.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productReceived.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(productReceived.get());
    }

    @PutMapping("/products/update/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductRecordDto productRecordDto){
        try {
            Optional<ProductModel> productReceived = productRepository.findById(id);
            if (productReceived.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
            ProductModel prodToUpdate = productReceived.get();
            BeanUtils.copyProperties(productRecordDto, prodToUpdate);

            return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(prodToUpdate));
        } catch (Exception e){
            throw new ExceptionHandlerController("Failed to update product: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<Object> deleteAProduct(@PathVariable(value = "id") UUID id){
        try {
            Optional<ProductModel> productReceived = productRepository.findById(id);
            if (productReceived.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }
            productRepository.delete(productReceived.get());
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
        } catch (Exception e){
            throw new ExceptionHandlerController("Failed to delete product: " + e.getMessage());
        }
    }

}

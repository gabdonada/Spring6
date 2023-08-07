package com.example.springboot.controllers;

public class ExceptionHandlerController extends RuntimeException {
    public ExceptionHandlerController(String message){
        super(message);
    }
}

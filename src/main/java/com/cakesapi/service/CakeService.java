package com.cakesapi.service;

import com.cakesapi.model.Cake;

import java.util.List;

public interface CakeService {

    List<Cake> getAllCakes();

    Cake getCakeById(Long id);

    Cake createCake(Cake cake);

    List<Cake> createCakes(List<Cake> cakes);

    Cake updateCake(Long id, Cake cake);

    void deleteCake(Long id);
}

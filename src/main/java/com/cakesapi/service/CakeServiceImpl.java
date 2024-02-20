package com.cakesapi.service;

import com.cakesapi.exception.CakeNotFoundException;
import com.cakesapi.model.Cake;
import com.cakesapi.repository.CakeRepository;
import com.cakesapi.validation.CakeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CakeServiceImpl implements CakeService {

    @Autowired
    private CakeRepository cakeRepository;

    public List<Cake> getAllCakes() {
        log.info("Get All Cakes from database");
        return cakeRepository.findAll();
    }

    public Cake getCakeById(Long id) {
        log.info("Get Cake Details for Id: " + id);
        return cakeRepository.findById(id)
                .orElseThrow(() -> new CakeNotFoundException("Cake not found with id " + id));
    }

    public Cake createCake(Cake cake) {
        log.info("Adding new cake: " + cake.getFlavour());
        CakeValidator.validateCake(cake);
        return cakeRepository.save(cake);
    }

    @Override
    public List<Cake> createCakes(List<Cake> cakes) {
        return cakeRepository.saveAll(cakes);
    }

    public Cake updateCake(Long id, Cake cake) {
        log.info("Updating Cake Id: " + id);
        CakeValidator.validateCake(cake);
        Cake existingCake = getCakeById(id);
        existingCake.setFlavour(cake.getFlavour());
        existingCake.setIcing(cake.getIcing());
        existingCake.setImage(cake.getImage());
        return cakeRepository.save(existingCake);
    }

    public void deleteCake(Long id) {
        log.info("Deleting Cake Id: " + id);
        cakeRepository.delete(getCakeById(id));
    }
}

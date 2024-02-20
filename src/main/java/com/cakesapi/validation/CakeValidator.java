package com.cakesapi.validation;

import com.cakesapi.model.Cake;
import org.springframework.util.Assert;

public class CakeValidator {

    public static void validateCake(Cake cake) {
        Assert.hasText(cake.getFlavour(), "Flavor cannot be empty or null");
        Assert.hasText(cake.getIcing(), "Icing cannot be empty or null");
    }
}

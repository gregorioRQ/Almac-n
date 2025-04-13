package com.pola.api_inventario.validacion;

import com.pola.api_inventario.rest.models.Item;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ItemCantidadValidator implements ConstraintValidator<ValidItemCantidad, Item> {

    @Override
    public boolean isValid(Item item, ConstraintValidatorContext context) {
        return (item.getCantidadLotes() != null && item.getCantidadLotes() > 0) ||
                (item.getPesoPorUnidad() > 0) ||
                (item.getVolumenPorUnidad() > 0) ||
                (item.getCantidadLotes() != null && item.getCantidadLotes() > 0) ||
                (item.getLongitudPorUnidad() > 0) ||
                (item.getUnidadesPorLote() > 0);
    }

}

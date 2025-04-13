package com.pola.api_inventario.validacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ItemCantidadValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidItemCantidad {
    String message() default "Debe especificar al menos un campo de cantidad válido (cantidad, pesoTotal, volumenTotal, etc.)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

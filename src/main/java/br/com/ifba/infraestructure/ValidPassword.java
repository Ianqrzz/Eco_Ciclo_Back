package br.com.ifba.infraestructure;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target( { java.lang.annotation.ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Senha deve conter entre 8 e 20 caracteres, com pelo menos um caracter maiusculo, um minusculo e um carater especial";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

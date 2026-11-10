package br.com.ifba.infraestructure;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String Password_Pattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Let @NotBlank or @NotNull handle null/empty validation on the DTO
        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        return password.matches(Password_Pattern);
    }
}

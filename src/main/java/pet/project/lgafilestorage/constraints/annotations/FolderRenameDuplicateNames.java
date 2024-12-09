package pet.project.lgafilestorage.constraints.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pet.project.lgafilestorage.constraints.validators.FolderDuplicateNamesValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FolderDuplicateNamesValidator.class)
public @interface FolderRenameDuplicateNames {

    String message() default "New name should differ from the old name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

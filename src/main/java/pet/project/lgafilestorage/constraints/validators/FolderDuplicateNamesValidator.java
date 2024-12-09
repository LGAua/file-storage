package pet.project.lgafilestorage.constraints.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pet.project.lgafilestorage.constraints.annotations.FolderRenameDuplicateNames;
import pet.project.lgafilestorage.model.dto.folder.FolderRenameRequestDto;

public class FolderDuplicateNamesValidator implements ConstraintValidator<FolderRenameDuplicateNames, FolderRenameRequestDto> {

    @Override
    public boolean isValid(FolderRenameRequestDto value, ConstraintValidatorContext context) {
        String oldName = value.getFolderName();
        String newName = value.getFolderNewName();

        return !(oldName.isBlank() || newName.isBlank()) &&
                (!oldName.equals(newName));
    }
}

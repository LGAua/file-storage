package pet.project.lgafilestorage.constraints.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pet.project.lgafilestorage.constraints.annotations.FileRenameDuplicateNames;
import pet.project.lgafilestorage.constraints.annotations.FolderRenameDuplicateNames;
import pet.project.lgafilestorage.model.dto.file.FileRenameDto;
import pet.project.lgafilestorage.model.dto.folder.FolderRenameRequestDto;

public class FileDuplicateNamesValidator implements ConstraintValidator<FileRenameDuplicateNames, FileRenameDto> {

    @Override
    public boolean isValid(FileRenameDto value, ConstraintValidatorContext context) {
        String oldName = value.getFileName();
        oldName = oldName.substring(0, oldName.lastIndexOf("."));

        String newName = value.getFileNewName();
        return !(oldName.isBlank() || newName.isBlank()) &&
                (!oldName.equals(newName));
    }
}

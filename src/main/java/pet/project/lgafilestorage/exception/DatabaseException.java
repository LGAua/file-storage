package pet.project.lgafilestorage.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DatabaseException extends DataIntegrityViolationException {
    public DatabaseException(String message) {
        super(message);
    }
}

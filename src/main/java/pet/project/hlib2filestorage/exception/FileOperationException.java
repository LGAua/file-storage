package pet.project.hlib2filestorage.exception;

public class FileOperationException extends RuntimeException {
    public FileOperationException(String message,Throwable cause) {
        super(message,cause);
    }
    public FileOperationException(String message) {
        super(message);
    }
}

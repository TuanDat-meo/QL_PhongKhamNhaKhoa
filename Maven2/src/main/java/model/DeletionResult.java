package model;

public class DeletionResult {
    private boolean success;
    private String message;
    private int errorCode;

    public DeletionResult(boolean success, String message, int errorCode) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}

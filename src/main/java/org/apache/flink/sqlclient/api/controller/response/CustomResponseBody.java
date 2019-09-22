package org.apache.flink.sqlclient.api.controller.response;

public class CustomResponseBody {

    private String message;
    private String internalCode;

    public CustomResponseBody(String message) {
        this.message = message;
    }

    public CustomResponseBody(String message, String internalCode) {
        this.message = message;
        this.internalCode = internalCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }



}

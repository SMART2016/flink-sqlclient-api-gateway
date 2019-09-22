package org.apache.flink.sqlclient.api.controller.executor.exception;

/**
 * Exception thrown during the execution of SQL statements.
 */
public class SqlExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SqlExecutionException(String message) {
        super(message);
    }

    public SqlExecutionException(String message, Throwable e) {
        super(message, e);
    }
}

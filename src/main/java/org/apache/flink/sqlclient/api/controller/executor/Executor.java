package org.apache.flink.sqlclient.api.controller.executor;

import org.apache.flink.sqlclient.api.controller.executor.exception.SqlExecutionException;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.client.gateway.ProgramTargetDescriptor;
import org.apache.flink.table.client.gateway.ResultDescriptor;
import org.apache.flink.table.client.gateway.TypedResult;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;

/**
 * A gateway for communicating with Flink and other external systems.
 */
public interface Executor {

    /**
     * Starts the executor and ensures that its is ready for commands to be executed.
     */
    void start() throws SqlExecutionException;

    /**
     * Lists all session properties that are defined by the executor and the session.
     */
    Map<String, String> getSessionProperties(SessionContext session) throws SqlExecutionException;

    /**
     * Lists all registered catalogs.
     */
    List<String> listCatalogs(SessionContext session) throws SqlExecutionException;

    /**
     * Lists all databases in the current catalog.
     */
    List<String> listDatabases(SessionContext session) throws SqlExecutionException;

    /**
     * Lists all tables in the current database of the current catalog.
     */
    List<String> listTables(SessionContext session) throws SqlExecutionException;

    /**
     * Lists all user-defined functions known to the executor.
     */
    List<String> listUserDefinedFunctions(SessionContext session) throws SqlExecutionException;

    /**
     * Lists all functions known to the executor.
     */
    List<String> listFunctions(SessionContext session) throws SqlExecutionException;

    /**
     * Sets a catalog with given name as the current catalog.
     */
    void useCatalog(SessionContext session, String catalogName) throws SqlExecutionException;

    /**
     * Sets a database with given name as the current database of the current catalog.
     */
    void useDatabase(SessionContext session, String databaseName) throws SqlExecutionException;

    /**
     * Returns the schema of a table. Throws an exception if the table could not be found. The
     * schema might contain time attribute types for helping the user during debugging a query.
     */
    TableSchema getTableSchema(SessionContext session, String name) throws SqlExecutionException;

    /**
     * Returns a string-based explanation about AST and execution plan of the given statement.
     */
    String explainStatement(SessionContext session, String statement) throws SqlExecutionException;

    /**
     * Returns a list of completion hints for the given statement at the given position.
     */
    List<String> completeStatement(SessionContext session, String statement, int position);

    /**
     * Submits a Flink SQL query job (detached) and returns the result descriptor.
     */
    ResultDescriptor executeQuery(SessionContext session, String query) throws SqlExecutionException;

    /**
     * Asks for the next changelog results (non-blocking).
     */
    TypedResult<List<Tuple2<Boolean, Row>>> retrieveResultChanges(SessionContext session, String resultId) throws SqlExecutionException;

    /**
     * Creates an immutable result snapshot of the running Flink job. Throws an exception if no Flink job can be found.
     * Returns the number of pages.
     */
    TypedResult<Integer> snapshotResult(SessionContext session, String resultId, int pageSize) throws SqlExecutionException;

    /**
     * Returns the rows that are part of the current page or throws an exception if the snapshot has been expired.
     */
    List<Row> retrieveResultPage(String resultId, int page) throws SqlExecutionException;

    /**
     * Cancels a table program and stops the result retrieval. Blocking until cancellation command has
     * been sent to cluster.
     */
    void cancelQuery(SessionContext session, String resultId) throws SqlExecutionException;

    /**
     * Submits a Flink SQL update statement such as INSERT INTO.
     *
     * @param session context in with the statement is executed
     * @param statement SQL update statement (currently only INSERT INTO is supported)
     * @return information about the target of the submitted Flink job
     */
    ProgramTargetDescriptor executeUpdate(SessionContext session, String statement) throws SqlExecutionException;

    /**
     * Validates the current session. For example, it checks whether all views are still valid.
     */
    void validateSession(SessionContext session) throws SqlExecutionException;

    /**
     * Stops the executor.
     */
    void stop(SessionContext session);
}

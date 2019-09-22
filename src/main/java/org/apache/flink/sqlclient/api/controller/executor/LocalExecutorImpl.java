package org.apache.flink.sqlclient.api.controller.executor;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.plugin.PluginConfig;
import org.apache.flink.core.plugin.PluginUtils;
import org.apache.flink.sqlclient.api.controller.executor.config.EnvConfigManager;
import org.apache.flink.sqlclient.api.controller.executor.exception.SqlExecutionException;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.client.SqlClientException;
import org.apache.flink.table.client.gateway.ProgramTargetDescriptor;
import org.apache.flink.table.client.gateway.ResultDescriptor;
import org.apache.flink.table.client.gateway.TypedResult;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class LocalExecutorImpl implements Executor{

    //The configuration file defining the cluster to connect to and few cluster specific parameters.
    private final Configuration flinkConfig;

    /**
     * Constructor taking env file map in case of Local Mode
     * @param envFileIsMap
     */
    public LocalExecutorImpl(Map<String, InputStream> envFileIsMap) {
        try {
            //load the flink cluster config file.
            // [link-conf.yaml file which is packaged as one of the files in the input zip file]
            flinkConfig = EnvConfigManager.loadFlinkConfig(envFileIsMap.get("flink-conf.yaml"));
            System.out.println("Flink config details:"+flinkConfig.toMap());
            print(CoreOptions.getParentFirstLoaderPatterns(flinkConfig));
            System.out.println("Plugin config: "+ PluginConfig.fromConfiguration(flinkConfig));


            //Initialize file system default/Actual distributed based on the mode
            //The information for the filesystem is acquired from the first config file flink-conf.yaml
            //For storing the incoming files against the current sessionid is one requirement for the API so
            //that it can be reused for further communication or during recovery
            //This can be a local filesystem for now or any other distributed file system in Real environment[S3 , Hadoop etc]
            FileSystem.initialize(flinkConfig, PluginUtils.createPluginManagerFromRootFolder(flinkConfig));
        }catch (Exception e) {
                throw new SqlClientException("Could not load Flink configuration.", e);
            }
    }

    private void print(String[] strArr){
        for (String s: strArr) {
            System.out.println(s);
        }
    }

    @Override
    public void start() throws SqlExecutionException {

    }

    @Override
    public Map<String, String> getSessionProperties(SessionContext session) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<String> listCatalogs(SessionContext session) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<String> listDatabases(SessionContext session) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<String> listTables(SessionContext session) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<String> listUserDefinedFunctions(SessionContext session) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<String> listFunctions(SessionContext session) throws SqlExecutionException {
        return null;
    }

    @Override
    public void useCatalog(SessionContext session, String catalogName) throws SqlExecutionException {

    }

    @Override
    public void useDatabase(SessionContext session, String databaseName) throws SqlExecutionException {

    }

    @Override
    public TableSchema getTableSchema(SessionContext session, String name) throws SqlExecutionException {
        return null;
    }

    @Override
    public String explainStatement(SessionContext session, String statement) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<String> completeStatement(SessionContext session, String statement, int position) {
        return null;
    }

    @Override
    public ResultDescriptor executeQuery(SessionContext session, String query) throws SqlExecutionException {
        return null;
    }

    @Override
    public TypedResult<List<Tuple2<Boolean, Row>>> retrieveResultChanges(SessionContext session, String resultId) throws SqlExecutionException {
        return null;
    }

    @Override
    public TypedResult<Integer> snapshotResult(SessionContext session, String resultId, int pageSize) throws SqlExecutionException {
        return null;
    }

    @Override
    public List<Row> retrieveResultPage(String resultId, int page) throws SqlExecutionException {
        return null;
    }

    @Override
    public void cancelQuery(SessionContext session, String resultId) throws SqlExecutionException {

    }

    @Override
    public ProgramTargetDescriptor executeUpdate(SessionContext session, String statement) throws SqlExecutionException {
        return null;
    }

    @Override
    public void validateSession(SessionContext session) throws SqlExecutionException {

    }

    @Override
    public void stop(SessionContext session) {

    }
}

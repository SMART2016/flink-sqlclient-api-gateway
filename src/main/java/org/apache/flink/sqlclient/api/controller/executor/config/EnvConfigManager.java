package org.apache.flink.sqlclient.api.controller.executor.config;

import jdk.internal.util.xml.impl.Input;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.flink.table.client.SqlClientException;
import org.apache.flink.table.client.config.ConfigUtil;
import org.apache.flink.table.client.config.Environment;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class EnvConfigManager {

    private static final String[] SENSITIVE_KEYS = new String[]{"password", "secret"};
    private static final Logger LOG = LoggerFactory.getLogger(EnvConfigManager.class);


    public static Configuration loadFlinkConfig(InputStream envFile) {
        return loadYAMLResource(envFile);
    }


    private static Configuration loadYAMLResource(InputStream file) {
        Configuration config = new Configuration();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            Throwable var3 = null;

            try {
                int lineNo = 0;

                String line;
                while ((line = reader.readLine()) != null) {
                    ++lineNo;
                    String[] comments = line.split("#", 2);
                    String conf = comments[0].trim();
                    if (conf.length() > 0) {
                        String[] kv = conf.split(": ", 2);
                        if (kv.length == 1) {
                            LOG.warn("Error while trying to split key and value in configuration file " + file + ":" + lineNo + ": \"" + line + "\"");
                        } else {
                            String key = kv[0].trim();
                            String value = kv[1].trim();
                            if (key.length() != 0 && value.length() != 0) {
                                LOG.info("Loading configuration property: {}, {}", key, isSensitive(key) ? "******" : value);
                                config.setString(key, value);
                            } else {
                                LOG.warn("Error after splitting key and value in configuration file " + file + ":" + lineNo + ": \"" + line + "\"");
                            }
                        }
                    }
                }
            } catch (Throwable var19) {
                var3 = var19;
                throw var19;
            } finally {
                if (reader != null) {
                    if (var3 != null) {
                        try {
                            reader.close();
                        } catch (Throwable var18) {
                            var3.addSuppressed(var18);
                        }
                    } else {
                        reader.close();
                    }
                }

            }

            return config;
        } catch (IOException var21) {
            throw new RuntimeException("Error parsing YAML configuration.", var21);
        }
    }

    private static boolean isSensitive(String key) {
        Preconditions.checkNotNull(key, "key is null");
        String keyInLower = key.toLowerCase();
        String[] var2 = SENSITIVE_KEYS;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String hideKey = var2[var4];
            if (keyInLower.length() >= hideKey.length() && keyInLower.contains(hideKey)) {
                return true;
            }
        }

        return false;
    }


    //1) Returns a blank environment object if no sql client env file is uploaded in the request
    //2) If sql client env file is uploaded as input streams in the API then create an environment parsing the inputstream,
    //3) Latter when the environment inputstream file will be pushed to the Filesystem , an environment with actual env file(sql-client-defaults.yaml)
    // url can be generated.
    //@RuntimeException:  Throws runtime exception SqlClientException
    public static Environment getEnvironment(URL envUrl, Map<String, InputStream> envFileMap) {

        if (envFileMap.get("sql-client-defaults.yaml") == null) {
            return new Environment();
        } else if (envUrl == null) {
            LOG.info("Using session environment file from InputStream stored in inmemory envFileMap");
            try {
                return parse(envFileMap.get("sql-client-defaults.yaml"));
            } catch (IOException e) {
                LOG.error("Could not read session environment file (sql-client-defaults.yaml) From Input stream in envFileMap Reason: ", e);
                throw new SqlClientException("Could not read session environment file (sql-client-defaults.yaml) From Input stream in envFileMap: " + e);
            }
        } else {
            System.out.println("Reading session environment from: " + envUrl);
            LOG.info("Using session environment file: {}", envUrl);
            try {
                return Environment.parse(envUrl);
            } catch (IOException e) {
                LOG.error("Could not read session environment (sql-client-defaults.yaml) file at: {}", envUrl);
                throw new SqlClientException("Could not read session environment (sql-client-defaults.yaml) file at: " + envUrl, e);
            }
        }

    }

    private static Environment parse(InputStream envFile) throws IOException {
        try {
            return new ConfigUtil.LowerCaseYamlMapper().readValue(envFile, Environment.class);
        } catch (JsonMappingException var2) {
            throw new SqlClientException("Could not parse environment file. Cause: " + var2.getMessage());
        }
    }
}

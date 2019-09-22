package org.apache.flink.sqlclient.api.controller.executor.config;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class EnvConfigManager {

    private static final String[] SENSITIVE_KEYS = new String[]{"password", "secret"};
    private static final Logger LOG = LoggerFactory.getLogger(EnvConfigManager.class);


    public static Configuration loadFlinkConfig(InputStream envFile) {
        Configuration conf = null;
        conf = loadYAMLResource(envFile);
        return conf;
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

    public static boolean isSensitive(String key) {
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
}

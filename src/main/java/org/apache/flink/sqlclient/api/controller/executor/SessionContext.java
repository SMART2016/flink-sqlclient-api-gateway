package org.apache.flink.sqlclient.api.controller.executor;

import org.apache.flink.table.client.config.Environment;
import org.apache.flink.table.client.config.entries.ExecutionEntry;
import org.apache.flink.table.client.config.entries.ViewEntry;
import org.apache.flink.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.apache.flink.util.Preconditions.checkArgument;

/**
 * Context describing a session.
 */
public class SessionContext {

    private final String name;

    private final Environment defaultEnvironment;

    private final Map<String, String> sessionProperties;

    private final Map<String, ViewEntry> views;

    public SessionContext(String name, Environment defaultEnvironment) {
        this.name = name;
        this.defaultEnvironment = defaultEnvironment;
        this.sessionProperties = new HashMap<>();
        // the order of how views are registered matters because
        // they might reference each other
        this.views = new LinkedHashMap<>();
    }

    public void setSessionProperty(String key, String value) {
        sessionProperties.put(key, value);
    }

    public void resetSessionProperties() {
        sessionProperties.clear();
    }

    public void addView(ViewEntry viewEntry) {
        views.put(viewEntry.getName(), viewEntry);
    }

    public void removeView(String name) {
        views.remove(name);
    }

    public Map<String, ViewEntry> getViews() {
        return Collections.unmodifiableMap(views);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getCurrentCatalog() {
        return Optional.ofNullable(sessionProperties.get(ExecutionEntry.EXECUTION_CURRENT_CATALOG));
    }

    public void setCurrentCatalog(String currentCatalog) {
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(currentCatalog));

        sessionProperties.put(ExecutionEntry.EXECUTION_CURRENT_CATALOG, currentCatalog);
    }

    public Optional<String> getCurrentDatabase() {
        return Optional.ofNullable(sessionProperties.get(ExecutionEntry.EXECUTION_CURRENT_DATABASE));
    }

    public void setCurrentDatabase(String currentDatabase) {
        checkArgument(!StringUtils.isNullOrWhitespaceOnly(currentDatabase));

        sessionProperties.put(ExecutionEntry.EXECUTION_CURRENT_DATABASE, currentDatabase);
    }

    public Environment getEnvironment() {
        return Environment.enrich(
                defaultEnvironment,
                sessionProperties,
                views);
    }

    public SessionContext copy() {
        final SessionContext session = new SessionContext(name, defaultEnvironment);
        session.sessionProperties.putAll(sessionProperties);
        session.views.putAll(views);
        return session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionContext)) {
            return false;
        }
        SessionContext context = (SessionContext) o;
        return Objects.equals(name, context.name) &&
                Objects.equals(defaultEnvironment, context.defaultEnvironment) &&
                Objects.equals(sessionProperties, context.sessionProperties) &&
                Objects.equals(views, context.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                defaultEnvironment,
                sessionProperties,
                views);
    }
}

package it.tinna.smartdoc.server.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class SmartDocRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String key = DatabaseContextHolder.getClientDatabase();
        log.debug("Routing DataSource to: {}", key);
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(key, "servicedb")) {
            return "servicedb";
        }
        return "shareddb";
    }
}

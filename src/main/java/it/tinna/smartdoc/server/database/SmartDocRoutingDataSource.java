package it.tinna.smartdoc.server.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class SmartDocRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getClientDatabase();
    }
}

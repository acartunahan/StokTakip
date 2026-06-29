package com.stoktakip.migration.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class MigrationContext {

    private final Long calismaId;
    private final boolean dryRun;
    private final Map<String, TableMigrationStats> stats = new LinkedHashMap<>();

    public MigrationContext(Long calismaId, boolean dryRun) {
        this.calismaId = calismaId;
        this.dryRun = dryRun;
    }

    public Long calismaId() {
        return calismaId;
    }

    public boolean dryRun() {
        return dryRun;
    }

    public TableMigrationStats stats(String tabloAdi) {
        return stats.computeIfAbsent(tabloAdi, ignored -> new TableMigrationStats());
    }

    public String ozetJson() {
        StringBuilder json = new StringBuilder("{\"dryRun\":").append(dryRun).append(",\"tables\":[");
        boolean ilkDolu = true;
        for (Map.Entry<String, TableMigrationStats> entry : stats.entrySet()) {
            if (!ilkDolu) {
                json.append(',');
            }
            json.append(entry.getValue().toJson(entry.getKey()));
            ilkDolu = false;
        }
        return json.append("]}").toString();
    }
}

package com.superbool.watcher.model;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by superbool on 16/9/28.
 */
@Data
@NoArgsConstructor
public class MeasurementModel {
    private String database;
    private String measurement;
    private List<String> tags;
    private List<String> fields;

    public MeasurementModel(String database, String measurement) {
        this.database = database;
        this.measurement = measurement;
    }

    public String toJsonStr() {
        return new Gson().toJson(this);
    }

}

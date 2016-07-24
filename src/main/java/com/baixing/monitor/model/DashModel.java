package com.baixing.monitor.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by kofee on 16/7/22.
 */

@NoArgsConstructor
@Data
public class DashModel {
    private long id;
    private int version = 0;
    private String slug = "";
    private String title = "";
    private String data = "";
    private long orgId = 0;
    private Date created = new Date();
    private Date updated = new Date();
    private int updatedBy = 0;
    private int createdBy = 0;
    private long gnetId = 0;
    private String pluginId = "";

}

package com.baixing.monitor.util;

/**
 * Created by kofee on 16/7/31.
 */
public enum OrgEnum {
    Org1(1, "业务系统部", "yaoguang"),
    Org2(2, "营收运营部", ""),
    Org3(3, "数据部", ""),
    Org4(4, "产品部", ""),
    Unknown(6, "", "");

    private int orgId;
    private String orgDesc;
    private String database;

    OrgEnum(int orgId, String orgDesc, String database) {
        this.orgId = orgId;
        this.orgDesc = orgDesc;
        this.database = database;
    }

    public static OrgEnum valueOf(int orgId) {
        switch (orgId) {
            case 1:
                return Org1;
            case 2:
                return Org2;
            case 3:
                return Org3;
            case 4:
                return Org4;
        }
        return Unknown;

    }

    public int getOrgId() {
        return orgId;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public String getDatabase() {
        return database;
    }
}

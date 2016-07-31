package com.baixing.monitor.util;

/**
 * Created by kofee on 16/7/31.
 */
public enum OrgEnum {
    Org1(1, "业务系统部"),
    Org2(2, "这个部门"),
    Org3(3, "那个部门"),
    Org4(4, "主站");

    private long orgId;
    private String orgDesc;

    OrgEnum(long orgId, String orgDesc) {
        this.orgId = orgId;
        this.orgDesc = orgDesc;
    }

    public long getOrgId() {
        return orgId;
    }

    public String getOrgDesc() {
        return orgDesc;
    }
}

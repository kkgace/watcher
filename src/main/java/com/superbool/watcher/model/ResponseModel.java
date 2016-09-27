package com.superbool.watcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kofee on 16/7/23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {
    private int code;
    private String msg;
}

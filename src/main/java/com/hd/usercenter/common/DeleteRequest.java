package com.hd.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @auther hd
 * @Description 通用删除请求
 */
@Data
public class DeleteRequest implements Serializable {


    private static final long serialVersionUID = -971987862566843257L;

    private long id;
}

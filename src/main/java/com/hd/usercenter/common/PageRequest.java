package com.hd.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @auther hd
 * @Description
 */
@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = -971987862566843257L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}

package com.loyo.oa.v2.activity.home.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 销售统计全部数据
 * Created by xeq on 16/6/17.
 */
public class HttpStatistics implements Serializable {
    public List<HttpAchieves> achieves;//业绩目标
    public List<HttpSalechance> salechance;//销售漏斗
    public List<HttpProcess> process;//过程统计
    public List<HttpBulking> bulking;//增量统计
}

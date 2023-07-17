package com.ruofei.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
@Data
@TableName(value = "trffpn_app.veh_cr_procstatus_view")
//@TableName(value = "VEH_CR_PROCSTATUS")
public class Video {
    @TableId(value = "LSH")
    private String LSH; // 流水号
    @TableField(value = "CYQXH")
    private String CYQXH; // 查验区序号
    @TableField(value = "HPZL")
    private String HPZL;// 号牌种类
    @TableField(value = "HPHM")
    private String HPHM;//号牌号码
    @TableField(value = "CLSBDH")
    private String CLSBDH;//识别代号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "KSSJ")
    private java.util.Date KSSJ;//开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "XTJSSJ")
    private java.util.Date XTJSSJ;//系统结束时间
}
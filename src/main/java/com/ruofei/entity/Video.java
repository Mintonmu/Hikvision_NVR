package com.ruofei.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data
@TableName(value = "VEH_CR_PROCSTATUS")
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
    @TableField(value = "KSSJ")
    private java.util.Date KSSJ;//开始时间
    @TableField(value = "XTJSSJ")
    private java.util.Date XTJSSJ;//系统结束时间
    @TableField(value = "CYZT")
    private char CYZT;//查验状态
    @TableField(value = "JLZT")
    private char JLZT;//记录状态
}

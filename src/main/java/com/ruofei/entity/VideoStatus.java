package com.ruofei.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "VEH_CR_VIDEO_RECORD")
public class VideoStatus {
    public VideoStatus() {
    }

    public VideoStatus(String lsh, Date KSSJ, Date XTJSSJ, int status) {
        this.LSH = lsh;
        this.KSSJ = KSSJ;
        this.XTJSSJ = XTJSSJ;
        this.STATUS = status;
    }

    @TableField(value = "LSH")
    private String LSH; // 流水号
    @TableField(value = "KSSJ")
    private Date KSSJ;
    @TableField(value = "XTJSSJ")
    private Date XTJSSJ;
    @TableField(value = "STATUS")
    private int STATUS;//下载状态
}

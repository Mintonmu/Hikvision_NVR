package com.ruofei.domain;

import com.vaadin.flow.component.button.Button;
import lombok.Data;

@Data
public class DownloadedVideoFile {

    private String fileName;
    private String downloadProgress;
    private String fileSize;
    private Button operator;
}
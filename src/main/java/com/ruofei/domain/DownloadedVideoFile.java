package com.ruofei.domain;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Data;

@Data
public class DownloadedVideoFile {

    private String fileName;
    private String downloadProgress;
    private String fileSize;
    private String operator;
}
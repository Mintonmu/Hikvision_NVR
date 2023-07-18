package com.ruofei.view;

import com.github.olafj.vaadin.flow.Video;
import com.ruofei.components.VideoCompoment;
import com.ruofei.components.VideoDialog;
import com.ruofei.components.VideoDownloader;
import com.ruofei.components.VideoPresetEditor;
import com.ruofei.domain.DownloadedVideoFile;
import com.ruofei.okhttp3.DownloadFileProgressListener;
import com.ruofei.repo.VideoEncoderPresetRepository;
import com.ruofei.service.DownloadService;
import com.ruofei.service.EncodeService;
import com.ruofei.util.FileUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.language.Bean;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Theme(Material.class)
@Route
@Push
@Slf4j

public class MainView extends VerticalLayout {

    private static VideoCompoment video;
    private final VideoEncoderPresetRepository videoEncoderPresetRepository;
    private final Grid<DownloadedVideoFile> downloadedVideoFilesGrid = new Grid<>(DownloadedVideoFile.class);
    private final VideoPresetEditor editor;
    private final VideoDownloader videoDownloader;
    private final DownloadService downloadService;
    private final Button downloadBtn = new Button("截取", VaadinIcon.DOWNLOAD.create());
    private final Div message = createMessageDiv("tes");
    private final HorizontalLayout dateBar = new HorizontalLayout(message);
    private final TextField nvrIpAddress = new TextField("NVR IP Address", "127.0.0.1", "请输入服务器地址");
    //    private final TextField channel = new TextField("NVR channel", "3", "请输入请求通道");
    private final TextField startDateTime = new TextField("start datetime", "请输入开始时间");
    private final TextField endDateTime = new TextField("end datetime", "请输入结束时间");

    private final HorizontalLayout downloadParamsBar = new HorizontalLayout(nvrIpAddress, startDateTime, endDateTime, downloadBtn);
    //    private final HorizontalLayout downloadBar = new HorizontalLayout();
    private final VerticalLayout downloadVideoLayout = new VerticalLayout(downloadParamsBar, dateBar, downloadedVideoFilesGrid);
    Editor<DownloadedVideoFile> DownloadedVideoFileeditor = downloadedVideoFilesGrid.getEditor();
    private DownloadFileProgressListener progressListener;
    private Timer autoUpdateInfoOnPage = new Timer();
    private String query;
    public MainView(DownloadService downloadService, EncodeService encodeService, VideoEncoderPresetRepository videoEncoderPresetRepository, VideoPresetEditor editor, VideoDownloader videoDownloader) {
        VaadinRequest request = VaadinService.getCurrentRequest();
         query = request.getParameter("lsh");

        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        startDateTime.setValue(sdf.format(date));
        endDateTime.setValue(sdf.format(date));
        File dir = new File(FileUtil.nvrHomeDir());
        if (!dir.exists()) dir.mkdirs();
        this.downloadService = downloadService;
        this.videoEncoderPresetRepository = videoEncoderPresetRepository;
        this.editor = editor;
        this.videoDownloader = videoDownloader;
        add(downloadVideoLayout, videoDownloader, editor);
        downloadedVideoFilesGrid.setColumns("fileName");
        downloadedVideoFilesGrid.addComponentColumn(p -> {
            Button frontbtn = new Button("前");
            frontbtn.addClickListener(e -> {
                //TODO:完成按钮响应事件
                Video video = new Video();
                video.setWidth("700px");
                video.setControls(true);
                Path path = Paths.get(p.getFileName());
                // 获取文件名
                String fileName = path.getFileName().toString();
//                String b = FileUtil.nvrHomeDir()  + "/" + fileName + "/ch-1" + fileName.substring(2)+ ".mp4";
                video.setSource("api/media/video?path=" + "/" + fileName + "/ch-1" + fileName.substring(16) + ".mp4");
                VideoDialog videoDialog = new VideoDialog(video);
                add(videoDialog);
            });
            return frontbtn;
        }).setWidth("150px").setFlexGrow(0);
        downloadedVideoFilesGrid.addComponentColumn(p -> {
            Button backbtn = new Button("后");
            backbtn.addClickListener(e -> {
                //TODO:完成按钮响应事件
                Video video = new Video();
                video.setWidth("700px");
                video.setControls(true);
                Path path = Paths.get(p.getFileName());
                // 获取文件名
                String fileName = path.getFileName().toString();
//                String b = FileUtil.nvrHomeDir()  + "/" + fileName + "/ch-3" + fileName.substring(2)+ ".mp4";
                video.setSource("api/media/video?path=" + "/" + fileName + "/ch-4" + fileName.substring(16) + ".mp4");
                VideoDialog videoDialog = new VideoDialog(video);
                add(videoDialog);
            });
            return backbtn;
        }).setWidth("150px").setFlexGrow(0);
        downloadedVideoFilesGrid.addComponentColumn(p -> {
            Button movebtn = new Button("移动");
            movebtn.addClickListener(e -> {
                //TODO:完成按钮响应事件
                Video video = new Video();
                video.setWidth("700px");
                video.setControls(true);
                Path path = Paths.get(p.getFileName());
                // 获取文件名
                String fileName = path.getFileName().toString();
//                String b = FileUtil.nvrHomeDir()  + "/" + fileName + "/ch-5" + fileName.substring(2)+ ".mp4";
                video.setSource("api/media/video?path=" + "/" + fileName + "/ch-5" + fileName.substring(16) + ".mp4");
                VideoDialog videoDialog = new VideoDialog(video);
                add(videoDialog);
            });
            return movebtn;
        }).setWidth("150px").setFlexGrow(0);

        downloadBtn.addClickListener(e -> {
            CompletableFuture.runAsync(() -> downloadService.download(nvrIpAddress.getValue(), startDateTime.getValue(), endDateTime.getValue(), progressListener));
        });
        message.setVisible(true);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        progressListener = new DownloadFileProgressListener() {
            boolean firstUpdate = true;

            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                attachEvent.getUI().access(() -> {
//                    freeSpace();
                    if (done) {
                        message.setText("downloaded " + FileUtil.humanReadableByteCount(contentLength));
                    } else {
                        if (firstUpdate) {
                            firstUpdate = false;
                            if (contentLength == -1) {
                                MainView.log.info("content-length: unknown");
                            } else {
                                System.out.format("content-length: %d\n", contentLength);
                            }
                        }

                        if (contentLength != -1) {
                            message.setText((100 * bytesRead) / contentLength + "% done " + FileUtil.humanReadableByteCount(bytesRead) + " of " + FileUtil.humanReadableByteCount(contentLength));
                        }
                    }
                });
            }
        };

        autoUpdateInfoOnPage.schedule(new TimerTask() {
            @Override
            public void run() {
                attachEvent.getUI().access(() -> {
//                    freeSpace();
                    filesList();
                    attachEvent.getUI().push();
                });
            }
        }, 0, 1000 * 30);

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        progressListener = null;
        autoUpdateInfoOnPage = null;
    }

    private void filesList() {
        Collection<DownloadedVideoFile> items = new LinkedList<DownloadedVideoFile>();
        try {
            Files.newDirectoryStream(Paths.get(FileUtil.nvrHomeDir()), new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) throws IOException {
                    return !entry.toString().endsWith(".DS_Store");
                }
            }).forEach(file -> {
                if (!"".equals(query) && file.toFile().getAbsolutePath().contains(query.trim())) {
                    DownloadedVideoFile item = new DownloadedVideoFile();
                    item.setFileName(file.toFile().getAbsolutePath());
                    items.add(item);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadedVideoFilesGrid.setItems(items);
    }

    private void UpdateMessage(Div message, DatePicker datePicker) {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            message.setText("Day: " + selectedDate.getDayOfMonth() + "\nMonth: " + selectedDate.getMonthValue() + "\nYear: " + selectedDate.getYear() + "\nLocale: " + datePicker.getLocale());
        } else {
            message.setText("No date is selected");
        }
    }

    private Div createMessageDiv(String id) {
        Div message = new Div();
        message.setId(id);
        message.getStyle().set("whiteSpace", "pre");
        return message;
    }

}
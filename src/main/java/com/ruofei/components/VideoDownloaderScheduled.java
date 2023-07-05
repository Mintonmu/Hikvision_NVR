package com.ruofei.components;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ruofei.dao.VideoMapper;
import com.ruofei.dao.VideoStatusMapper;
import com.ruofei.entity.Video;
import com.ruofei.entity.VideoStatus;
import com.ruofei.util.DownloadSimpleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class VideoDownloaderScheduled {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoStatusMapper statusMapper;
    private DateFormat dateformat = new SimpleDateFormat("yyyyMMdd'T'hhmmssX");
    private DateFormat save_dateformat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    @Autowired
    private Environment environment;

    public static long getTime(Date startTime, Date endTime) throws ParseException {
        long eTime = endTime.getTime();
        long sTime = startTime.getTime();
        long diff = (eTime - sTime) / 1000;
        return diff;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduled() {
        List<Video> videoList = videoMapper.selectList(null);
        for (Video video :
                videoList) {
            String lsh = video.getLSH();

            VideoStatus videoStatus = new LambdaQueryChainWrapper<>(statusMapper)
                    .eq(VideoStatus::getLSH, video.getLSH())
                    .eq(VideoStatus::getKSSJ, video.getKSSJ()).one();
            if (videoStatus == null || videoStatus.getSTATUS() == 0) {
                // 查询不到记录，或者记录状态为0，开始下载
                // -------------- 下载视频
                int status = 0;

                Date startTime = video.getKSSJ();
                Date endTime = video.getXTJSSJ();

                try {
                    String startTimeStr = dateformat.format(startTime).toLowerCase();
                    startTimeStr = startTimeStr.substring(0, startTimeStr.indexOf('+')) + 'z';
                    String endTimeStr = dateformat.format(endTime).toLowerCase();
                    endTimeStr = endTimeStr.substring(0, endTimeStr.indexOf('+')) + 'z';

                    String path_pre = "rtsp://" + environment.getProperty("nvr.username") + ":" + environment.getProperty("nvr.password") + "@" + environment.getProperty("nvr.host") + ":" + environment.getProperty("nvr.port");
                    String src1 = path_pre + "/Streaming/tracks/101?starttime=" + startTimeStr + "&&endtime=" + endTimeStr;
                    String src2 = path_pre + "/Streaming/tracks/301?starttime=" + startTimeStr + "&&endtime=" + endTimeStr;
                    String src3 = path_pre + "/Streaming/tracks/501?starttime=" + startTimeStr + "&&endtime=" + endTimeStr;
//                    String src1 = "rtsp://192.168.31.244:554/live1";
//                    String src2 = "rtsp://192.168.31.244:554/live3";
//                    String src3 = "rtsp://192.168.31.244:554/live5";
                    String dir = environment.getProperty("nvr.home") + "\\ch-" + lsh + "-" + save_dateformat.format(startTime) + "-" + save_dateformat.format(endTime) + "\\";
                    File file = new File(dir);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String dest1 = dir + "ch-1-" + save_dateformat.format(startTime) + "-" + save_dateformat.format(endTime) + ".mp4";
                    String dest2 = dir + "ch-3-" + save_dateformat.format(startTime) + "-" + save_dateformat.format(endTime) + ".mp4";
                    String dest3 = dir + "ch-5-" + save_dateformat.format(startTime) + "-" + save_dateformat.format(endTime) + ".mp4";

                    boolean isFinish1 = DownloadSimpleUtil.convert(src1, dest1, getTime(startTime, endTime));
                    boolean isFinish2 = DownloadSimpleUtil.convert(src2, dest2, getTime(startTime, endTime));
                    boolean isFinish3 = DownloadSimpleUtil.convert(src3, dest3, getTime(startTime, endTime));

//                    System.out.println("________________________________");
//                    System.out.println(lsh);
//                    System.out.println(startTimeStr);
//                    System.out.println(endTimeStr);
//
//                    System.out.println(isFinish1);
//                    System.out.println(isFinish2);
//                    System.out.println(isFinish3);
//                    System.out.println("________________________________");
                    if (isFinish1 && isFinish2 && isFinish3) {
                        status = 1;
                    }

                } catch (Exception e) {
//                    System.out.println("-------------------------");
//                    System.out.println(e);
//                    System.out.println("-------------------------");
//                    e.printStackTrace();
                }

                // -------------- 下载结束


                //下载完成插入数据
                int res = 0;
                VideoStatus newStatus = new VideoStatus(lsh, video.getKSSJ(), video.getXTJSSJ(), status);
                if (videoStatus == null) {
                    res = statusMapper.insert(newStatus);
                } else {
                    LambdaUpdateWrapper<VideoStatus> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper
                            .eq(VideoStatus::getLSH, video.getLSH())
                            .eq(VideoStatus::getKSSJ, video.getKSSJ())
                            .set(VideoStatus::getSTATUS, status);
                    res = statusMapper.update(null, lambdaUpdateWrapper);
                }
                System.out.println(res);

            }
        }
    }
}
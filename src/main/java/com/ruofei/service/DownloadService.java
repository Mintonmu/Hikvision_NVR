package com.ruofei.service;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ruofei.util.DownloadUtil;
import com.ruofei.util.FileUtil;
import org.springframework.stereotype.Service;

import com.ruofei.okhttp3.DownloadFileProgressListener;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DownloadService{

    public void download(String nvrIpAddress, String channel, String startDateTime, String endDateTime, DownloadFileProgressListener progressListener) {
//        rtsp://admin:hq8569678@172.16.16.170:554/Streaming/tracks/101??starttime=20230601t102012z&endtime=20230601t102112z

//        String url = ("http://" + nvrIpAddress + "/cgi-bin/c?action=startLoad&channel=" + channel + "&startTime="
//                + startDateTime + "&endTime=" + endDateTime).replace(" ", "%20");

        String url1 = "rtsp://" + nvrIpAddress + ":554/live1";
        String url3 = "rtsp://" + nvrIpAddress + ":554/live3";
        String url5 = "rtsp://" + nvrIpAddress + ":554/live5";
        long secs = 0;
        try {
            secs = getTime(startDateTime, endDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String destDirName = FileUtil.nvrHomeDir() + FileUtil.makeDirName(startDateTime, endDateTime);
        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        String dest1 = destDirName + "\\\\" + FileUtil.makeFileName("1", startDateTime, endDateTime) + ".mp4";
        String dest3 = destDirName + "\\\\"+ FileUtil.makeFileName("3", startDateTime, endDateTime) + ".mp4";
        String dest5 = destDirName + "\\\\" + FileUtil.makeFileName("5", startDateTime, endDateTime) + ".mp4";
//        System.out.println(dest1);
        try {
            DownloadUtil.convert(url1, dest1, secs, progressListener);
            DownloadUtil.convert(url3, dest3, secs, progressListener);
            DownloadUtil.convert(url5, dest5, secs, progressListener);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //计算两个时间相差的秒数
    public static long getTime(String startTime, String endTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long eTime = df.parse(endTime).getTime();
        long sTime = df.parse(startTime).getTime();
        long diff = (eTime - sTime) / 1000;
        return diff;
    }

}
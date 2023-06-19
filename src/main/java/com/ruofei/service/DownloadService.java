package com.ruofei.service;

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

//        String url = ("http://" + nvrIpAddress + "/cgi-bin/c?action=startLoad&channel=" + channel + "&startTime="
//                + startDateTime + "&endTime=" + endDateTime).replace(" ", "%20");
        String url = "rtsp://localhost:554/live";
        long secs = 0;
        try {
            secs = getTime(startDateTime, endDateTime);
        } catch (ParseException e) {
            System.out.println();
            throw new RuntimeException(e);
        }
        String dest = FileUtil.nvrHomeDir() + FileUtil.makeFileName(channel, startDateTime, endDateTime) + ".mp4";
        try {
            DownloadUtil.convert(url, dest, secs, progressListener);
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
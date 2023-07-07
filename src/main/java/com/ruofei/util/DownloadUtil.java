package com.ruofei.util;

import com.ruofei.okhttp3.DownloadFileProgressListener;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadUtil implements Runnable {
    InputStream in; // 正常输出流
    boolean stop; // 结束进程
    Process pro;
    long secs;
    DownloadFileProgressListener progressListener;

    public DownloadUtil(Process process, InputStream in, long secs, DownloadFileProgressListener progressListener) {
        this.in = in;
        this.pro = process;
        this.progressListener = progressListener;
        this.secs = secs;
    }

    public static void convert(String src, String dest, long secs, DownloadFileProgressListener progressListener) throws Exception {
        String ffcmdpath = "cmd /c ffmpeg -timeout 5";
        StringBuilder cmd = new StringBuilder();
        cmd.append(ffcmdpath)
                .append(" -rtsp_transport tcp ") // 使用tcp的命令，默认是udp
                .append(" -i ")
                .append(" \"")
                .append(src)
                .append("\"")
                .append(" -vcodec copy ")
                .append(" -y ") // 覆盖
                .append(dest);

        System.out.println(cmd);
        Process process = Runtime.getRuntime().exec(cmd.toString());
        // 输出内容
        DownloadUtil twffIn = new DownloadUtil(process, process.getInputStream(), secs, progressListener);
        DownloadUtil twffInErr = new DownloadUtil(process, process.getErrorStream(), secs, progressListener);
        Thread t = new Thread(twffIn);
        Thread t1 = new Thread(twffInErr);
        t.start();
        t1.start();

        int i = process.waitFor(); // 一定要配合2个 inputstream ，要不然会一直阻塞
        twffIn.setStop(true);
        twffInErr.setStop(true); // 停止 线程
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


    @Override
    public void run() {
        Scanner scanner = new Scanner(in);
        while (!stop) {
            if (scanner.hasNext()) {
                String s = scanner.nextLine();
                System.out.println(s);
                // 判断停止录像的条件

                if (s.startsWith("frame=")) {
                    String time = s.split("time=")[1].substring(0, 8);
                    if (time.charAt(2) == ':' && time.charAt(5) == ':') {
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                        try {
                            long seconds = format1.parse(time).getSeconds();
                            if (seconds >= this.secs) {
                                cnt++;
                                this.progressListener.update(seconds, this.secs, cnt == 3);
                                stop = true;
                            } else {
                                this.progressListener.update(seconds, this.secs, false);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
        System.out.println("###读取线程结束啦###");
    }

    static int cnt;
}

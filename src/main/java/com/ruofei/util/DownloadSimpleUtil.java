package com.ruofei.util;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class DownloadSimpleUtil implements Runnable {
    InputStream in; // 正常输出流
    boolean stop; // 结束进程
    Process pro;
    long secs;
    boolean isFinish;

    public DownloadSimpleUtil(Process process, InputStream in, long secs) {
        this.in = in;
        this.pro = process;
        this.secs = secs;
        this.stop = false;
        this.isFinish = false;
    }

    public static boolean convert(String src, String dest, long secs) throws Exception {

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
        DownloadSimpleUtil twffIn = new DownloadSimpleUtil(process, process.getInputStream(), secs);
        DownloadSimpleUtil twffInErr = new DownloadSimpleUtil(process, process.getErrorStream(), secs);
        Thread t = new Thread(twffIn);
        Thread t1 = new Thread(twffInErr);
        t.start();
        t1.start();

        process.waitFor(); // 一定要配合2个 inputstream ，要不然会一直阻塞
        twffIn.setStop(true);
        twffInErr.setStop(true); // 停止 线程

        return twffIn.isFinish || twffInErr.isFinish;
    }

    public static boolean convert2(String src, String dest, long secs) throws Exception {

        String ffcmdpath = "cmd /c ffmpeg -reorder_queue_size 0 -timeout 5";
        StringBuilder cmd = new StringBuilder();
        cmd.append(ffcmdpath)
                .append(" -rtsp_transport tcp ") // 使用tcp的命令，默认是udp
                .append(" -i ")
                .append(" \"")
                .append(src)
                .append("\"")
                .append(" -f m4v ")
                .append(" -vcodec copy ")
                .append(" -y ") // 覆盖
                .append(dest + "_tmp")
                .append(" && ffmpeg -i ")
                .append(dest + "_tmp")
                .append(" -vcodec copy ")
                .append(" -y ")
                .append(dest)
                .append(" && ")
                .append(" del/f/s/q ")
                .append(dest + "_tmp");

        System.out.println(cmd);
        Process process = Runtime.getRuntime().exec(cmd.toString());
        // 输出内容
        DownloadSimpleUtil twffIn = new DownloadSimpleUtil(process, process.getInputStream(), secs);
        DownloadSimpleUtil twffInErr = new DownloadSimpleUtil(process, process.getErrorStream(), secs);
        Thread t = new Thread(twffIn);
        Thread t1 = new Thread(twffInErr);
        t.start();
        t1.start();

        process.waitFor(); // 一定要配合2个 inputstream ，要不然会一直阻塞
        twffIn.setStop(true);
        twffInErr.setStop(true); // 停止 线程

        return twffIn.isFinish || twffInErr.isFinish;
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
                if (s.contains("Qavg:") || s.contains("删除文件")) {
                    stop = true;
                    isFinish = true;
                    break;
                }
                if (s.startsWith("frame=")) {
                    String time = s.split("time=")[1].substring(0, 8);
                    if (time.charAt(2) == ':' && time.charAt(5) == ':') {
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                        try {
                            long seconds = format1.parse(time).getSeconds();
                            if (seconds >= this.secs) {
                                stop = true;
                                isFinish = true;
                                break;
                            } else {
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
}

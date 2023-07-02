package com.ruofei.util;

import com.ruofei.okhttp3.DownloadFileProgressListener;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

public class DownloadUtil implements Runnable{
    InputStream in ; // 正常输出流
    boolean stop ; // 结束进程
    int maxFrameCount = 75000;
    Process pro ;
    long secs;
    DownloadFileProgressListener progressListener;

    public DownloadUtil(Process process, InputStream in, long secs, DownloadFileProgressListener progressListener){
        this.in = in;
        this.pro = process;
        this.progressListener = progressListener;
        this.secs = secs;
    }
    public static void convert(String src, String dest, long secs, DownloadFileProgressListener progressListener)throws Exception{
        String ffcmdpath = "cmd /c ffmpeg";
        StringBuilder cmd = new StringBuilder();
        cmd.append(ffcmdpath)
                .append(" -rtsp_transport tcp ") // 使用tcp的命令，默认是udp
                .append(" -i ").append(src)
                	.append(" -vcodec copy ")
//                .append(" -vcodec h264 ")
                //	.append(" -acodec copy ") // 音频，不设置好像也有。
//                //	.append(" -s 1280*720 ")   // 设置分辨率，关系到视频的大小或者为 -vf scale=iw/2:ih/2
                //	.append(" -vf scale=iw/2:ih/2 ")
                .append(" -y ") // 覆盖
                .append(dest);
//
//        cmd.append("cmd /c gst-launch-1.0 -e rtspsrc location=")
//            .append(src)
//            .append(" protocols=tcp ! rtph264depay ! h264parse ! mp4mux ! progressreport update-freq=1 ! filesink location=")
//            .append(dest);
        System.out.println(cmd);
        Process process = Runtime.getRuntime().exec(cmd.toString());
        // 输出内容
        DownloadUtil twffIn = new DownloadUtil(process, process.getInputStream(), secs, progressListener);
        DownloadUtil twffInErr = new DownloadUtil(process,process.getErrorStream(), secs, progressListener);
        Thread t = new Thread(twffIn);
        Thread t1 = new Thread(twffInErr);
        t.start();t1.start();
        // 停止指令,10秒后停止 ,一定要发送这个，要不然视频保存不下来

        int i = process.waitFor(); // 一定要配合2个 inputstream ，要不然会一直阻塞
        twffIn.setStop(true);twffInErr.setStop(true); // 停止 线程
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


    /**
     * @title stopConvert
     * @date 2018年10月11日
     * @param process
     * @return
     * @description 发送停止命令,
     * 但是要注意发送完成后，不一定会马上停止 。因为进程结束也是需要时间的
     */
    public static boolean stopConvert(Process process ){
//        System.out.println("###send EOS cmd ");
        try{
//            if (isLinux()) {
//                String pid = getProcessId(process);
//                if (pid != null) {
//                    ProcessBuilder killProcessBuilder = new ProcessBuilder("kill", "-2", pid);
//                    killProcessBuilder.start();
//                }
//            }
//            long pid = process.pid();
//            Runtime.getRuntime().exec("kill " + pid);
//            BufferedWriter out = new BufferedWriter (new OutputStreamWriter(process.getOutputStream()));
//
////            char ctrlBreak = (char)3;
//            out.write(0x03);
//            out.flush();
//            out.write(0x03);
//            out.write('\n');
//            out.flush();
//            process.destroyForcibly();

        }catch(Exception err){
//            err.printStackTrace();
            System.out.println(err.toString());
            return false;
        }
        return true;
    }
    private static boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }

    private static String getProcessId(Process process) {
        try {
            // 获取Linux系统中进程的PID
            if (isLinux()) {
                Field pidField = process.getClass().getDeclaredField("pid");
                pidField.setAccessible(true);
                return String.valueOf(pidField.getInt(process));
            }
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void run() {
        Scanner scanner = new Scanner(in);
        while(!stop){
            if(scanner.hasNext()){
                String s = scanner.nextLine();
                System.out.println(s);
                // 判断停止录像的条件

                if(s.contains("progressreport0")){
                    int seconds = Integer.parseInt(s.substring(s.indexOf("):")+2
                            ,s.indexOf("seconds")).trim());
                    if(seconds >= this.secs/3.0){
                        cnt ++;
                        this.progressListener.update(seconds, this.secs, cnt == 3);
                        stop=true;
                        stopConvert(pro);
                    }else{
                        this.progressListener.update(seconds, this.secs, false);
                    }

                }
            }else{
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

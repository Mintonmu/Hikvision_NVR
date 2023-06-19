package com.ruofei.okhttp3;

public interface DownloadFileProgressListener {
    
    void update(long bytesRead, long contentLength, boolean done);
}
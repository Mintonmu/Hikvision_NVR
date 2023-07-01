package com.ruofei;

import com.ruofei.dao.VideoMapper;
import com.ruofei.entity.Video;
import org.freedesktop.gstreamer.glib.GObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.PlayBin;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NvrVideoDownloaderApplicationTests {

	@Test
	public void contextLoads() {
	}
	@Autowired
	private VideoMapper videoMapper;

	@Test
	public void testSelect() {
		System.out.println("--------selectAll method test-------");
		//查询全部，参数是一个Wrapper，条件构造器，先不使用为null
//        System.out.println(videoMapper);
//        List<VideoCompoment> videoList = videoMapper.selectList(null);
//        videoList.forEach(System.out::println);
		Video video = videoMapper.selectById("1230607016485");
		System.out.println(video);
	}

	@Test
	public void rtspDownload(){
		// 初始化 GStreamer
		Gst.init("RTSPVideoDownloader", null);

		// 创建播放器元素
		PlayBin playBin = new PlayBin("PlayBin");

		// 设置播放器参数
		playBin.setURI(URI.create("rtsp rtsp://localhost:554/live1"));  // 替换为实际的 RTSP URL
		Element filesink = ElementFactory.make("filesink", "output");
		filesink.set("location", "/Users/niudale/nvr/ch-2019-04-19-13-46-00-2019-04-19-13-46-13/output.mp4");  // 设置保存视频的输出文件名

		// 创建管道
		Pipeline pipeline = new Pipeline();
		pipeline.addMany(playBin, filesink);

		// 连接元素
		playBin.getStaticPad("video").link(filesink.getStaticPad("sink"));

		// 启动播放器
		pipeline.setState(State.PLAYING);

		// 等待下载完成
		Bus bus = pipeline.getBus();
		bus.connect((Bus.EOS) gstObject -> {
			System.out.println("视频下载完成");
			pipeline.setState(State.NULL);
			Gst.quit();
		});

		// 运行 GStreamer 主循环
		Gst.main();
	}

}

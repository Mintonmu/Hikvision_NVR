package com.ruofei;

import com.ruofei.dao.VideoMapper;
import com.ruofei.entity.Video;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}

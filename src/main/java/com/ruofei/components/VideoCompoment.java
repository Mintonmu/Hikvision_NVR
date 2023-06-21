package com.ruofei.components;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("video")
public class VideoCompoment extends HtmlContainer {

    private volatile static VideoCompoment videoCompoment;
    private static final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault(
            "src",
            ""
    );

    public static VideoCompoment getvideoCompoment() {
        if (videoCompoment == null) {
            synchronized (VideoCompoment.class) {
                if (videoCompoment == null) {
                    videoCompoment = new VideoCompoment();
                }
            }
        }
        return videoCompoment;
    }

    private VideoCompoment() {
        super();
        getElement().setProperty("controls", true);
    }

    private VideoCompoment(String src) {
        setSrc(src);
        getElement().setProperty("controls", true);
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}
package com.ruofei.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VideoDialog extends Div {
    public VideoDialog(VideoCompoment videoCompoment) {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = createDialogLayout(dialog, videoCompoment);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private static VerticalLayout createDialogLayout(Dialog dialog, VideoCompoment videoCompoment) {
        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());
        VerticalLayout dialogLayout = new VerticalLayout(videoCompoment, closeButton);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "600px").set("max-width", "100%");
        dialogLayout.setAlignSelf(FlexComponent.Alignment.END, closeButton);
        return dialogLayout;
    }

}

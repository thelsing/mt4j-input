package org.mt4j.input;

import javax.swing.*;
import java.awt.*;

public class SwingHitTestInfoProvider implements IHitTestInfoProvider {
    JFrame mainFrame;

    public SwingHitTestInfoProvider(JFrame mainFrame)
    {

        this.mainFrame = mainFrame;
    }

    @Override
    public Component getComponentAt(float x, float y) {
        Point p = new Point();
        p.x = (int)x;
        p.y = (int)y;

        Component topComponent = mainFrame;
        // the assumption is that only one frame is showing
        Frame[] frames = Frame.getFrames();
        for(Frame frame: frames) {
            if(!frame.isShowing())
                continue;

            topComponent = frame;

            Window[] windows = frame.getOwnedWindows();
            for (Window window : windows) {
                if (!window.isShowing())
                    continue;

                if (window instanceof Dialog && ((Dialog) window).isModal() || window.isActive() || window.isAlwaysOnTop()) {
                    topComponent = window;
                    break;
                }
            }
        }

        p.x -= topComponent.getX();
        p.y -= topComponent.getY();

        Component c = SwingUtilities.getDeepestComponentAt(topComponent, p.x, p.y);

        return c;
    }

    @Override
    public boolean isBackGroundAt(float x, float y) {
        return false;
    }
}

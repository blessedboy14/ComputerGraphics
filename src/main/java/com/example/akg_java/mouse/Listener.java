package com.example.akg_java.mouse;

import com.example.akg_java.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class Listener extends MouseAdapter {
    public Point lastPoint = null;
    private final float sensitivity = 0.5f;
    private final float wheel_sensitivity = 0.8f;

    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastPoint = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastPoint != null) {
            Point pt = e.getPoint();
            int dx = lastPoint.x - pt.x;
            int dy = lastPoint.y - pt.y;
            double angle_x = dx * sensitivity;
            double angle_y = dy * sensitivity;
            JFrame obj = (JFrame)e.getSource();
            App t = (App)obj.getContentPane().getComponents()[0];
            t.onMouseDragged(angle_y, angle_x);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double t = e.getScrollAmount();
        int k = e.getWheelRotation();
        ((App)((JFrame)e.getSource()).getContentPane().getComponents()[0]).onWheelTouched(t*k * wheel_sensitivity);
    }
}

package com.example.akg_java.mouse;

import com.example.akg_java.App;
import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Timer;
import java.util.TimerTask;

public class Listener extends MouseAdapter {
    public Point lastPoint = null;
    private final float sensitivity = 0.01f;
    private final float controllerSens = 0.1f;
    private final float deadZone = 0.09f;
    private XInputAxes ax;
    private double stickX;
    private double stickY;
    private App myApp;
    double cameraPhi = Math.PI / 2;
    double cameraTheta = Math.PI / 2;
    private final float wheel_sensitivity = 0.8f;

    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = e.getPoint();
    }

    private void timerTask() {
        XInputDevice device;
        try {
            device = XInputDevice.getAllDevices()[0];
        } catch (XInputNotLoadedException e) {
            throw new RuntimeException(e);
        }
        if (device!= null && device.poll()) {
            XInputComponents components = device.getComponents();
            XInputButtons buttons = components.getButtons();
            XInputAxes axes = components.getAxes();
            ax = axes;
            stickX = Math.abs(axes.rx) < deadZone ? 0 : -axes.rx;
            stickY = Math.abs(axes.ry) < deadZone ? 0 : axes.ry;
            cameraPhi += stickX * controllerSens;//yaw
            cameraTheta += stickY * controllerSens;//pitch
            myApp.onMouseDragged(cameraTheta, cameraPhi);
            if (buttons.x) {
                myApp.onWheelTouched(-3);
            } else if (buttons.b) {
                myApp.onWheelTouched(3);
            }
        }
    }

    public Listener(App myApp) {
        this.myApp = myApp;
        Thread xBoxThread = new Thread(() -> {
            Timer worker = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    timerTask();
                }
            };
            worker.scheduleAtFixedRate(task, 0, 30);
        });
        xBoxThread.start();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastPoint != null) {
            Point pt = e.getPoint();
            double dx = lastPoint.x - pt.x;
            double dy = pt.y - lastPoint.y;
            lastPoint.x = pt.x;
            lastPoint.y  = pt.y;
            double angle_x = dx * sensitivity;
            double angle_y = dy * sensitivity;
            cameraPhi += angle_x;
            cameraTheta += angle_y;
            JFrame obj = (JFrame)e.getSource();
            App t = (App)obj.getContentPane().getComponents()[0];
            t.onMouseDragged(cameraPhi, cameraTheta);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double t = e.getScrollAmount();
        int k = e.getWheelRotation();
        ((App)((JFrame)e.getSource()).getContentPane().getComponents()[0]).onWheelTouched(t*k * wheel_sensitivity);
    }
}

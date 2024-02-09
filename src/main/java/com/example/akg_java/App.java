package com.example.akg_java;

import com.example.akg_java.EngineUtility.Camera;
import com.example.akg_java.EngineUtility.Mesh;
import com.example.akg_java.EngineUtility.ZBuffer;
import com.example.akg_java.math.*;
import com.example.akg_java.EngineUtility.Graphics;
import com.example.akg_java.mouse.Listener;
import com.sun.prism.paint.Color;
import javafx.scene.PerspectiveCamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class App extends JComponent implements ActionListener {
    private static final int HEIGHT = 800;
    private static final int HEADER = 40;
    private static final int WIDTH = 1600;
    private static final String fileName = "D:/LABS/AKG/AKG_LAB1_OBJ_PARSER/porshe.obj";
    private static JFrame frame;
    private Robot inputs;
    private long prev;
    private Graphics graphics;
    private Mesh input;
/*    private Timer timer = new Timer(1, this);*/
    private double angle = 180;
    private Camera camera = new Camera(Matr4x4.getCameraMatrix(Matr4x4.camera(Camera.CAMERA_DISTANCE)));
    public static void main(String[] args) throws AWTException, IOException {
        BufferedImage buffer = new BufferedImage(WIDTH + 1, HEIGHT + 1, BufferedImage.TYPE_INT_RGB);
        App app = new App();
        Listener l = new Listener();
        frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBounds(0, 0, WIDTH, HEIGHT + HEADER);
        frame.add(app);
        frame.addMouseMotionListener(l);
        frame.addMouseListener(l);
        frame.addMouseWheelListener(l);
        app.init(buffer);
    }

    private void init(BufferedImage buffer) throws AWTException, IOException {
        inputs = new Robot();
        graphics = new Graphics(buffer, WIDTH, HEIGHT);
        prev = System.currentTimeMillis();
        input = Mesh.loadMesh(App.fileName);
        repaint();
/*        timer.start();*/
    }

    public Matr4x4 screenProjection =
            Matr4x4.projection(90, (double) HEIGHT / WIDTH, 0.1f, 1000.0f)
            .multiply(Matr4x4.screen(WIDTH, HEIGHT));

    public Matr4x4 objectPosition = Matr4x4.rotationY(180)
            .multiply(Matr4x4.translation(0, 0, 2));

    public Matr4x4 matr = objectPosition
            .multiply(Matr4x4.getCameraMatrix(Matr4x4.identity()))
            .multiply(screenProjection);
    public ZBuffer buffer = new ZBuffer(WIDTH, HEIGHT);

    private Vec3d cameraPos = new Vec3d(0, 0, 1);
    private Vec3d lightDir = new Vec3d(0, 0, 1);

    @Override
    public void paint(java.awt.Graphics g) {
        if (this.input != null) {
            buffer.drop();
            graphics.clear(Color.BLACK.getIntArgbPre());
/*            angle += (System.currentTimeMillis() - prev) / 1000.0 * 90;*/
            frame.setTitle(String.format("%d fps", (int) (1000 / (System.currentTimeMillis() - prev))));
            prev = System.currentTimeMillis();
            java.awt.Color clr = new java.awt.Color(255, 153, 153);
            Matr4x4 t = Matr4x4.rotationY(angle)
                    .multiply(Matr4x4.translation(0, 0, 0))
                    .multiply(camera.getCameraView());
            Matr4x4 test =  Matr4x4.rotationY(angle)
                    .multiply(Matr4x4.translation(0, 0, 0))
                    .multiply(camera.getCameraView())
                    .multiply(Matr4x4.projection(90, (double) HEIGHT / WIDTH, 0.1f, 1000.0f))
                    .multiply(Matr4x4.screen(WIDTH, HEIGHT));
            for (Triangle triangle: input.getTris()) {
                Triangle tri = triangle.multiplyMatrix(t);
                Vec3d[] v = tri.getPoints();
                Vec3d normal = v[2].subtract(v[0]).Cross(v[1].subtract(v[0]));
                normal.normalize();
                double similar = normal.Dot(cameraPos);
                double intense = normal.Dot(lightDir);
                if (similar >= 0) {
                    if (intense >= 0) {
                        graphics.rasterBarycentric(triangle.multiplyMatrix(test), buffer,
                                WIDTH, HEIGHT, new java.awt.Color((float) (clr.getRed() * intense) / 255,
                                        (float) (clr.getGreen() * intense) / 255,
                                        (float) (clr.getBlue() * intense) / 255).getRGB());
                    }
                }
            }
            g.drawImage(graphics.getBuffer(), 0, 0, null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
/*        repaint();*/
    }

    public void onMouseDragged(double pitch, double yaw) {
        camera.rotate(pitch, yaw);
        repaint();
    }

    public void onWheelTouched(double val) {
        Camera.CAMERA_DISTANCE += val;
        if (Camera.CAMERA_DISTANCE <= 0.2f) {
            Camera.CAMERA_DISTANCE = 0.7f;
        }
        camera.updateDistance();
        repaint();
    }
}
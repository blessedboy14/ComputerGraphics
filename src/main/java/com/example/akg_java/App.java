package com.example.akg_java;

import com.example.akg_java.math.*;
import com.example.akg_java.math.Graphics;
import com.sun.prism.paint.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class App extends JComponent implements ActionListener, KeyListener {
    private static final int HEIGHT = 800;
    private static final int HEADER = 40;
    private static final int WIDTH = 1600;
    private static final String fileName = "D:/LABS/AKG/AKG_LAB1_OBJ_PARSER/lord.obj";
    private static final java.awt.Color paintColor = new java.awt.Color(255, 153, 153);
    private static JFrame frame;
    private Robot inputs;
    private long prev;
    private Graphics graphics;
    private Mesh input;
    private Camera camera;
    /*    private Timer timer = new Timer(1, this);*/
    private double angle = 0;

    public static void main(String[] args) throws AWTException, IOException {
        BufferedImage buffer = new BufferedImage(WIDTH + 1, HEIGHT + 1, BufferedImage.TYPE_INT_RGB);
        App app = new App();
        frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBounds(0, 0, WIDTH, HEIGHT + HEADER);
        frame.add(app);
        frame.addKeyListener(app);
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

/*    public Matr4x4 matr = Matr4x4.rotationY(0)
            .multiply(Matr4x4.translation(0, 0, 400))
            .multiply(Matr4x4.getCameraMatrix(Matr4x4.identity()))
            .multiply(Matr4x4.projection(90, (double) HEIGHT / WIDTH, 0.1, 1000.0f))
            .multiply(Matr4x4.screen(WIDTH, HEIGHT));*/

    public Matr4x4 screenProjection =
            Matr4x4.projection(90, (double) HEIGHT / WIDTH, 0.1f, 10.0f)
                    .multiply(Matr4x4.screen(WIDTH, HEIGHT));

    public Matr4x4 objectPosition = Matr4x4.rotationY(180)
            .multiply(Matr4x4.translation(0, -70, 110));

    public Matr4x4 matr = objectPosition
            .multiply(Matr4x4.getCameraMatrix(Matr4x4.identity()))
            .multiply(screenProjection);

    public ZBuffer buffer = new ZBuffer(WIDTH, HEIGHT);

    private Vec3d cameraVec = new Vec3d(0, 0, 0);
    private Vec3d lightDir = new Vec3d(0, 0.6, 0.8);

    @Override
    public void paint(java.awt.Graphics g) {
        if (this.input != null) {
            buffer.drop();
            graphics.clear(Color.BLACK.getIntArgbPre());
            /*            angle += (System.currentTimeMillis() - prev) / 1000.0 * 90;*/
            frame.setTitle(String.format("%d fps", (int) (1000 / (System.currentTimeMillis() - prev))));
            prev = System.currentTimeMillis();
/*            if (angle > 360) {
                angle -= 360;
            }*/

            for (Triangle triangle : input.getTris()) {
                Triangle tri = triangle.multiplyMatrix(objectPosition);
                Vec3d[] v = tri.getPoints();
                Vec3d normal = v[2].subtract(v[0]).Cross(v[1].subtract(v[0]));
                normal.normalize();
                //double similar = normal.Dot(cameraVec);
                double intense = normal.Dot(lightDir);
                /*                if (similar >= 0) {*/
                if (intense >= 0) {
                    graphics.rasterBarycentric(triangle.multiplyMatrix(matr), buffer,
                            WIDTH, HEIGHT, new java.awt.Color((float) (paintColor.getRed() * intense) / 255,
                                    (float) (paintColor.getGreen() * intense) / 255,
                                    (float) (paintColor.getBlue() * intense) / 255,
                                    1).getRGB());
                    /*                        graphics.drawTriangle(triangle.multiplyMatrix(matr), Color.GREEN.getIntArgbPre());*/
/*                graphics.rasterBarycentric(triangle.multiplyMatrix(matr), buffer,
                        WIDTH, HEIGHT, Color.GREEN.getIntArgbPre());*/
/*                        graphics.scanlineRasterization(triangle.multiplyMatrix(matr),
                                new java.awt.Color((float) (clr.getRed() * intense) / 255,
                                (float) (clr.getGreen() * intense) / 255,
                                (float) (clr.getBlue() * intense) / 255,
                                1).getRGB());*/
/*                        graphics.triangle(triangle.multiplyMatrix(matr), buffer,
                                new java.awt.Color((float) (clr.getRed() * intense) / 255,
                                        (float) (clr.getGreen() * intense) / 255,
                                        (float) (clr.getBlue() * intense) / 255,
                                        1).getRGB());*/
                }
                /*                }*/
            }
            g.drawImage(graphics.getBuffer(), 0, 0, null);
            /*                        graphics.drawTriangle(triangle.multiplyMatrix(model)
                                ,Color.GREEN.getIntArgbPre());*/
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
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
    private static final String fileName = "D:/LABS/AKG/AKG_LAB1_OBJ_PARSER/head.obj";
    private static JFrame frame;
    private Robot inputs;
    private long prev;
    private Graphics graphics;
    private Mesh input;
    private Timer timer = new Timer(1, this);
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
        timer.start();
    }

    private Vec3d lightDir = new Vec3d(0, 0, -1);

    public Matr4x4 matr = Matr4x4.rotationY(180)
            .multiply(Matr4x4.translation(0, 0, 1.5))
            .multiply(Matr4x4.getCameraMatrix(Matr4x4.identity()))
            .multiply(Matr4x4.projection(90, (double) HEIGHT / WIDTH, 0.1, 1000.0f))
            .multiply(Matr4x4.screen(WIDTH, HEIGHT));

    public ZBuffer buffer = new ZBuffer(WIDTH, HEIGHT);

    private Vec3d camera = new Vec3d(0, 0, -1);

    @Override
    public void paint(java.awt.Graphics g) {
        if (this.input != null) {
            buffer.drop();
/*            angle += (System.currentTimeMillis() - prev) / 1000.0 * 90;*/
            frame.setTitle(String.format("%d fps", (int) (1000 / (System.currentTimeMillis() - prev))));
            prev = System.currentTimeMillis();
            if (angle > 360) {
                angle -= 360;
            }
            java.awt.Color clr = new java.awt.Color(0, 255, 0);
            graphics.clear(Color.BLACK.getIntArgbPre());
/*            Matr4x4 model = Matr4x4.rotationY(angle)
                    .multiply(matr);*/
            for (Triangle triangle: input.getTris()) {
                Vec3d[] v = triangle.getPoints();
                Vec3d normal = v[2].subtract(v[0]).Cross(v[1].subtract(v[0]));
                normal.normalize();
                double test = normal.Dot(camera);
                double intense = normal.Dot(lightDir);
                if (test < 0) {

                } else {
                    if (intense > 0) {
                        graphics.rasterBarycentric(triangle.multiplyMatrix(matr), buffer,
                                WIDTH, HEIGHT, new java.awt.Color((float) (clr.getRed() * intense) / 255,
                                        (float) (clr.getGreen() * intense) / 255,
                                        (float) (clr.getBlue() * intense) / 255,
                                        1).getRGB());
                    }
/*                    graphics.drawTriangle(triangle.multiplyMatrix(model)
                            ,new java.awt.Color((float) (clr.getRed() * intense) / 255,
                                    (float) (clr.getGreen() * intense) / 255,
                                    (float) (clr.getBlue() * intense) / 255,
                                    1).getRGB());*/
                }
/*                if (intense > 0) {
                    graphics.rasterize(triangle.multiplyMatrix(model), new java.awt.Color((float) (clr.getRed() * intense) / 255,
                            (float) (clr.getGreen() * intense) / 255,
                            (float) (clr.getBlue() * intense) / 255,
                            1).getRGB());
                }*/
            }
            g.drawImage(graphics.getBuffer(), 0, 0, null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
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
package com.example.akg_java;

import com.example.akg_java.EngineUtility.Camera;
import com.example.akg_java.EngineUtility.Graphics;
import com.example.akg_java.EngineUtility.Mesh;
import com.example.akg_java.EngineUtility.ZBuffer;
import com.example.akg_java.math.Matr4x4;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;
import com.example.akg_java.mouse.Listener;
import com.sun.prism.paint.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class App extends JComponent {
    private static final int HEIGHT = 800;
    private static final int HEADER = 40;
    private static final int WIDTH = 1600;
    private static final String fileName = "D:/LABS/AKG/AKG_LAB1_OBJ_PARSER/objs/head.obj";
    private static JFrame frame;
    private long prev;
    private Graphics graphics;
    public ZBuffer zBuffer = new ZBuffer(WIDTH, HEIGHT);
    private Mesh input;
    private final Camera camera = new Camera(Matr4x4.getCameraMatrix(Matr4x4.camera(Camera.CAMERA_DISTANCE)));
    public static void main(String[] args) throws IOException {
        BufferedImage buffer = new BufferedImage(WIDTH + 1, HEIGHT + 1, BufferedImage.TYPE_INT_RGB);
        App app = new App();
        Listener l = new Listener(app);
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

    private final String diff_path = "examples/Gwyn Lord of Cinder/a.tga";
    private final String n_path = "examples/Gwyn Lord of Cinder/a_n.tga";
    private final String s_path = "examples/Gwyn Lord of Cinder/a_s.tga";

    private BufferedImage head_diffuse;
    private BufferedImage head_normals;
    private BufferedImage head_specular;

    private void init(BufferedImage buffer) throws IOException {
        graphics = new Graphics(buffer, WIDTH, HEIGHT, zBuffer, camera);
        prev = System.currentTimeMillis();
        input = Mesh.loadMesh(App.fileName);
        camera.rotateCamera(Math.PI / 2, Math.PI / 2);
        head_diffuse = tryToReadTGA(diff_path);
        head_normals = tryToReadTGA(n_path);
        head_specular = tryToReadTGA(s_path);
        repaint();
    }

    private BufferedImage tryToReadTGA(String path) throws IOException {
        File tgaFile = new File(path);
        return ImageIO.read(tgaFile);
    }

    private Vec3d cameraPos = new Vec3d(0, 0, 1).toNormal();
    private Vec3d lightDir = new Vec3d(1, 0, 1).toNormal();
    private final java.awt.Color clr = new java.awt.Color(227,198,240);

    private boolean isCentred = false;

    @Override
    public void paint(java.awt.Graphics g) {
        if (this.input != null) {
            zBuffer.drop();
            graphics.clear(Color.BLACK.getIntArgbPre());
            frame.setTitle(String.format("%d fps", (int) (1000 / (System.currentTimeMillis() - prev))));
            prev = System.currentTimeMillis();
            Matr4x4 resultMatrix = camera.getCameraView()
                    .multiply(Matr4x4.projection(90, (double) HEIGHT / WIDTH, 0.1f, 1000.0f))
                    .multiply(Matr4x4.screen(WIDTH, HEIGHT));
            lightDir = camera.getEye().subtract(camera.getTarget()).toNormal();
            cameraPos = camera.getEye().subtract(camera.getTarget()).toNormal();
            Vec3d centerVec = new Vec3d(0, 0, 0);
            long i = 0;
            for (Triangle triangle: input.getTris()) {
                Vec3d triNorm = triangle.getNormal().grade(-1);
                if (!(cameraPos.Dot(triNorm) < 0)) {
                    if (!isCentred) {
                        i++;
                        Vec3d[] v = triangle.multiplyMatrix(camera.getCameraView()).getPoints();
                        centerVec = centerVec.add(v[0]).add(v[1]).add(v[2]);
                    }
/*                    graphics.rasterize(triangle, resultMatrix, clr, lightDir);*/
                    graphics.tryToMakeDiffuseMap(triangle, resultMatrix, head_diffuse, head_normals, head_specular,
                            clr, lightDir);
                }
/*                graphics.drawTriangle(triangle.multiplyMatrix(resultMatrix), clr.getRGB());*/
            }
            if (!isCentred) {
                centerVec = centerVec.grade(1.0f / i / 3);
                camera.setTarget(new Vec3d(0, centerVec.y, 0));
                camera.new_y = centerVec.y;
                isCentred = true;
            }
            g.drawImage(graphics.getBuffer(), 0, 0, null);
        }
    }

    private Vec3d calculateAvg(List<Triangle> tris, Matr4x4 matrix ) {
        Vec3d center = new Vec3d(0, 0, 0);
        for (Triangle tri: tris) {
            Vec3d[] v = tri.multiplyMatrix(matrix).getPoints();
            center = center.add(v[0]).add(v[1]).add(v[2]);
        }
        return center;
    }

    public void onMouseDragged(double theta, double phi) {
        camera.rotateCamera(theta, phi);
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
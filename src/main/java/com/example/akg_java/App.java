package com.example.akg_java;

import com.example.akg_java.EngineUtility.Camera;
import com.example.akg_java.EngineUtility.Graphics;
import com.example.akg_java.EngineUtility.Mesh;
import com.example.akg_java.EngineUtility.ZBuffer;
import com.example.akg_java.math.MathUtility;
import com.example.akg_java.math.Matr4x4;
import com.example.akg_java.math.Triangle;
import com.example.akg_java.math.Vec3d;
import com.example.akg_java.mouse.Listener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class App extends JComponent {
    private static final int HEIGHT = 800;
    private static final int HEADER = 40;
    private static final int WIDTH = 1600;
    private static int IMG_WIDTH = 1600;
    private static int IMG_HEIGHT = 800;
/*    private static final String fileName = "D:/LABS/AKG/AKG_JAVA/examples/untitled.obj";*/
    private static final String fileName = "D:/LABS/AKG/AKG_JAVA/examples/Shovel Knight/shovel_low.obj";
    private static JFrame frame;
    private long prev;
    private Graphics graphics;
    public ZBuffer zBuffer = new ZBuffer(WIDTH, HEIGHT);
    private Mesh input;
    private final Map<String, BufferedImage[]> tagToPaths = new HashMap<>();
    private final Camera camera = new Camera(Matr4x4.getCameraMatrix(Matr4x4.camera(Camera.CAMERA_DISTANCE)));
    private static BufferedImage buffer = new BufferedImage(IMG_WIDTH + 1, IMG_HEIGHT + 1, BufferedImage.TYPE_INT_RGB);
    public static void main(String[] args) throws IOException {
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
        frame.addKeyListener(l);
        app.init();
    }

/*    private void fillTagsMap() throws IOException {
        String base = "examples/Gwyn Lord of Cinder/";
        String temp = "c5370_1";
        String ext = ".png";
        tagToPaths.put("Material__25", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "c5370_7";
        tagToPaths.put("Material__26", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "c5370_4";
        tagToPaths.put("Material__27", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "c5370_5";
        tagToPaths.put("Material__28", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "c5370_2";
        tagToPaths.put("Material__29", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "c5370_6";
        tagToPaths.put("Material__30", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "c5370_3";
        tagToPaths.put("Material__31", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
        temp = "WP_A_0268";
        tagToPaths.put("Material__32", new BufferedImage[]{ImageIO.read(new File(base + temp + ext)),
                ImageIO.read(new File(base + temp + "_n" + ext)),
                ImageIO.read(new File(base + temp + "_s" + ext))});
    }*/

    private void fillTagsMap() throws IOException {
        String base = "examples/girl/";
        String d = "cw_face_00_00_00.bmp";
        String n = "cw_n_none.bmp";
        String s = "cw_s_face_00_00.bmp";
        tagToPaths.put("cw_O_face_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_bura_00_d_00.tga";
        n = "cw_s_bura_00_00.bmp";
        s = "cw_n_none.bmp";
        tagToPaths.put("P_buraN_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_syatu_00_d_00.tga";
        n = "cw_n_syatu_00.bmp";
        s = "cw_s_syatu_00_d.bmp";
        tagToPaths.put("P_ssyatu_00_d_u_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_syatu_00_d_00.tga";
        n = "cw_n_syatu_00.bmp";
        s = "cw_s_syatu_00_d.bmp";
        tagToPaths.put("P_ssbotan_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_syatu_00_d_00.tga";
        n = "cw_n_syatu_00.bmp";
        s = "cw_s_syatu_00_d.bmp";
        tagToPaths.put("P_ssbotan_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_ribon_00.bmp";
        n = "cw_n_ribon_00.bmp";
        s = "cw_s_ribon_00.bmp";
        tagToPaths.put("P_ribonS_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_syatu_00_d_00.tga";
        n = "cw_n_syatu_00.bmp";
        s = "cw_s_syatu_00_d.bmp";
        tagToPaths.put("P_ssyatu_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_pskirt_00_d_00.tga";
        n = "cw_n_none.bmp";
        s = "cw_s_sskirt_00_d_00.bmp";
        tagToPaths.put("P_skirtp_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_pskirt_00_d_00.tga";
        n = "cw_n_none.bmp";
        s = "cw_s_sskirt_00_d_00.bmp";
        tagToPaths.put("P_skirtp_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_pskirt_00_d_00.tga";
        n = "cw_n_none.bmp";
        s = "cw_s_sskirt_00_d_00.bmp";
        tagToPaths.put("P_skirtp_00_d_u_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_highsox_00.bmp";
        n = "cw_n_highsox_00.bmp";
        s = "cw_s_highsox_00.bmp";
        tagToPaths.put("P_soxH_00_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_kutu_lowfa_01.jpg";
        n = "cw_n_none.bmp";
        s = "cw_s_kutu_lowfa_01.bmp";
        tagToPaths.put("P_kutuR_00_d_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_tume_00_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_s_tume_00_00.bmp";
        tagToPaths.put("P_wbody_00_00_4", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_tikubi_00_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_s_tikubi_00_00.bmp";
        tagToPaths.put("P_wbody_00_00_3", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_tikubi_00_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_s_tikubi_00_00.bmp";
        tagToPaths.put("P_wbody_00_00_2", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_body_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_body_s_00_00.bmp";
        tagToPaths.put("P_wbody_00_00_1", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_bodyude_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_s_body_00_01.bmp";
        tagToPaths.put("P_wbody_00_00_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_kmnpb_00_00.tga";
        n = "cw_n_kmnpb_00.bmp";
        s = "cw_s_kmnpb_00.bmp";
        tagToPaths.put("P_kmnpb_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_eyekage_.tga";
        n = "cw_eyekage_.tga";
        s = "cw_eyekage_.tga";
        tagToPaths.put("cw_O_eyekage_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_eyehikari_.tga";
        n = "cw_eyehikari_.tga";
        s = "cw_eyehikari_.tga";
        tagToPaths.put("cw_O_eyehikari_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_eye_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_s_eye_00.bmp";
        tagToPaths.put("cw_O_eye_L_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_eye_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_s_eye_00.bmp";
        tagToPaths.put("cw_O_eye_R_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_hairbase_00_00.tga";
        n = "cw_n_none.bmp";
        s = "cw_t_hairbase_sp.bmp";
        tagToPaths.put("cw_O_hairbase_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_ha_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_ha_00.bmp";
        tagToPaths.put("cw_O_ha_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_t_sita.bmp";
        n = "cw_n_none.bmp";
        s = "cw_t_sitatekari.bmp";
        tagToPaths.put("cw_O_sita_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_mayuge_00_00.tga";
        n = "cw_n_none.bmp";
        s = "cw_s_mayuge_00_00.bmp";
        tagToPaths.put("cw_O_mayuge_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_matuge_00.tga";
        n = "cw_matuge_00.tga";
        s = "cw_matuge_00.tga";
        tagToPaths.put("cw_O_matuge_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_04_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_kiwa_kana_ver00_00_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_03_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_back_kana_ver01_00_1", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_02_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_back_kana_ver01_00_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_01_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_back_kana_ver00_01_2", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_02_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_back_kana_ver00_01_1", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_03_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_back_kana_ver00_01_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_02_00.tga";
        n = "hair_black_dummy.bmp";
        s = "cw_hair_00_02_00_sp.bmp";
        tagToPaths.put("O_hair_back_kana_ver00_00_2", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_03_00.tga";
        n = "hair_black_dummy.bmp";
        s = "cw_hair_00_03_00_sp.bmp";
        tagToPaths.put("O_hair_back_kana_ver00_00_1", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_01_00.tga";
        n = "hair_black_dummy.bmp";
        s = "cw_hair_00_01_00_sp.bmp";
        tagToPaths.put("O_hair_back_kana_ver00_00_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_00_00.tga";
        n = "hair_black_dummy.bmp";
        s = "hair_black_dummy.bmp";
        tagToPaths.put("O_hair_front_kana_ver00_01_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_hair_00_00_00.tga";
        n = "hair_black_dummy.bmp";
        s = "cw_hair_00_00_00_sp.bmp";
        tagToPaths.put("O_hair_front_kana_ver00_00_0", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
        d = "cw_unc_pussy.tga";
        tagToPaths.put("P_wbody_00_00_6", new BufferedImage[]{ImageIO.read(new File(base + d)), null, null});
        d = "cw_t_body_00.bmp";
        n = "cw_n_none.bmp";
        s = "cw_body_s_00_00.bmp";
        tagToPaths.put("P_wbody_00_00_5", new BufferedImage[]{ImageIO.read(new File(base + d)),
                ImageIO.read(new File(base + n)),
                ImageIO.read(new File(base + s))});
    }

/*    private final String diff_path = "examples/Gwyn Lord of Cinder/a.tga";
    private final String n_path = "examples/Gwyn Lord of Cinder/a_n.tga";
    private final String s_path = "examples/Gwyn Lord of Cinder/a_s.tga";*/

    private final String diff_path = "examples/Shovel Knight/shovel_diffuse.png";
    private final String n_path = "examples/Shovel Knight/shovel_normal_map.png";
    private final String s_path = "examples/Shovel Knight/shovel_mrao.png";

    private BufferedImage head_diffuse;
    private BufferedImage head_normals;
    private BufferedImage head_specular;
    private BufferedImage cube;

    private void init() throws IOException {
        graphics = new Graphics(buffer, IMG_WIDTH, IMG_HEIGHT, zBuffer, camera, false);
        prev = System.currentTimeMillis();
/*        cube = tryToReadTGA("examples\\cube.jpg");*/
        input = Mesh.loadMesh(App.fileName);
        camera.rotateCamera(Math.PI / 2, Math.PI / 2);
/*        head_diffuse = tryToReadTGA(diff_path);
        head_normals = tryToReadTGA(n_path);
        head_specular = tryToReadTGA(s_path);*/
/*        fillTagsMap();*/
        repaint();
    }

    private void printPixels(BufferedImage map) {
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                int color = map.getRGB(i, j);
                double red = ((color >> 16) & 0xFF) / 255.0f;
                double green = ((color >> 8) & 0xFF) / 255.0f;
                double blue = (color & 0xFF) / 255.0f;
                System.out.print(red + " " + green + " " + blue + " | ");
            }
            System.out.println();
        }
    }

    private BufferedImage tryToReadTGA(String path) throws IOException {
        File tgaFile = new File(path);
        return ImageIO.read(tgaFile);
    }

    private Vec3d cameraPos = new Vec3d(0, 0, -1).toNormal();
    private Vec3d lightDir = new Vec3d(0, 0, -1).toNormal();
    private java.awt.Color clr2 = new java.awt.Color(255, 143, 0);
    private java.awt.Color clr = new java.awt.Color((float) Math.pow(clr2.getRed() / 255.0f, 2.2f),
            (float)Math.pow(clr2.getGreen() / 255.0f, 2.2f),
            (float)Math.pow(clr2.getBlue() / 255.0f, 2.2f) );

    private java.awt.Color bg = new java.awt.Color(32, 32, 32);
    private boolean isCentred = false;
    @Override
    public void paint(java.awt.Graphics g) {
        if (this.input != null) {
            prev = System.currentTimeMillis();
            zBuffer.drop();
            graphics.clear(bg.getRGB());
            Matr4x4 resultMatrix = camera.getCameraView()
                .multiply(Matr4x4.projection(90, (double) IMG_HEIGHT / IMG_WIDTH, 1f, 1000.0f))
                    .multiply(Matr4x4.screen(IMG_WIDTH, IMG_HEIGHT));
            Vec3d centerVec = new Vec3d(0, 0, 0);
            long i = 0;
            for (Triangle triangle: input.getTris()) {
                Vec3d triNorm = triangle.getNormal().grade(-1);
/*                lightDir = camera.getEye().subtract(triangle.getPoints()[0]).toNormal();*/
                cameraPos = camera.getEye().subtract(triangle.getPoints()[0]).toNormal();
                if (!(cameraPos.Dot(triNorm) <= 0.0f)) {
                    if (!isCentred) {
                        i++;
                        Vec3d[] v = triangle.multiplyMatrix(camera.getCameraView()).getPoints();
                        centerVec = centerVec.add(v[0]).add(v[1]).add(v[2]);
                    }
                    graphics.rasterize(triangle, resultMatrix, clr, lightDir);
/*                    graphics.textureTriangle(triangle, resultMatrix,
                            new BufferedImage[] {head_diffuse, head_normals, head_specular},
                            lightDir
                    );*/
/*                    if (!triangle.getTag().isEmpty() && !Objects.equals(triangle.getTag(), "P_ssyatu_00_d_0") &&
                            !Objects.equals(triangle.getTag(), "P_ssyatu_00_d_u_0") && !Objects.equals(triangle.getTag(), "P_buraN_d_0")) {
                        if (!triangle.getTag().isEmpty() && tagToPaths.containsKey(triangle.getTag())) {
                            graphics.textureTriangle(triangle, resultMatrix, tagToPaths.get(triangle.getTag()), lightDir);
*//*                            graphics.tryToApplyMultiple(tagToPaths.get(triangle.getTag()), triangle, resultMatrix, lightDir, clr);*//*
                        }
                    }*/
                }
            }
            if (!isCentred) {
                centerVec = centerVec.grade(1.0f / i / 3);
                centerVec.y -= centerVec.y / 4;
                camera.setTarget(new Vec3d(centerVec.x, centerVec.y, 0));
                camera.new_y = centerVec.y;
                isCentred = true;
            }
/*            drawLightSources(resultMatrix);*/
            g.drawImage(graphics.getBuffer(), 0, 0, WIDTH + 1, HEIGHT + 1, null);
            long curr = System.currentTimeMillis() - prev;
            frame.setTitle(String.format("%d millis to draw", curr));
        }
    }

    private void drawLightSources(Matr4x4 matrix) {
        List<Triangle> tris = MathUtility.generateSphereMesh(.3f, lightDir, 12, 24);
        for (Triangle tri: tris) {
            graphics.bulbRaster(tri, matrix,
                    Color.WHITE);
        }
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

    public void lightMoving(double xMove, double yMove, double zMove) {
        lightDir.x += xMove;
        lightDir.y += yMove;
        lightDir.z += zMove;
        repaint();
    }

    public void changeImageSizes(int val) {
        IMG_WIDTH = val > 0 ? IMG_WIDTH * 2: IMG_WIDTH / 2;
        IMG_HEIGHT = val > 0 ? IMG_HEIGHT * 2: IMG_HEIGHT / 2;
        zBuffer = new ZBuffer(IMG_WIDTH, IMG_HEIGHT);
        graphics.changeBufferParams(new BufferedImage(IMG_WIDTH + 1, IMG_HEIGHT + 1, BufferedImage.TYPE_INT_RGB),
                IMG_WIDTH, IMG_HEIGHT, zBuffer);
        repaint();
    }

}

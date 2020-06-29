package com.hui.pand.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * @author daihui
 * @date 2020/5/20 13:59
 */
public class CaptchaUtil {

    /*
    static char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
            'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
    */

    static char[] chars = {'A', 'B', 'C', 'E', 'F', 'G', 'H',
            'K', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static Random random = new Random();

    /**
     * 生成一个位数为count的随机验证码
     *
     * @param count
     * @return
     */
    public static String getCaptcha(int count) {
        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < count; i++) {
            char c = chars[current().nextInt(chars.length)];// 随机选取一个字母或数字
            captcha.append(c);
        }
        return captcha.toString();
    }

    /**
     * 为一个验证码生成一个图片
     * 特性： - 颜色随机 - 上下位置随机 - 左右位置随机，但字符之间不会重叠 - 左右随机旋转一个角度 - 避免字符出界 -
     * 随机颜色的小字符做背景干扰 - 根据字符大小自动调整图片大小、自动计算干扰字符的个数
     *
     * @param captcha
     * @return
     */
    public static BufferedImage getCaptchaImg(String captcha) {
        ThreadLocalRandom r = current();
        Random rand = new Random();
        int verifySize = captcha.length();
        int count = captcha.length();
        int fontSize = 28; // code的字体大小
        int fontMargin = fontSize / 2; // 字符间隔
        int width = fontSize * 6; // 图片长度
        int height = (int) (fontSize * 2); // 图片高度，根据字体大小自动调整；调整这个系数可以调整字体占图片的比例
        int avgWidth = width / count; // 字符平均占位宽度
        int maxDegree = 50; // 最大旋转度数

        // 背景颜色
        Color bkColor = Color.WHITE;
        // 验证码的颜色
        Color[] catchaColor = {Color.MAGENTA, Color.BLACK, Color.BLUE,
                Color.CYAN, Color.GREEN, Color.ORANGE, Color.PINK};

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 填充底色为灰白
        g.setColor(bkColor);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.GRAY);// 设置边框色
        g.fillRect(0, 0, width, height);

        Color c = getRandColor(200, 250);
        g.setColor(c);// 设置背景色
        g.fillRect(0, 2, width, height - 4);
        // 绘制干扰线
        Random random = new Random();
        g.setColor(getRandColor(160, 200));// 设置线条的颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        shear(g, width, height, c);// 使图片扭曲

        g.setColor(getRandColor(100, 160));
        fontSize = height - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g.setFont(font);
        char[] chars = captcha.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(
                    Math.PI / 4 * rand.nextDouble()
                            * (rand.nextBoolean() ? 1 : -1),
                    (width * 1d / verifySize) * i + fontSize / 2d, height / 2d);
            g.setTransform(affine);
            g.drawChars(chars, i, 1, ((width - 10) / verifySize) * i + 5,
                    height / 2 + fontSize / 2);
        }
        g.dispose();

        return image;
    }

    private static void shearX(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(2);
        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
    }

    private static void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }
        }
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255){
            fc = 255;
        }
        if (bc > 255){
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}

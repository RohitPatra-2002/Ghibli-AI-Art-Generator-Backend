package com.rohitpatra.ghibliapi.config;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {

    private static final int MIN_DIM = 320;
    private static final int MAX_DIM = 1536;

    public static BufferedImage resizeIfNeeded(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // If already within allowed range, return original
        if (width >= MIN_DIM && width <= MAX_DIM && height >= MIN_DIM && height <= MAX_DIM) {
            return originalImage;
        }

        // Scale image proportionally
        double scale = Math.min((double) MAX_DIM / width, (double) MAX_DIM / height);
        int newWidth = Math.max((int)(width * scale), MIN_DIM);
        int newHeight = Math.max((int)(height * scale), MIN_DIM);

        Image tmp = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    public static MultipartFile toMultipartFile(BufferedImage image, String name) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new MockMultipartFile(name, name + ".png", "image/png", new ByteArrayInputStream(baos.toByteArray()));
    }
}


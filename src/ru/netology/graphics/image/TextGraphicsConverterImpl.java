package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class TextGraphicsConverterImpl implements TextGraphicsConverter {

    private int width;
    private int height;
    private double ratio;
    private TextColorSchema schema;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        if (schema == null) {
            schema = new TextColorSchemaImpl();
        }

        BufferedImage img = ImageIO.read(new URL(url));

        if ((double) img.getWidth() / img.getHeight() > ratio && ratio > 0) {
            throw new BadImageSizeException((double) img.getWidth() / img.getHeight(), ratio);
        }

        Image scaledImage;
        BufferedImage bwImg;
        if (width > 0 || height > 0) {
            // TODO
            int newWidth = img.getWidth();
            int newHeight = img.getHeight();

            scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
            bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        } else {
            scaledImage = img.getScaledInstance(img.getWidth(), img.getHeight(), BufferedImage.SCALE_SMOOTH);
            bwImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        }
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();

        StringBuilder result = new StringBuilder();
        for (int h = 0; h < img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                result.append(c);
            }
            result.append('\n');
        }

        return result.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.ratio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}

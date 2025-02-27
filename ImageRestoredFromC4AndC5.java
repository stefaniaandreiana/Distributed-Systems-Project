package rmi_package;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageRestoredFromC4AndC5 {

    public static void main(String[] args) {
        try {
            BufferedImage imagePart1 = ImageIO.read(new File("received_image_zoomed_part1.bmp"));
            BufferedImage imagePart2 = ImageIO.read(new File("received_image_zoomed_part2.bmp"));
            if (imagePart1 == null || imagePart2 == null) {
                System.err.println("Eroare la citirea imaginilor!");
                return;
            }
            int combinedWidth = imagePart1.getWidth() + imagePart2.getWidth(); 
            int combinedHeight = Math.max(imagePart1.getHeight(), imagePart2.getHeight());
            BufferedImage restoredImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = restoredImage.createGraphics();
            g2d.drawImage(imagePart1, 0, 0, null);
            g2d.drawImage(imagePart2, imagePart1.getWidth(), 0, null);
            g2d.dispose();
            ImageIO.write(restoredImage, "bmp", new File("restored_image.bmp"));
            System.out.println("Imaginea a fost restaurată și salvată!");

        } catch (IOException e) {
            System.err.println("Eroare la restaurarea imaginii: " + e.getMessage());
        }
    }
}

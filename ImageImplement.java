package rmi_package;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageImplement extends UnicastRemoteObject implements ImageProcessor {

    protected ImageImplement() throws RemoteException {
        super();
    }

    @Override
    public void processImage(String base64Image) throws RemoteException {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream in = new ByteArrayInputStream(imageData);
            BufferedImage originalImage = ImageIO.read(in);
            BufferedImage zoomedImage = applyZoom(originalImage, 2.0);
            try (FileOutputStream fos = new FileOutputStream("received_image_zoomed_part1.bmp")) {
                ImageIO.write(zoomedImage, "bmp", fos);
                System.out.println("Imaginea a fost procesată și salvată cu zoom aplicat!");
            }

        } catch (IOException e) {
            System.err.println("Eroare la procesarea imaginii: " + e.getMessage());
        }
    }
    private BufferedImage applyZoom(BufferedImage originalImage, double zoomFactor) {
        int newWidth = (int) (originalImage.getWidth() * zoomFactor);
        int newHeight = (int) (originalImage.getHeight() * zoomFactor);
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage zoomedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = zoomedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return zoomedImage;
    }
}

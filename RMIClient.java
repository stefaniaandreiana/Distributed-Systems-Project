package rmi_package;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RMIClient {

    public static void main(String[] args) {
        try {
            String imagePath = "C:\\Users\\Stefania\\Documents\\Imagine.bmp"; 

            File imageFile = new File(imagePath);
            byte[] imageData = new byte[(int) imageFile.length()];

            try (FileInputStream fis = new FileInputStream(imageFile)) {
                fis.read(imageData);
            }

            int middle = imageData.length / 2;
            byte[] firstHalf = new byte[middle];
            byte[] secondHalf = new byte[imageData.length - middle];

            System.arraycopy(imageData, 0, firstHalf, 0, middle);
            System.arraycopy(imageData, middle, secondHalf, 0, secondHalf.length);

            String firstHalfBase64 = Base64.getEncoder().encodeToString(firstHalf);
            String secondHalfBase64 = Base64.getEncoder().encodeToString(secondHalf);

            LocateRegistry.getRegistry("localhost", 1099);
            ImageProcessor imageProcessorC4 = (ImageProcessor) Naming.lookup("rmi://localhost:1099/ImageProcessor");
            imageProcessorC4.processImage(firstHalfBase64);
            System.out.println("Prima jumătate a imaginii a fost trimisă către C4!");

            LocateRegistry.getRegistry("localhost", 1100);
            ImageProcessor imageProcessorC5 = (ImageProcessor) Naming.lookup("rmi://localhost:1100/ImageProcessor");
            imageProcessorC5.processImage(secondHalfBase64);
            System.out.println("A doua jumătate a imaginii a fost trimisă către C5!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


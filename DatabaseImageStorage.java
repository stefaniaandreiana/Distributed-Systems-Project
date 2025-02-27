package apachetomeejms;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseImageStorage {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/image_db";
    private static final String DB_USER = "student";
    private static final String DB_PASSWORD = "password_stud";

    public void saveImageToDatabase(String imagePath, String description) {
        byte[] imageBytes = readImageFile(imagePath);
        if (imageBytes != null) {
            File imageFile = new File(imagePath);
            String fileName = imageFile.getName();
            String fileType = getFileType("image/bmp");
            long fileSize = imageFile.length();
            String sql = "INSERT INTO pictures (file_name, file_type, file_size, image, description) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, fileName);
                stmt.setString(2, fileType);
                stmt.setLong(3, fileSize);
                stmt.setBytes(4, imageBytes);
                stmt.setString(5, description);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Imaginea a fost salvată cu succes în baza de date!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Eroare la citirea imaginii!");
        }
    }
    private byte[] readImageFile(String imagePath) {
        try {
            return Files.readAllBytes(Paths.get(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getFileType(String imagePath) {
        return "image/bmp"; 
    }


    public static void main(String[] args) {
        DatabaseImageStorage storage = new DatabaseImageStorage();
        String imagePath = "C:\\Users\\Stefania\\Documents\\Imagine/bmp";
        String description = "Imagine procesată pentru aplicație";
        storage.saveImageToDatabase(imagePath, description);
    }
}

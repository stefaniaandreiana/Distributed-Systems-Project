package rmi_package;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ImageServer {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("Serverul RMI este initializat la portul 1099");
            ImageImplement imageImplement = new ImageImplement();
            Naming.rebind("rmi://localhost:1099/ImageProcessor", imageImplement);
            System.out.println("Serverul c04 este acum disponibil si asteapta conexiuni...");
        } catch (Exception e) {
            System.err.println("Eroare la înregistrarea serverului RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


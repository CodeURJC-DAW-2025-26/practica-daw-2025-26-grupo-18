package es.codeurjc.scam_g18.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.Image;

@Service
public class ImageService {

    @Transactional
    // Convierte un archivo subido en una entidad Image persistible.
    public Image saveImage(MultipartFile file) throws IOException, SQLException {
        if (file.isEmpty()) {
            return null;
        }
        Blob blob = new SerialBlob(file.getBytes());
        Image image = new Image();
        image.setData(blob);
        return image;
    }

    @Transactional
    // Devuelve la imagen en formato data URI o una imagen por defecto.
    public String getConnectionImage(Image image) {
        if (image != null && image.getData() != null) {
            try {
                Blob blob = image.getData();
                int blobLength = (int) blob.length();
                byte[] bytes = blob.getBytes(1, blobLength);
                String base64 = Base64.getEncoder().encodeToString(bytes);
                return "data:image/jpeg;base64," + base64;
            } catch (SQLException e) {
                e.printStackTrace();
                return "/img/default_img.png";
            }
        }
        return "/img/default_img.png";
    }

    @Transactional
    // Carga una imagen desde ruta local y la transforma en entidad Image.
    public Image saveImage(String path) throws IOException, SQLException {
        java.nio.file.Path file = java.nio.file.Paths.get("src/main/resources/static" + path);
        if (!java.nio.file.Files.exists(file)) {
            return null;
        }
        byte[] bytes = java.nio.file.Files.readAllBytes(file);
        Blob blob = new SerialBlob(bytes);
        Image image = new Image();
        image.setData(blob);
        return image;
    }

}

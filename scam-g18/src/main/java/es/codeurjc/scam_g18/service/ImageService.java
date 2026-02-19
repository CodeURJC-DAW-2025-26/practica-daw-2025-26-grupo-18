package es.codeurjc.scam_g18.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.Image;
import es.codeurjc.scam_g18.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Transactional
    public Image saveImage(MultipartFile file) throws IOException, SQLException {
        if (file.isEmpty()) {
            return null;
        }
        Blob blob = new SerialBlob(file.getBytes());
        Image image = new Image();
        image.setData(blob);
        return imageRepository.save(image);
    }

    @Transactional
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
                return "/img/descarga.jpg";
            }
        }
        return "/img/descarga.jpg";
    }

    @Transactional
    public Image saveImage(String path) throws IOException, SQLException {
        java.nio.file.Path file = java.nio.file.Paths.get("src/main/resources/static" + path);
        if (!java.nio.file.Files.exists(file)) {
            return null;
        }
        byte[] bytes = java.nio.file.Files.readAllBytes(file);
        Blob blob = new SerialBlob(bytes);
        Image image = new Image();
        image.setData(blob);
        return imageRepository.save(image);
    }

}

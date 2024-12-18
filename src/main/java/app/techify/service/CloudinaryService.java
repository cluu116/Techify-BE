package app.techify.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    @Value("${cloudinary.url}")
    private String cloudinaryUrl;
    public Map<String, String> uploadImage(MultipartFile file) {
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        Transformation transformation = new Transformation();
        transformation
                .width(900)
                .height(800)
                .crop("fill")
                .gravity("auto")
                .fetchFormat("webp")
                .quality("auto");
        Map uploadParams = ObjectUtils.asMap("transformation", transformation);
        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            Map<String, String> result = new HashMap<>();
            result.put("url", (String) data.get("url"));
            result.put("public_id", (String) data.get("public_id"));
            return result;
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail");
        }
    }
}

package com.rohitpatra.ghibliapi.service;

import com.rohitpatra.ghibliapi.client.StabilityAIClient;
import com.rohitpatra.ghibliapi.config.ImageUtil;
import com.rohitpatra.ghibliapi.dto.TextToImageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class GhibliArtService {

    private final StabilityAIClient stabilityAIClient;
    private final String apiKey;

    public GhibliArtService(StabilityAIClient stabilityAIClient, @Value("${stability.api.key}") String apiKey) {
        this.stabilityAIClient = stabilityAIClient;
        this.apiKey = apiKey;
    }

    public byte[] createGhibliArt(MultipartFile image, String prompt) {
        String finalPrompt = prompt+", in the beautiful, detailed anime style of studio ghibli.";
        String engineId = "stable-diffusion-v1-6";
        String stylePreset = "anime";

        return stabilityAIClient.generateImageFromImage(
                "Bearer " + apiKey,
                engineId,
                image,
                finalPrompt,
                stylePreset
        );
    }

    public byte[] createGhibliArtFromText(String prompt, String style) {
        String finalPrompt = prompt+", in the beautiful, detailed anime style of studio ghibli.";
        String engineId = "stable-diffusion-v1-6";
        String stylePreset = style.equals("general") ? "anime" : style.replace("_", "-");

        TextToImageRequest requestPayload = new TextToImageRequest(finalPrompt, stylePreset);

        return stabilityAIClient.generateImageFromText(
                "Bearer " + apiKey,
                engineId,
                requestPayload
        );
    }

    public byte[] createGhibliArtFromImage(MultipartFile initImage,
                                           String prompt) throws IOException {
        String finalPrompt = prompt+", in the beautiful, detailed anime style of studio ghibli.";
        String engineId = "stable-diffusion-v1-6";
        String stylePreset = "anime";

        // Step 1: Read original image
        BufferedImage originalImage = ImageIO.read(initImage.getInputStream());

        // Step 2: Resize if required
        BufferedImage resizedImage = ImageUtil.resizeIfNeeded(originalImage);

        // Step 3: Convert resized image back to MultipartFile
        MultipartFile resizedMultipart = ImageUtil.toMultipartFile(resizedImage, "init_image");

        // Step 4: Call Feign client
        return stabilityAIClient.generateImageFromImage(
                "Bearer "+apiKey,
                engineId,
                resizedMultipart,
                finalPrompt,
                stylePreset
        );
    }
}

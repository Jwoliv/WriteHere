package com.example.WriteHere.controller;

import com.example.WriteHere.model.image.AbstractImage;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class ConvertMethods {
    public <T> Integer changeRating(T element, Integer value, List<T> elementsByUser) {
        if (elementsByUser.contains(element)) return --value;
        return ++value;
    }
    public <T extends AbstractImage> void setImagesToList(
            MultipartFile multipartFile,
            List<T> images,
            T imageEmpty,
            Boolean isPrevious
    ) {
        T image = convertToImage(multipartFile, isPrevious, imageEmpty);
        images.add(image);
    }

    public <T extends AbstractImage> T convertToImage(
            MultipartFile file,
            Boolean isPreviews,
            T image
    ) {
        image.setName(file.getOriginalFilename());
        image.setOriginalName(file.getOriginalFilename());
        image.setSize(file.getSize());
        image.setContentType(file.getContentType());
        image.setIsPreviews(isPreviews);
        try {
            image.setBytes(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
    public String convertTextToMarkDown(String text) {
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Parser parser = Parser.builder().build();
        return renderer.render(parser.parse(text));
    }
}

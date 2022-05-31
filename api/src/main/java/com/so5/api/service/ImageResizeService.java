package com.so5.api.service;

import com.so5.api.config.properties.ImageMagickProperties;
import lombok.RequiredArgsConstructor;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageResizeService {

    private final ImageMagickProperties imageMagickProperties;

    public ByteArrayOutputStream resizeImage(InputStream fis, String extension) throws IOException {

        IMOperation op = new IMOperation();
        op.addImage();
        op.resize(imageMagickProperties.getResize());
        op.addImage();

        try {
            BufferedImage bi = ImageIO.read(fis);
            Stream2BufferedImage s2b = new Stream2BufferedImage();

            ConvertCmd cmd = new ConvertCmd();
            cmd.setSearchPath(imageMagickProperties.getPath());
            cmd.setOutputConsumer(s2b);
            cmd.run(op, bi, extension + ":-");

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(s2b.getImage(), extension, os);
            return os;
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new RuntimeException(e);
        }
    }
}

package usr.afast.image.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.reflections.Reflections;
import usr.afast.image.annotation.ProcessingOf;
import usr.afast.image.annotation.Singleton;
import usr.afast.image.api.ImageProcessor;
import usr.afast.image.enums.AlgorithmType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Singleton
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorsConfiguration {
    private static final Map<AlgorithmType, ImageProcessor> processors;
    private static final String BASE_PACKAGE = "usr.afast.image";

    static {
        processors = new HashMap<>();
        Reflections reflections = new Reflections(BASE_PACKAGE);
        for (Class<? extends ImageProcessor> clazz : reflections.getSubTypesOf(ImageProcessor.class)) {
            if (clazz.isAnnotationPresent(ProcessingOf.class)) {
                ProcessingOf processing = clazz.getAnnotation(ProcessingOf.class);
                AlgorithmType type = processing.value();
                ImageProcessor processor = Configuration.get(clazz);
                if (processors.containsKey(type)) {
                    throw new RuntimeException("Found two ore more implementations for " + type.name());
                }
                processors.put(type, processor);
            }
        }
    }

    public static ImageProcessor getImageProcessor(AlgorithmType algorithmType) {
        return processors.get(algorithmType);
    }
}

package usr.afast.image.algo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usr.afast.image.detector.ObjectDetector;
import usr.afast.image.rest.Response;
import usr.afast.image.rest.ResponseStatus;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static usr.afast.image.util.ImageIO.read;

@RestController
public class DescriptorsHoughMapping {

    private static final String IMAGE_PATH_KEY = "image";
    private static final String OBJECT_PATH_KEY = "obj";

    @RequestMapping("/")
    public String main() {
        return "Use /doMagic mapping";
    }

    @RequestMapping("/doMagic")
    public Response doMagic(@RequestParam Map<String, String> args) {
        if (!args.containsKey(IMAGE_PATH_KEY)) {
            return fail(String.format("Image path is required (add '%s' property)", IMAGE_PATH_KEY));
        }
        final String imagePath = args.get(IMAGE_PATH_KEY);
        if (!imagePathValid(imagePath)) {
            return fail("Image path is invalid");
        }

        if (!args.containsKey(OBJECT_PATH_KEY)) {
            return fail(String.format("Object path is required (add '%s' property)", OBJECT_PATH_KEY));
        }
        final String objectPath = args.get(OBJECT_PATH_KEY);
        if (!imagePathValid(objectPath)) {
            return fail("Object path is invalid");
        }

        final Matrix image = Matrix.of(read(imagePath));
        final Matrix object = Matrix.of(read(objectPath));

        final int hash = Objects.hash(image.hashCode(), object.hashCode());
        double time = Stopwatch.inSeconds(() -> ObjectDetector.detect(imagePath, image, hash, object));

        return ok(String.format("Done in %.3f seconds", time));
    }

    private Response fail(String message) {
        return new Response(ResponseStatus.FAIL, message);
    }


    private Response ok(String message) {
        return new Response(ResponseStatus.OK, message);
    }

    private boolean imagePathValid(String path) {
        File file = new File(path);
        if (file.isDirectory() || !file.canRead()) return false;
        try {
            Matrix matrix = Matrix.of(read(path));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

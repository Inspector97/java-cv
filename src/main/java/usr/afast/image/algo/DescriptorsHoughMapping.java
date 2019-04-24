package usr.afast.image.algo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usr.afast.image.detector.ObjectDetector;
import usr.afast.image.rest.Response;
import usr.afast.image.rest.ResponseStatus;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import static usr.afast.image.util.ImageIO.read;

@RestController
public class DescriptorsHoughMapping {

    private static final String IMAGE_PATH_KEY = "image";
    private static final String OBJECT_PATH_KEY = "obj";
    private static final String SAVE_PATH_KEY = "save";

    @RequestMapping("/")
    public String main() {
        return "Use /doMagic mapping";
    }

    @RequestMapping("/doMagic")
    public Response doMagic(@RequestParam Map<String, String> args) {
        if (!args.containsKey(IMAGE_PATH_KEY)) {
            return fail(String.format("Image path is required (add '%s' property)", IMAGE_PATH_KEY));
        }
        final String[] imagePath = args.get(IMAGE_PATH_KEY).split(";");
        if (!imagePathValid(imagePath)) {
            return fail("Image path is invalid");
        }

        if (!args.containsKey(OBJECT_PATH_KEY)) {
            return fail(String.format("Object path is required (add '%s' property)", OBJECT_PATH_KEY));
        }
        final String[] objectPath = args.get(OBJECT_PATH_KEY).split(";");
        if (!imagePathValid(objectPath)) {
            return fail("Object path is invalid");
        }

        if (!args.containsKey(SAVE_PATH_KEY)) {
            return fail(String.format("Save path is required (add '%s' property)", SAVE_PATH_KEY));
        }
        final String savePath = args.get(SAVE_PATH_KEY);
        if (!folderPathValid(savePath)) {
            return fail("Save path is invalid");
        }

        final Matrix[] images = Arrays.stream(imagePath).map(path -> Matrix.of(read(path))).toArray(Matrix[]::new);
        final Matrix[] objects = Arrays.stream(objectPath).map(path -> Matrix.of(read(path))).toArray(Matrix[]::new);

        final String[] imageNames =
                Arrays.stream(imagePath).map(path -> new File(path).getName()).toArray(String[]::new);
        final String[] objectNames =
                Arrays.stream(objectPath).map(path -> new File(path).getName()).toArray(String[]::new);

        double time = Stopwatch.inSeconds(() -> ObjectDetector.detect(savePath, images, objects, imageNames, objectNames));

        return ok(String.format("Done in %.3f seconds", time));
    }

    private Response fail(String message) {
        return new Response(ResponseStatus.FAIL, message);
    }


    private Response ok(String message) {
        return new Response(ResponseStatus.OK, message);
    }

    private boolean imagePathValid(String... paths) {
        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory() || !file.canRead()) return false;
            try {
                Matrix matrix = Matrix.of(read(path));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean folderPathValid(String path) {
        File file = new File(path);
        return file.isDirectory() && file.canWrite();
    }

}

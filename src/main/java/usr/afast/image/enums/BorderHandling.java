package usr.afast.image.enums;

public enum BorderHandling {
    Wrap, Black, White, Mirror, Copy;

    public static BorderHandling of(String name) {
        try {
            return BorderHandling.valueOf(name);
        } catch (Exception e) {
            System.out.println(String.format("Failed to parse type of border handling from '%s'. Defaulting to Copy",
                                             name));
        }
        return BorderHandling.Copy;
    }
}

package usr.afast.image.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import usr.afast.image.annotation.Singleton;
import usr.afast.image.exception.FailedToInstantiateException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * kind from DI, see no reason in using Spring or anything like it
 */
public class Configuration {
    private Map<Class<?>, Object> singletonObjects;
    private static Configuration configuration;
    private static Reflections reflections = new Reflections("usr.afast.image");

    private Configuration() {
        singletonObjects = new HashMap<>();
    }

    /**
     * Gets static configuration or instantiate and store new one.
     * Traditional usage from singleton pattern.
     *
     * @return the case Configuration instance for all calls
     */
    private static Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    /**
     * Tries to instantiate instance from class itself, then search for all subtypes
     * (like interface implementations) and tries to instantiate all off then
     * until first success.
     * If found class is marked with {@link Singleton} annotation all calls
     * will return the same object.
     *
     * @param clazz class to be instantiated
     * @param <T>   type from this class
     * @return instance from the class or its subtype
     */
    public static <T> T get(Class<T> clazz) {
        Configuration configuration = getConfiguration();
        Object instance = configuration.getObjectOfClass(clazz);
        if (instance == null) {
            for (Class<? extends T> subClass : reflections.getSubTypesOf(clazz)) {
                instance = configuration.getObjectOfClass(subClass);
                if (instance != null) {
                    break;
                }
            }
        }
        if (instance == null) {
            throw new FailedToInstantiateException("No available constructors found for class " + clazz);
        }
        return (T) instance;
    }

    private Object getObjectOfClass(@NotNull Class<?> clazz) {
        if (clazz.isAnnotationPresent(Singleton.class)) {
            return getOrInstantiate(clazz);
        }
        return instantiate(clazz);
    }

    private Object getOrInstantiate(Class<?> clazz) {
        if (contains(clazz)) {
            return getSingleton(clazz);
        }
        putSingleton(clazz, instantiate(clazz));
        return getSingleton(clazz);
    }

    private Object instantiate(@NotNull Class<?> clazz) {
        Object newInstance = null;
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            newInstance = constructor.newInstance();
            constructor.setAccessible(false);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            //
        }
        return newInstance;
    }

    private Object getSingleton(Class<?> clazz) {
        return singletonObjects.get(clazz);
    }

    private void putSingleton(Class<?> clazz, Object instance) {
        singletonObjects.put(clazz, instance);
    }

    @Contract(pure = true)
    private boolean contains(Class<?> clazz) {
        return singletonObjects.containsKey(clazz);
    }
}

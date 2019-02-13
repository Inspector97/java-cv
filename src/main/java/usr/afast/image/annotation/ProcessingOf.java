package usr.afast.image.annotation;

import usr.afast.image.enums.AlgorithmType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessingOf {
    AlgorithmType value();
}

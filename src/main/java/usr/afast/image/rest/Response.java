package usr.afast.image.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Response implements Serializable {
    @NonNull
    private ResponseStatus status;
    @NonNull
    private String message;
    private Object content;
}

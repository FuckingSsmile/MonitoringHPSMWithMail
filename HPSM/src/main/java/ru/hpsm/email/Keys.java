package ru.hpsm.email;

import java.io.Serializable;

public interface Keys<T> extends Serializable {
    T getDefaultValue();
}

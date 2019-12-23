package com.ifritee.develop.jcksum;

/** Наблюдатель за состоянием */
public interface Observer {
    /** Действие при передаче 4-х корректирующих байт */
    void checkCorrectingBytes(int bytes);
}

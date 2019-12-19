package com.ifritee.develop.jcksum;

/** Наблюдатель за состоянием */
public interface Observer {
    /** Действие при передаче 4-х корректирующих байт */
    void CheckCorrectingBytes_v(int Bytes_i);
}

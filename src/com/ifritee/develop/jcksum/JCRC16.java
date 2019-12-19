package com.ifritee.develop.jcksum;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class JCRC16 {
    public int value = 0;
    /** Состояние работы для потоков */
    private AtomicBoolean _ThreadRunFlag_ab;

    public JCRC16() {
        _ThreadRunFlag_ab = new AtomicBoolean();
        reset();
    }

    public void update(byte var1) {
        int var2 = var1;

        for(int var4 = 7; var4 >= 0; --var4) {
            var2 <<= 1;
            int var3 = var2 >>> 8 & 1;
            if ((this.value & '耀') != 0) {
                this.value = (this.value << 1) + var3 ^ 4129;
            } else {
                this.value = (this.value << 1) + var3;
            }
        }
        this.value &= 65535;
    }

    /**
     * Расчет дополтельный 2-х байт
     * @param FinalCRC_i Желаемая КС
     */
    public void CalcPostBytes_v(int FinalCRC_i) {
        Runnable runnable_o = () -> {
            for (short i = -32768; (i < 32767) && (!_ThreadRunFlag_ab.get()); ++i) {
                int tempValue_i = this.value;
                byte[] bytes = ByteBuffer.allocate(2).putShort(i).array();
                update(bytes[0]);
                update(bytes[1]);
                if (value == FinalCRC_i) {
                    Notifier.getInstance().sendCorrectingBytes_pv(i);
                    _ThreadRunFlag_ab.set(true);
                }
                value = tempValue_i;
            }
            _ThreadRunFlag_ab.set(false);
        };
        (new Thread(runnable_o)).start();
    }

    public void reset() {
        this.value = 0;
        _ThreadRunFlag_ab.set(false);
    }

    public void Abort_v() {
        _ThreadRunFlag_ab.set(true);
    }
}
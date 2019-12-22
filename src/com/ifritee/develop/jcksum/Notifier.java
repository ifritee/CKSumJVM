package com.ifritee.develop.jcksum;

import java.util.ArrayList;

public class Notifier {
    /** Реализация singleton Double checked locking & volatile */
    private static volatile Notifier instance;
    public static Notifier getInstance() {
        Notifier LocalNotifier_o = instance;
        if(LocalNotifier_o == null) {
            synchronized (Notifier.class) {
                LocalNotifier_o = instance;
                if(LocalNotifier_o == null) {
                    instance = new Notifier();
                }
            }
        }
        return instance;
    }
    /** Массив наблюдателей */
    private ArrayList<Observer> _Observes_lst;

    /** Конструктор */
    Notifier() {
        _Observes_lst = new ArrayList<>();
    }

    /**
     * Добавляет наблюдателя
     * @param ob объект наблюдателя
     */
    public void addObserver_v(Observer ob) {
        if(!_Observes_lst.contains(ob)) {
            _Observes_lst.add(ob);
        }
    }

    /**
     * Удаляет наблюдателя из списка
     * @param ob объект наблюдателя
     */
    public void rmObserver_v(Observer ob) {
        if(_Observes_lst.contains(ob)) {
            _Observes_lst.remove(ob);
        }
    }

    /**
     * Отправка сообщения на всех слушателей
     * @param Bytes_i Корректирующие байты
     */
    public void sendCorrectingBytes_pv(int Bytes_i) {
        for(Observer observer : _Observes_lst) {
            observer.CheckCorrectingBytes_v(Bytes_i);
        }
    }
}

package com.ifritee.develop.jcksum;

import java.util.ArrayList;

public class Notifier {
    /** Реализация singleton Double checked locking & volatile */
    private static volatile Notifier instance;
    public static Notifier getInstance() {
        Notifier localNotifier = instance;
        if(localNotifier == null) {
            synchronized (Notifier.class) {
                localNotifier = instance;
                if(localNotifier == null) {
                    instance = new Notifier();
                }
            }
        }
        return instance;
    }
    /** Массив наблюдателей */
    final private ArrayList<Observer> OBSERVES;

    /** Конструктор */
    Notifier() {
        OBSERVES = new ArrayList<>();
    }

    /**
     * Добавляет наблюдателя
     * @param observer объект наблюдателя
     */
    public void addObserver(Observer observer) {
        if(!OBSERVES.contains(observer)) {
            OBSERVES.add(observer);
        }
    }

    /**
     * Удаляет наблюдателя из списка
     * @param observer объект наблюдателя
     */
    public void removeObserver(Observer observer) {
        OBSERVES.remove(observer);
    }

    /**
     * Отправка сообщения на всех слушателей
     * @param bytes Корректирующие байты
     */
    public void sendCorrectingBytes(int bytes) {
        for(Observer observer : OBSERVES) {
            observer.checkCorrectingBytes(bytes);
        }
    }
}

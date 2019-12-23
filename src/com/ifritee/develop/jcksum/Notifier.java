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
    private ArrayList<Observer> observesList;

    /** Конструктор */
    Notifier() {
        observesList = new ArrayList<>();
    }

    /**
     * Добавляет наблюдателя
     * @param observer объект наблюдателя
     */
    public void addObserver(Observer observer) {
        if(!observesList.contains(observer)) {
            observesList.add(observer);
        }
    }

    /**
     * Удаляет наблюдателя из списка
     * @param observer объект наблюдателя
     */
    public void rmObserver_v(Observer observer) {
        if(observesList.contains(observer)) {
            observesList.remove(observer);
        }
    }

    /**
     * Отправка сообщения на всех слушателей
     * @param bytes Корректирующие байты
     */
    public void sendCorrectingBytes(int bytes) {
        for(Observer observer : observesList) {
            observer.checkCorrectingBytes(bytes);
        }
    }
}

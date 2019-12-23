package com.ifritee.develop.jcksum;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 * Основное окно для ПО "Подмена J"
 */
public class RootWindow implements Observer {
    private JPanel rootPanel;
    private JComboBox<String> cb_crc_type;
    private JComboBox<String> cb_file_type;
    private JTextField tf_set_file;
    private JButton btn_choose_file;
    private JLabel lb_crc_dec;
    private JTextField tf_crc_dec;
    private JTextField tf_crc_hex;
    private JTextField tf_crc_oct;
    private JButton btn_calculate;
    private JButton btn_cancel;
    private JLabel lb_crc_hex;
    private JLabel lb_crc_oct;
    private JCheckBox chb_inject;
    private JLabel lb_correcting_bytes;
    /** Выбранный файл */
    private File selectedFile;
    /** Объект для рсчета контрольной суммы */
    final private JCKSum JCKSUM;
    /** Объект для рсчета CRC-16 */
    final private JCRC16 JCRC16;
    /** Словарь делителей */
    final HashMap<JTextField, Integer> RADIXES;
    /** Логер */
    public static final Logger LOGGER = Logger.getLogger(RootWindow.class.getName());

    /** Конструктор */
    public RootWindow() {
        Notifier.getInstance().addObserver(this);
        selectedFile = new File("");
        JCKSUM = new JCKSum();
        JCRC16 = new JCRC16();
        RADIXES = new HashMap<>();
        RADIXES.put(tf_crc_dec, 10);
        RADIXES.put(tf_crc_hex, 16);
        RADIXES.put(tf_crc_oct, 8);
        btn_choose_file.addActionListener(e -> { // Действия при выборе файла
            JFileChooser fileChooser = new JFileChooser();
            eraseData();
            btn_calculate.setEnabled(false);
            int ReturnCode_i = fileChooser.showOpenDialog(rootPanel);
            if (ReturnCode_i == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }
            tf_set_file.setText(selectedFile.getName());
            if(("CRC16").equals(cb_crc_type.getSelectedItem())) {
                calculateCRC16();
            } else if (("CRC32-C").equals(cb_crc_type.getSelectedItem())) {
                calculateCRC32();
            } else if (("CKSum").equals(cb_crc_type.getSelectedItem())) {
                calculateCKSum();
            }
        });
        cb_crc_type.addActionListener(e -> { // Действия при выборе типа КС
            if (selectedFile.exists() && selectedFile.canRead()) {
                if(("CRC16").equals(cb_crc_type.getSelectedItem())) {
                    calculateCRC16();
                } else if (("CRC32-C").equals(cb_crc_type.getSelectedItem())) {
                    calculateCRC32();
                } else if (("CKSum").equals(cb_crc_type.getSelectedItem())) {
                    calculateCKSum();
                }
            }
        });
        btn_calculate.addActionListener(e -> {  // Действия при расчете
            if (selectedFile.exists() && selectedFile.canRead()) {
                try {
                    long parseLong = Long.parseLong(tf_crc_dec.getText());
                    if (("CRC16").equals(cb_crc_type.getSelectedItem())) {
                        postBytesCalcCRC16(parseLong);
                    } else if (("CRC32-C").equals(cb_crc_type.getSelectedItem())) {
                        postBytesCalcCRC32(parseLong);
                    } else if (("CKSum").equals(cb_crc_type.getSelectedItem())) {
                        postBytesCalcCKSum(parseLong);
                    }
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.SEVERE, "Пустая строка с желаемой КС ", e);
                }
            }
        });

        tf_crc_dec.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        long value = Long.parseLong(tf_crc_dec.getText().length() == 0 ? "0" : tf_crc_dec.getText(), RADIXES.get(tf_crc_dec));
                        tf_crc_hex.setText(Long.toHexString(value));
                        tf_crc_oct.setText(Long.toOctalString(value));
                        btn_calculate.setEnabled(tf_crc_dec.getText().length() > 0);
                    }
                }, 200);
            }
        });

        tf_crc_hex.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        long value = Long.parseLong(tf_crc_hex.getText().length() == 0 ? "0" : tf_crc_hex.getText(), RADIXES.get(tf_crc_hex));
                        tf_crc_dec.setText(Long.toString(value));
                        tf_crc_oct.setText(Long.toOctalString(value));
                        btn_calculate.setEnabled(tf_crc_hex.getText().length() > 0);
                    }
                }, 200);
            }
        });

        tf_crc_oct.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        long value = Long.parseLong(tf_crc_oct.getText().length() == 0 ? "0" : tf_crc_oct.getText(), RADIXES.get(tf_crc_oct));
                        tf_crc_hex.setText(Long.toHexString(value));
                        tf_crc_dec.setText(Long.toString(value));
                        btn_calculate.setEnabled(tf_crc_oct.getText().length() > 0);
                    }
                }, 200);
            }
        });

        chb_inject.addItemListener(e -> cb_file_type.setEnabled(e.getStateChange() == ItemEvent.SELECTED));

        btn_cancel.addActionListener(e -> {
            JCKSUM.abort();
            JCRC16.abort();
        });
    }

    private void eraseData() {
        tf_set_file.setText("");
        tf_crc_dec.setText("");
        tf_crc_hex.setText("");
        tf_crc_oct.setText("");
        lb_crc_dec.setText("DEC: ");
        lb_crc_hex.setText("HEX: ");
        lb_crc_oct.setText("OCT: ");
    }

    /**
     * Запуск расчета доп. 4-х байт данных
     * @param parseLong Дребуемая КС
     */
    private void postBytesCalcCRC32(long parseLong) {

    }

    /**
     * Запуск расчета доп. 2-х байт данных
     * @param parseLong Дребуемая КС
     */
    private void postBytesCalcCRC16(long parseLong) {
        if(parseLong >= 0 && parseLong <= 65535) {
            JCRC16.calcPostBytes((int) parseLong);
        }
    }

    /**
     * Запуск расчета доп. 4-х байт данных
     * @param parseLong Дребуемая КС
     */
    private void postBytesCalcCKSum(long parseLong) {
        if(chb_inject.isSelected()) {   // Если выбран, то проверяем на бинарный формат
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(selectedFile);
                byte[] bytes = new byte[4];
                int c = fileInputStream.read(bytes, 0, 4);
                if(c != -1 && bytes[0] == 0x7f && bytes[1] == 'E' && bytes[2] == 'L' && bytes[3] == 'F') {  // Это ELF файл
                    System.out.println("This is ELF!");
                }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                if(fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        JCKSUM.calcPostBytes(parseLong);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    /** Подсчет КС по алгоритму CRC32 */
    private void calculateCRC32() {
        FileInputStream fileInputStream = null;
        try {
            LOGGER.info("Запуск подсчета CRC32 для файла " + selectedFile);
            CRC32 crc32 = new CRC32();
            fileInputStream = new FileInputStream(selectedFile);
            int c;
            while ((c = fileInputStream.read()) != -1) {
                crc32.update(c);
            }
            fileInputStream.close();
            lb_crc_dec.setText("DEC: " + crc32.getValue());
            lb_crc_hex.setText("HEX: " + Long.toHexString(crc32.getValue()).toUpperCase());
            lb_crc_oct.setText("OCT: " + Long.toOctalString(crc32.getValue()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Не удалось обработать файл " + selectedFile, e);
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Не удалось посчитать CRC16 файла " + selectedFile, e);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Выход за границы массива ", e);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Подсчет КС по алгоритму CRC32 */
    private void calculateCRC16() {
        FileInputStream fileInputStream = null;
        try {
            LOGGER.info("Запуск подсчета CRC16 для файла " + selectedFile);
            JCRC16.reset();
            fileInputStream = new FileInputStream(selectedFile);
            int c;
            while ((c = fileInputStream.read()) != -1) {
                JCRC16.update((byte) c);
            }
            lb_crc_dec.setText("DEC: " + JCRC16.value);
            lb_crc_hex.setText("HEX: " + Long.toHexString(JCRC16.value).toUpperCase());
            lb_crc_oct.setText("OCT: " + Long.toOctalString(JCRC16.value));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Не удалось обработать файл " + selectedFile, e);
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Не удалось посчитать CRC16 файла " + selectedFile, e);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Выход за границы массива ", e);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Подсчет КС по алгоритму CKSum */
    private void calculateCKSum() {
        FileInputStream fileInputStream = null;
        try {
            LOGGER.info("Запуск подсчета CKSum для файла " + selectedFile);
            JCKSUM.erase();
            fileInputStream = new FileInputStream(selectedFile);
            int c;
            while ((c = fileInputStream.read()) != -1) {
                JCKSUM.addByte(c);
            }
            int value = JCKSUM.getValue();
            lb_crc_dec.setText("DEC: " + value);
            lb_crc_hex.setText("HEX: " + Integer.toHexString(value).toUpperCase());
            lb_crc_oct.setText("OCT: " + Integer.toOctalString(value));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Не удалось обработать файл " + selectedFile, e);
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Не удалось посчитать CRC16 файла " + selectedFile, e);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, "Выход за границы массива ", e);
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void checkCorrectingBytes(int bytes) {
        lb_correcting_bytes.setText("0x" + Integer.toHexString(bytes).toUpperCase());
    }
}

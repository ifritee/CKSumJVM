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
    /** Выбранный файл */
    private File _selectedFile;
    /** Объект для рсчета cksum */
    private JCKSum _JCKSum_o;
    /** Объект для рсчета CRC16 */
    private JCRC16 _JCRC16_o;
    /** Словарь делителей */
    HashMap<JTextField, Integer> _RadixDict_map;
    /** Логер */
    public static final Logger logger = Logger.getLogger(RootWindow.class.getName());

    /** Конструктор */
    public RootWindow() {
        Notifier.getInstance().addObserver_v(this);
        _selectedFile = new File("");
        _JCKSum_o = new JCKSum();
        _JCRC16_o = new JCRC16();
        _RadixDict_map = new HashMap<>();
        _RadixDict_map.put(tf_crc_dec, 10);
        _RadixDict_map.put(tf_crc_hex, 16);
        _RadixDict_map.put(tf_crc_oct, 8);
        btn_choose_file.addActionListener(e -> { // Действия при выборе файла
            JFileChooser fileChooser = new JFileChooser();
            eraseData_v();
            btn_calculate.setEnabled(false);
            int ReturnCode_i = fileChooser.showOpenDialog(rootPanel);
            if (ReturnCode_i == JFileChooser.APPROVE_OPTION) {
                _selectedFile = fileChooser.getSelectedFile();
            }
            tf_set_file.setText(_selectedFile.getName());
            if(cb_crc_type.getSelectedItem() == "CRC16") {
                CRC16Calculate_v();
            } else if (cb_crc_type.getSelectedItem() == "CRC32") {
                CRC32Calculate_v();
            } else if (cb_crc_type.getSelectedItem() == "CKSum") {
                CKSumCalculate_v();
            }
        });
        cb_crc_type.addActionListener(e -> { // Действия при выборе типа КС
            if (_selectedFile.exists() && _selectedFile.canRead()) {
                if(cb_crc_type.getSelectedItem() == "CRC16") {
                    CRC16Calculate_v();
                } else if (cb_crc_type.getSelectedItem() == "CRC32") {
                    CRC32Calculate_v();
                } else if (cb_crc_type.getSelectedItem() == "CKSum") {
                    CKSumCalculate_v();
                }
            }
        });
        btn_calculate.addActionListener(e -> {  // Действия при расчете
            if (_selectedFile.exists() && _selectedFile.canRead()) {
                try {
                    long parseLong = Long.parseLong(tf_crc_dec.getText());
                    if (cb_crc_type.getSelectedItem() == "CRC16") {
                        PostBytesCalcCRC16_v(parseLong);
                    } else if (cb_crc_type.getSelectedItem() == "CRC32") {
                        PostBytesCalcCRC32_v(parseLong);
                    } else if (cb_crc_type.getSelectedItem() == "CKSum") {
                        PostBytesCalcCKSum_v(parseLong);
                    }
                } catch (NumberFormatException ex) {
                    logger.log(Level.SEVERE, "Пустая строка с желаемой КС ", e);
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
                        long Value_i = Long.parseLong(tf_crc_dec.getText().length() == 0 ? "0" : tf_crc_dec.getText(), _RadixDict_map.get(tf_crc_dec));
                        tf_crc_hex.setText(Long.toHexString(Value_i));
                        tf_crc_oct.setText(Long.toOctalString(Value_i));
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
                        long Value_i = Long.parseLong(tf_crc_hex.getText().length() == 0 ? "0" : tf_crc_hex.getText(), _RadixDict_map.get(tf_crc_hex));
                        tf_crc_dec.setText(Long.toString(Value_i));
                        tf_crc_oct.setText(Long.toOctalString(Value_i));
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
                        long Value_i = Long.parseLong(tf_crc_oct.getText().length() == 0 ? "0" : tf_crc_oct.getText(), _RadixDict_map.get(tf_crc_oct));
                        tf_crc_hex.setText(Long.toHexString(Value_i));
                        tf_crc_dec.setText(Long.toString(Value_i));
                        btn_calculate.setEnabled(tf_crc_oct.getText().length() > 0);
                    }
                }, 200);
            }
        });

        chb_inject.addItemListener(e -> {
            cb_file_type.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });

        btn_cancel.addActionListener(e -> {
            _JCKSum_o.Abort_v();
            _JCRC16_o.Abort_v();
        });
    }

    private void eraseData_v() {
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
    private void PostBytesCalcCRC32_v(long parseLong) {

    }

    /**
     * Запуск расчета доп. 2-х байт данных
     * @param parseLong Дребуемая КС
     */
    private void PostBytesCalcCRC16_v(long parseLong) {
        if(parseLong >= 0 && parseLong <= 65535) {
            _JCRC16_o.CalcPostBytes_v((int) parseLong);
        }
    }

    /**
     * Запуск расчета доп. 4-х байт данных
     * @param parseLong Дребуемая КС
     */
    private void PostBytesCalcCKSum_v(long parseLong) {
        _JCKSum_o.CalcPostBytes_v(parseLong);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    /** Подсчет КС по алгоритму CRC32 */
    private void CRC32Calculate_v() {
        try {
            logger.info("Запуск подсчета CRC32 для файла " + _selectedFile);
            CRC32 CRC32_o = new CRC32();
            FileInputStream fis = new FileInputStream(_selectedFile);
            int c;
            while ((c = fis.read()) != -1) {
                CRC32_o.update(c);
            }
            lb_crc_dec.setText("DEC: " + CRC32_o.getValue());
            lb_crc_hex.setText("HEX: " + Long.toHexString(CRC32_o.getValue()).toUpperCase());
            lb_crc_oct.setText("OCT: " + Long.toOctalString(CRC32_o.getValue()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось обработать файл " + _selectedFile, e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Не удалось посчитать CRC16 файла " + _selectedFile, e);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.SEVERE, "Выход за границы массива ", e);
        }
    }

    /** Подсчет КС по алгоритму CRC32 */
    private void CRC16Calculate_v() {
        try {
            logger.info("Запуск подсчета CRC16 для файла " + _selectedFile);
            _JCRC16_o.reset();
            FileInputStream fis = new FileInputStream(_selectedFile);
            int c;
            while ((c = fis.read()) != -1) {
                _JCRC16_o.update((byte) c);
            }
            lb_crc_dec.setText("DEC: " + _JCRC16_o.value);
            lb_crc_hex.setText("HEX: " + Long.toHexString(_JCRC16_o.value).toUpperCase());
            lb_crc_oct.setText("OCT: " + Long.toOctalString(_JCRC16_o.value));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось обработать файл " + _selectedFile, e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Не удалось посчитать CRC16 файла " + _selectedFile, e);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.SEVERE, "Выход за границы массива ", e);
        }
    }

    /** Подсчет КС по алгоритму CKSum */
    private void CKSumCalculate_v() {
        try {
            logger.info("Запуск подсчета CKSum для файла " + _selectedFile);
            _JCKSum_o.erase_v();
            FileInputStream fis = new FileInputStream(_selectedFile);
            int c;
            while ((c = fis.read()) != -1) {
                _JCKSum_o.addByte_v(c);
            }
            int Value_i = _JCKSum_o.getValue_i();
            lb_crc_dec.setText("DEC: " + Value_i);
            lb_crc_hex.setText("HEX: " + Integer.toHexString(Value_i).toUpperCase());
            lb_crc_oct.setText("OCT: " + Integer.toOctalString(Value_i));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось обработать файл " + _selectedFile, e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Не удалось посчитать CRC16 файла " + _selectedFile, e);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.SEVERE, "Выход за границы массива ", e);
        }
    }

    @Override
    public void CheckCorrectingBytes_v(int Bytes_i) {
        System.out.println("Check correcting bytes 0x" + Integer.toHexString(Bytes_i).toUpperCase());
    }
}

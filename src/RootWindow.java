import sun.misc.CRC16;
import sun.plugin2.gluegen.runtime.CPU;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 * Основное окно для ПО "Подмена J"
 */
public class RootWindow {
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
    /** Выбранный файл */
    private File _selectedFile;
    /** Логер */
    public static final Logger logger = Logger.getLogger(
            RootWindow.class.getName());

    /** Конструктор */
    public RootWindow() {
        _selectedFile = new File("");
        btn_choose_file.addActionListener(e -> { // Действия при выборе файла
            JFileChooser fileChooser = new JFileChooser();
            int ReturnCode_i = fileChooser.showOpenDialog(rootPanel);
            if (ReturnCode_i == JFileChooser.APPROVE_OPTION) {
                _selectedFile = fileChooser.getSelectedFile();
                tf_set_file.setText(_selectedFile.getName());
                btn_calculate.setEnabled(true);
                if(cb_crc_type.getSelectedItem() == "CRC16") {
                    CRC16Calculate_v();
                } else if (cb_crc_type.getSelectedItem() == "CRC32") {
                    CRC32Calculate_v();
                } else if (cb_crc_type.getSelectedItem() == "CKSum") {
                    CKSumCalculate_v();
                }
            } else { // Ошибка открытия
                btn_calculate.setEnabled(false);
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
            int CPUCount_i = Runtime.getRuntime().availableProcessors();
            System.out.println(CPUCount_i);
        });
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
            CRC16 CRC32_o = new CRC16();
            FileInputStream fis = new FileInputStream(_selectedFile);
            int c;
            while ((c = fis.read()) != -1) {
                CRC32_o.update((byte) c);
            }
            lb_crc_dec.setText("DEC: " + CRC32_o.value);
            lb_crc_hex.setText("HEX: " + Long.toHexString(CRC32_o.value).toUpperCase());
            lb_crc_oct.setText("OCT: " + Long.toOctalString(CRC32_o.value));
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
            JCKSum JCKSum_o = new JCKSum();
            FileInputStream fis = new FileInputStream(_selectedFile);
            int c;
            while ((c = fis.read()) != -1) {
                JCKSum_o.addByte_v(c);
            }
            int Value_i = JCKSum_o.getValue_i();
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
}

package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

/**
 * Fabrica de componentes Swing con el estilo comun de la aplicacion.
 * Evita repetir el mismo armado de paneles en cada ventana.
 */
public class ComponentesGUI {

    public static void configurar(JFrame ventana, String titulo, int ancho, int alto) {
        ventana.setTitle(titulo);
        ventana.setSize(ancho, alto);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setLayout(new BorderLayout());
        ventana.setLocationRelativeTo(null);
        ventana.getContentPane().setBackground(EstilosGUI.COLOR_CLARO);
    }

    public static JLabel titulo(String texto) {
        JLabel titulo = new JLabel("<html><div style='text-align:center;'>" + texto + "</div></html>");
        titulo.setHorizontalAlignment(JLabel.CENTER);
        titulo.setFont(EstilosGUI.FUENTE_TITULO);
        titulo.setForeground(EstilosGUI.COLOR);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return titulo;
    }

    public static JTextField campoTexto() {
        JTextField campo = new JTextField();
        campo.setBorder(EstilosGUI.GRAY_BORDER);
        return campo;
    }

    public static JCheckBox casilla(String texto) {
        JCheckBox casilla = new JCheckBox(texto);
        casilla.setBackground(EstilosGUI.COLOR_CLARO);
        return casilla;
    }

    public static <T> JComboBox<T> combo(T[] opciones) {
        JComboBox<T> combo = new JComboBox<>(opciones);
        combo.setBorder(EstilosGUI.GRAY_BORDER);
        return combo;
    }

    /** Panel de formulario: etiquetas a la izquierda y campos a la derecha. */
    public static JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JLabel[] labels = new JLabel[etiquetas.length];
        for (int i = 0; i < etiquetas.length; i++) {
            labels[i] = new JLabel(etiquetas[i]);
        }
        return formulario(labels, campos);
    }

    /** Igual que el anterior, pero con etiquetas ya creadas (utiles si cambian en tiempo de ejecucion). */
    public static JPanel formulario(JLabel[] etiquetas, JComponent[] campos) {
        JPanel panelEtiquetas = new JPanel(new GridLayout(etiquetas.length, 1, 0, 8));
        panelEtiquetas.setBackground(EstilosGUI.COLOR_CLARO);
        panelEtiquetas.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 8));

        JPanel panelCampos = new JPanel(new GridLayout(campos.length, 1, 0, 8));
        panelCampos.setBackground(EstilosGUI.COLOR_CLARO);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 20));

        for (JLabel etiqueta : etiquetas) {
            panelEtiquetas.add(etiqueta);
        }
        for (JComponent campo : campos) {
            panelCampos.add(campo);
        }

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(EstilosGUI.COLOR_CLARO);
        contenedor.add(panelEtiquetas, BorderLayout.WEST);
        contenedor.add(panelCampos, BorderLayout.CENTER);
        return contenedor;
    }

    public static JPanel panelBotones(JButton... botones) {
        JPanel panel = new JPanel();
        panel.setBackground(EstilosGUI.COLOR_CLARO);
        for (JButton boton : botones) {
            panel.add(boton);
        }
        return panel;
    }

    /**
     * Renderer generico para combos: recibe como se saca el texto de cada elemento.
     * Sirve igual para un combo de meseros, de clientes o de platillos.
     * Un valor nulo conserva el texto por defecto, para opciones como "Sin cliente".
     */
    public static <T> DefaultListCellRenderer renderer(Function<T, String> texto) {
        return new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean tieneFoco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, tieneFoco);
                if (valor != null) {
                    setText(texto.apply((T) valor));
                }
                return this;
            }
        };
    }

    /**
     * Formatea dinero en pesos colombianos. Todas las vistas lo muestran igual,
     * y ninguna vuelve a convertir un BigDecimal a double para pintarlo.
     */
    public static String moneda(BigDecimal valor) {
        if (valor == null) {
            return NumberFormat.getCurrencyInstance(Locale.of("es", "CO")).format(BigDecimal.ZERO);
        }
        return NumberFormat.getCurrencyInstance(Locale.of("es", "CO")).format(valor);
    }

    public static JTable tabla() {
        JTable tabla = new JTable();
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        return tabla;
    }

    public static DefaultTableModel modelo(String... columnas) {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        for (String columna : columnas) {
            modelo.addColumn(columna);
        }
        return modelo;
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}

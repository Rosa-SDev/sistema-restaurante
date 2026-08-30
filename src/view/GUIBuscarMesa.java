package view;

import controller.ControllerMesa;
import model.EstadoMesa;
import model.Mesa;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Busqueda de mesas por numero o por estado. Los resultados se pintan con
 * el mismo adaptador y las mismas columnas que el listado.
 */
public class GUIBuscarMesa extends JFrame {

    private JTextField numeroTexto;
    private JComboBox<String> estadoCombo;
    private JTable tabla;

    public GUIBuscarMesa() {
        ComponentesGUI.configurar(this, "Buscar mesa", 560, 400);

        numeroTexto = new JTextField(6);
        numeroTexto.setBorder(EstilosGUI.GRAY_BORDER);

        String[] estados = new String[EstadoMesa.values().length + 1];
        estados[0] = "TODOS";
        for (int i = 0; i < EstadoMesa.values().length; i++) {
            estados[i + 1] = EstadoMesa.values()[i].name();
        }
        estadoCombo = ComponentesGUI.combo(estados);

        JButton buscarBTN = new JButton("Buscar");
        buscarBTN.addActionListener(e -> buscar());

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltros.setBackground(EstilosGUI.COLOR_CLARO);
        panelFiltros.add(new JLabel("Número de mesa:"));
        panelFiltros.add(numeroTexto);
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(estadoCombo);
        panelFiltros.add(buscarBTN);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(EstilosGUI.COLOR_CLARO);
        panelNorte.add(ComponentesGUI.titulo("Buscar por número o por estado"), BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.CENTER);

        tabla = ComponentesGUI.tabla();

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void buscar() {
        List<Mesa> resultados = new ArrayList<>();
        try {
            if (!numeroTexto.getText().trim().isEmpty()) {
                Mesa mesa = ControllerMesa.buscarMesaPorNumero(Integer.parseInt(numeroTexto.getText().trim()));
                if (mesa != null) {
                    resultados.add(mesa);
                }
            } else if ("TODOS".equals(estadoCombo.getSelectedItem())) {
                resultados = ControllerMesa.listarMesas();
            } else {
                resultados = ControllerMesa.buscarMesa((String) estadoCombo.getSelectedItem());
            }
        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El número de mesa debe ser un entero.");
            return;
        }

        tabla.setModel(new AdaptadorTablaModelo<>(resultados, GUIListarMesas.columnas()));

        if (resultados.isEmpty()) {
            ComponentesGUI.aviso(this, "No se encontraron mesas.");
        }
    }
}

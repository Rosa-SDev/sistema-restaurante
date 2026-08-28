package view;

import controller.ControllerPedido;
import model.EstadoPedido;
import model.Pedido;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Busqueda de pedidos por Id o por estado. Pinta los resultados con las mismas
 * columnas que el listado, para que las dos tablas no se desincronicen.
 */
public class GUIBuscarPedido extends JFrame {

    private static final String TODOS = "TODOS";

    private JTextField idTexto;
    private JComboBox<String> estadoCombo;
    private JTable tabla;

    public GUIBuscarPedido() {
        ComponentesGUI.configurar(this, "Buscar pedido", 860, 420);

        idTexto = new JTextField(8);
        idTexto.setBorder(EstilosGUI.GRAY_BORDER);

        String[] estados = new String[EstadoPedido.values().length + 1];
        estados[0] = TODOS;
        for (int i = 0; i < EstadoPedido.values().length; i++) {
            estados[i + 1] = EstadoPedido.values()[i].name();
        }
        estadoCombo = ComponentesGUI.combo(estados);

        JButton buscarBTN = new JButton("Buscar");
        buscarBTN.addActionListener(e -> buscar());

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltros.setBackground(EstilosGUI.COLOR_CLARO);
        panelFiltros.add(new JLabel("Id:"));
        panelFiltros.add(idTexto);
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(estadoCombo);
        panelFiltros.add(buscarBTN);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(EstilosGUI.COLOR_CLARO);
        panelNorte.add(ComponentesGUI.titulo("Buscar por Id o por estado"), BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.CENTER);

        tabla = ComponentesGUI.tabla();

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void buscar() {
        List<Pedido> resultados = new ArrayList<>();
        try {
            if (!idTexto.getText().trim().isEmpty()) {
                Pedido pedido = ControllerPedido.buscarPedido(Integer.parseInt(idTexto.getText().trim()));
                if (pedido != null) {
                    resultados.add(pedido);
                }
            } else if (TODOS.equals(estadoCombo.getSelectedItem())) {
                resultados = ControllerPedido.listarPedidos();
            } else {
                resultados = ControllerPedido.buscarPedido(
                        EstadoPedido.valueOf((String) estadoCombo.getSelectedItem()));
            }
        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id debe ser un numero entero.");
            return;
        }

        tabla.setModel(new AdaptadorTablaModelo<>(resultados, GUIListarPedidos.columnas()));

        if (resultados.isEmpty()) {
            ComponentesGUI.aviso(this, "No se encontraron pedidos.");
        }
    }
}

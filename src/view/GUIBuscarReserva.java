package view;

import controller.ControllerReserva;
import model.EstadoReserva;
import model.Reserva;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUIBuscarReserva extends JFrame {

    private JTextField idTexto;
    private JComboBox<EstadoReserva> estadoCombo;
    private JTable tabla;

    public GUIBuscarReserva() {
        ComponentesGUI.configurar(this, "Buscar reserva", 800, 420);

        idTexto = new JTextField(8);
        idTexto.setBorder(EstilosGUI.GRAY_BORDER);
        estadoCombo = ComponentesGUI.combo(EstadoReserva.values());

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
        List<Reserva> resultados = new ArrayList<>();
        try {
            if (!idTexto.getText().trim().isEmpty()) {
                Reserva reserva = ControllerReserva.buscarReserva(
                        Integer.parseInt(idTexto.getText().trim()));
                if (reserva != null) {
                    resultados.add(reserva);
                }
            } else {
                EstadoReserva estado = (EstadoReserva) estadoCombo.getSelectedItem();
                resultados = ControllerReserva.buscarReserva(estado);
            }
        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id debe ser un numero entero.");
            return;
        }

        tabla.setModel(new AdaptadorTablaModelo<>(resultados, GUIListarReservas.columnas()));

        if (resultados.isEmpty()) {
            ComponentesGUI.aviso(this, "No se encontraron reservas.");
        }
    }
}

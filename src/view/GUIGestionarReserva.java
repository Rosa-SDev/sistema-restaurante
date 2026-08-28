package view;

import controller.ControllerReserva;
import model.IActualizable;
import model.Reserva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;

/**
 * Permite confirmar o cancelar una reserva seleccionandola de un combo.
 * Implementa IActualizable para refrescarse cuando otra ventana cambia datos.
 */
public class GUIGestionarReserva extends JFrame implements IActualizable {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JComboBox<Reserva> reservaCombo;
    private JLabel detalle;

    public GUIGestionarReserva() {
        ComponentesGUI.configurar(this, "Confirmar o cancelar reserva", 560, 320);
        setResizable(false);

        reservaCombo = new JComboBox<>();
        reservaCombo.setBorder(EstilosGUI.GRAY_BORDER);
        reservaCombo.setRenderer(ComponentesGUI.renderer(
                (Reserva r) -> "Reserva #" + r.getId() + " - " + r.getCliente().getNombre()
                        + " - " + r.getEstado()));
        reservaCombo.addActionListener(e -> mostrarDetalle());

        detalle = new JLabel(" ");
        detalle.setHorizontalAlignment(JLabel.CENTER);
        detalle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Reserva:"},
                new JComponent[]{reservaCombo});

        JButton confirmarBTN = new JButton("Confirmar");
        confirmarBTN.addActionListener(e -> confirmar());
        JButton cancelarBTN = new JButton("Cancelar reserva");
        cancelarBTN.addActionListener(e -> cancelar());

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(EstilosGUI.COLOR_CLARO);
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(detalle, BorderLayout.CENTER);

        add(ComponentesGUI.titulo("Confirmar o cancelar reserva"), BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(confirmarBTN, cancelarBTN), BorderLayout.SOUTH);

        cargarReservas();
        ControllerReserva.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerReserva.removeActualizable(GUIGestionarReserva.this);
            }
        });
    }

    private void cargarReservas() {
        Reserva seleccionada = (Reserva) reservaCombo.getSelectedItem();

        reservaCombo.removeAllItems();
        for (Reserva reserva : ControllerReserva.listarReservas()) {
            reservaCombo.addItem(reserva);
        }

        if (seleccionada != null && ControllerReserva.buscarReserva(seleccionada.getId()) != null) {
            reservaCombo.setSelectedItem(seleccionada);
        }
        mostrarDetalle();
    }

    private void mostrarDetalle() {
        Reserva reserva = (Reserva) reservaCombo.getSelectedItem();
        if (reserva == null) {
            detalle.setText("No hay reservas registradas.");
            return;
        }
        detalle.setText(String.format("<html><div style='text-align:center;'>%s<br>Mesa %d "
                        + "(capacidad %d)<br>%d personas - mesero: %s<br><b>Estado: %s</b></div></html>",
                reserva.getFechaHora().format(FORMATO), reserva.getMesa().getNumero(),
                reserva.getMesa().getCapacidad(), reserva.getNumPersonas(),
                reserva.getMesero().getNombre(), reserva.getEstado()));
    }

    private void confirmar() {
        try {
            Reserva reserva = seleccionada();
            ControllerReserva.confirmarReserva(reserva);
            ComponentesGUI.exito(this, "Reserva confirmada. La mesa " + reserva.getMesa().getNumero()
                    + " quedo RESERVADA.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    private void cancelar() {
        try {
            Reserva reserva = seleccionada();
            if (!ComponentesGUI.confirmar(this, "Cancelar la reserva #" + reserva.getId() + "?")) {
                return;
            }
            ControllerReserva.cancelarReserva(reserva);
            ComponentesGUI.exito(this, "Reserva cancelada. La mesa quedo LIBRE.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    private Reserva seleccionada() {
        Reserva reserva = (Reserva) reservaCombo.getSelectedItem();
        if (reserva == null) {
            throw new RuntimeException("Error: seleccione una reserva.");
        }
        return reserva;
    }

    @Override
    public void actualizar() {
        cargarReservas();
    }
}

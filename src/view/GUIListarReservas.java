package view;

import controller.ControllerReserva;
import model.IActualizable;
import model.Reserva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Listado de reservas con el Adapter y el Observer.
 * Las columnas se comparten con GUIBuscarReserva.
 */
public class GUIListarReservas extends JFrame implements IActualizable {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTable tabla;

    public GUIListarReservas() {
        ComponentesGUI.configurar(this, "Listado de reservas", 800, 400);

        tabla = ComponentesGUI.tabla();

        add(ComponentesGUI.titulo("Reservas registradas"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        actualizar();
        ControllerReserva.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerReserva.removeActualizable(GUIListarReservas.this);
            }
        });
    }

    /** Columnas compartidas con GUIBuscarReserva. */
    public static List<Columna<Reserva>> columnas() {
        return List.of(
                col("ID", Reserva::getId),
                col("Fecha y hora", r -> r.getFechaHora().format(FORMATO)),
                col("Cliente", r -> r.getCliente().getNombre()),
                col("Mesa", r -> r.getMesa().getNumero()),
                col("Mesero", r -> r.getMesero().getNombre()),
                col("Personas", Reserva::getNumPersonas),
                col("Estado", Reserva::getEstado));
    }

    @Override
    public void actualizar() {
        tabla.setModel(new AdaptadorTablaModelo<>(ControllerReserva.listarReservas(), columnas()));
    }
}

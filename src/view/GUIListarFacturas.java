package view;

import controller.ControllerFactura;
import model.Factura;
import model.IActualizable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Listado de facturas emitidas, con la anulacion de la seleccionada.
 * Observa a ControllerFactura para repintarse cuando se emite una nueva.
 */
public class GUIListarFacturas extends JFrame implements IActualizable {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTable tabla;

    /** Respaldo de las filas visibles, para saber que factura se selecciono. */
    private List<Factura> filas = new ArrayList<>();

    public GUIListarFacturas() {
        ComponentesGUI.configurar(this, "Facturas emitidas", 900, 420);

        tabla = ComponentesGUI.tabla();

        JButton anularBTN = new JButton("Anular la factura seleccionada");
        anularBTN.addActionListener(e -> anular());

        add(ComponentesGUI.titulo("Facturas emitidas"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(anularBTN), BorderLayout.SOUTH);

        actualizar();
        ControllerFactura.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerFactura.removeActualizable(GUIListarFacturas.this);
            }
        });
    }

    @Override
    public void actualizar() {
        filas = ControllerFactura.listarFacturas();
        tabla.setModel(new AdaptadorTablaModelo<>(filas, List.of(
                col("ID", Factura::getId),
                col("Número", Factura::getNumero),
                col("Fecha", f -> f.getFecha() != null ? f.getFecha().format(FORMATO) : "-"),
                col("Pedido", f -> "#" + f.getPedido().getId()),
                col("Cajero", f -> f.getCajero().getNombre()),
                col("Subtotal", f -> ComponentesGUI.moneda(f.getSubtotal())),
                col("Impuestos", f -> ComponentesGUI.moneda(f.getImpuestos())),
                col("Total", f -> ComponentesGUI.moneda(f.getTotal())),
                col("Estado", f -> f.isAnulada() ? "ANULADA" : "VIGENTE"))));
    }

    private void anular() {
        try {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                throw new RuntimeException("Error: seleccione una factura de la tabla.");
            }

            Factura factura = filas.get(fila);
            if (!ComponentesGUI.confirmar(this, "¿Anular la factura " + factura.getNumero() + "?")) {
                return;
            }

            ControllerFactura.anularFactura(factura);
            ComponentesGUI.exito(this, "Factura anulada.");

        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}

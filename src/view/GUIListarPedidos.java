package view;

import controller.ControllerPedido;
import model.IActualizable;
import model.Pedido;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Listado de pedidos. Observa a ControllerPedido, asi que se repinta solo
 * cuando otra ventana crea un pedido, le agrega platillos o lo cancela.
 */
public class GUIListarPedidos extends JFrame implements IActualizable {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTable tabla;

    public GUIListarPedidos() {
        ComponentesGUI.configurar(this, "Listado de pedidos", 860, 400);

        tabla = ComponentesGUI.tabla();

        add(ComponentesGUI.titulo("Pedidos registrados"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        actualizar();
        ControllerPedido.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerPedido.removeActualizable(GUIListarPedidos.this);
            }
        });
    }

    /** Columnas de una tabla de pedidos. Compartidas con la ventana de busqueda. */
    public static List<Columna<Pedido>> columnas() {
        return List.of(
                col("ID", Pedido::getId),
                col("Fecha", p -> p.getFechaHora().format(FORMATO)),
                col("Mesa", p -> p.getMesa().getNumero()),
                col("Mesero", p -> p.getMesero().getNombre()),
                // el cliente es opcional y el cocinero se asigna al preparar
                col("Cliente", p -> p.getCliente() != null ? p.getCliente().getNombre() : "-"),
                col("Cocinero", p -> p.getCocinero() != null ? p.getCocinero().getNombre() : "-"),
                col("Estado", Pedido::getEstado),
                col("Platillos", p -> p.getPlatillos().size()),
                col("Total", p -> ComponentesGUI.moneda(p.calcularTotal())));
    }

    @Override
    public void actualizar() {
        tabla.setModel(new AdaptadorTablaModelo<>(ControllerPedido.listarPedidos(), columnas()));
    }
}

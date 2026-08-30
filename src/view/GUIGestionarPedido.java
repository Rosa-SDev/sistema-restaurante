package view;

import controller.ControllerPedido;
import controller.ControllerPlatillo;
import model.EstadoPedido;
import model.IActualizable;
import model.Pedido;
import model.Platillo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Gestion de un pedido abierto: agregar y quitar platillos.
 *
 * Ya no existe DetallePedido (decision B). El pedido guarda List&lt;Platillo&gt; con
 * repeticion, asi que dos hamburguesas son dos elementos iguales en la lista y
 * dos filas en esta tabla. El campo "Cantidad" sigue aqui por comodidad, pero
 * no viaja al modelo: el controlador es quien repite la llamada N veces.
 *
 * Solo se listan pedidos ABIERTOS porque el controlador rechaza agregar o
 * quitar platillos en cualquier otro estado.
 */
public class GUIGestionarPedido extends JFrame implements IActualizable {

    private JComboBox<Pedido> pedidoCombo;
    private JComboBox<Platillo> platilloCombo;
    private JTextField cantidadTexto;
    private JTable tabla;
    private JLabel total;

    /** Respaldo de las filas visibles, para saber que platillo se selecciono. */
    private List<Platillo> filas = new ArrayList<>();

    public GUIGestionarPedido() {
        ComponentesGUI.configurar(this, "Gestionar pedido", 720, 500);

        pedidoCombo = new JComboBox<>();
        pedidoCombo.setBorder(EstilosGUI.GRAY_BORDER);
        pedidoCombo.setRenderer(ComponentesGUI.renderer((Pedido p) ->
                "Pedido #" + p.getId() + " - Mesa " + p.getMesa().getNumero()
                        + " - " + p.getMesero().getNombre()));
        pedidoCombo.addActionListener(e -> refrescarDetalle());

        JPanel panelSeleccion = new JPanel(new BorderLayout(10, 0));
        panelSeleccion.setBackground(EstilosGUI.COLOR_CLARO);
        panelSeleccion.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        panelSeleccion.add(new JLabel("Pedido abierto:"), BorderLayout.WEST);
        panelSeleccion.add(pedidoCombo, BorderLayout.CENTER);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(EstilosGUI.COLOR_CLARO);
        panelNorte.add(ComponentesGUI.titulo("Gestionar pedido"), BorderLayout.NORTH);
        panelNorte.add(panelSeleccion, BorderLayout.CENTER);

        tabla = ComponentesGUI.tabla();

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(construirPanelAcciones(), BorderLayout.SOUTH);

        cargarPedidos();
        ControllerPedido.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerPedido.removeActualizable(GUIGestionarPedido.this);
            }
        });
    }

    private JPanel construirPanelAcciones() {
        platilloCombo = new JComboBox<>();
        platilloCombo.setBorder(EstilosGUI.GRAY_BORDER);
        platilloCombo.setRenderer(ComponentesGUI.renderer((Platillo pl) ->
                pl.getNombre() + " - " + ComponentesGUI.moneda(pl.getPrecio())
                        + (pl.isDisponible() ? "" : "  (no disponible)")));
        for (Platillo platillo : ControllerPlatillo.listarPlatillos()) {
            platilloCombo.addItem(platillo);
        }

        cantidadTexto = new JTextField("1", 4);
        cantidadTexto.setBorder(EstilosGUI.GRAY_BORDER);

        JButton agregarBTN = new JButton("Agregar al pedido");
        agregarBTN.addActionListener(e -> agregarPlatillo());
        JButton quitarBTN = new JButton("Quitar una unidad");
        quitarBTN.addActionListener(e -> quitarPlatillo());

        JPanel filaPlatillo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        filaPlatillo.setBackground(EstilosGUI.COLOR_CLARO);
        filaPlatillo.add(new JLabel("Platillo:"));
        filaPlatillo.add(platilloCombo);
        filaPlatillo.add(new JLabel("Cantidad:"));
        filaPlatillo.add(cantidadTexto);
        filaPlatillo.add(agregarBTN);
        filaPlatillo.add(quitarBTN);

        total = new JLabel(" ");
        total.setHorizontalAlignment(JLabel.CENTER);
        total.setFont(new Font("Arial", Font.BOLD, 14));
        total.setForeground(EstilosGUI.COLOR);
        total.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JButton cancelarBTN = new JButton("Cancelar pedido");
        cancelarBTN.addActionListener(e -> cancelarPedido());

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBackground(EstilosGUI.COLOR_CLARO);
        panel.add(filaPlatillo);
        panel.add(total);
        panel.add(ComponentesGUI.panelBotones(cancelarBTN));
        return panel;
    }

    private void cargarPedidos() {
        Pedido seleccionado = (Pedido) pedidoCombo.getSelectedItem();

        pedidoCombo.removeAllItems();
        for (Pedido pedido : ControllerPedido.buscarPedido(EstadoPedido.ABIERTO)) {
            pedidoCombo.addItem(pedido);
        }

        if (seleccionado != null && ControllerPedido.buscarPedido(seleccionado.getId()) != null) {
            pedidoCombo.setSelectedItem(seleccionado);
        }
        refrescarDetalle();
    }

    private void refrescarDetalle() {
        Pedido pedido = (Pedido) pedidoCombo.getSelectedItem();

        filas = pedido != null ? pedido.getPlatillos() : new ArrayList<>();
        tabla.setModel(new AdaptadorTablaModelo<>(filas, List.of(
                col("Platillo", Platillo::getNombre),
                col("Categoría", Platillo::getCategoria),
                col("Precio", pl -> ComponentesGUI.moneda(pl.getPrecio())))));

        if (pedido == null) {
            total.setText("No hay pedidos abiertos.");
        } else {
            // Sin impuestos: los calcula Factura al emitirse (decision C)
            total.setText("Platillos: " + filas.size()
                    + "     Total del consumo (sin impuestos): "
                    + ComponentesGUI.moneda(pedido.calcularTotal()));
        }
    }

    private void agregarPlatillo() {
        try {
            ControllerPedido.agregarPlatillo(
                    seleccionado(),
                    (Platillo) platilloCombo.getSelectedItem(),
                    Integer.parseInt(cantidadTexto.getText().trim()));
            cantidadTexto.setText("1");

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "La cantidad debe ser un número entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    private void quitarPlatillo() {
        try {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                throw new RuntimeException("Error: seleccione en la tabla el platillo que quiere quitar.");
            }
            // quita una unidad, no todas las iguales
            ControllerPedido.quitarPlatillo(seleccionado(), filas.get(fila));

        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    private void cancelarPedido() {
        try {
            Pedido pedido = seleccionado();
            if (!ComponentesGUI.confirmar(this, "¿Cancelar el pedido #" + pedido.getId()
                    + "? La mesa quedará libre.")) {
                return;
            }
            ControllerPedido.cancelarPedido(pedido);
            ComponentesGUI.exito(this, "Pedido cancelado.");

        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    private Pedido seleccionado() {
        Pedido pedido = (Pedido) pedidoCombo.getSelectedItem();
        if (pedido == null) {
            throw new RuntimeException("Error: seleccione un pedido abierto.");
        }
        return pedido;
    }

    @Override
    public void actualizar() {
        cargarPedidos();
    }
}

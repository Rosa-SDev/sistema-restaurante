package view;

import controller.ControllerFactura;
import controller.ControllerPedido;
import controller.ControllerUsuario;
import model.Cajero;
import model.EstadoPedido;
import model.Factura;
import model.MetodoPago;
import model.Pedido;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Emision de la factura de un pedido.
 *
 * El metodo de pago ya no vive en la factura: se registra en el pedido con
 * registrarPago(). Quien orquesta las dos cosas es ControllerFactura, que emite
 * primero para saber cuanto hay que cobrar y solo guarda la factura si el monto
 * alcanza; asi un cobro insuficiente no deja una factura huerfana.
 *
 * El cajero es una relacion de la factura (relacion 7), por eso se elige aqui.
 */
public class GUIFacturarPedido extends JFrame {

    private static final int DECIMALES = 2;

    private JComboBox<Pedido> pedidoCombo;
    private JComboBox<Cajero> cajeroCombo;
    private JComboBox<MetodoPago> metodoCombo;
    private JTextField montoTexto;
    private JLabel detalle;

    public GUIFacturarPedido() {
        ComponentesGUI.configurar(this, "Facturar pedido", 560, 420);
        setResizable(false);

        pedidoCombo = new JComboBox<>();
        pedidoCombo.setBorder(EstilosGUI.GRAY_BORDER);
        pedidoCombo.setRenderer(ComponentesGUI.renderer((Pedido p) ->
                "Pedido #" + p.getId() + " - Mesa " + p.getMesa().getNumero()));
        for (Pedido pedido : facturables()) {
            pedidoCombo.addItem(pedido);
        }
        pedidoCombo.addActionListener(e -> mostrarResumen());

        cajeroCombo = new JComboBox<>();
        cajeroCombo.setBorder(EstilosGUI.GRAY_BORDER);
        cajeroCombo.setRenderer(ComponentesGUI.renderer((Cajero c) -> c.getNombre()));
        for (Cajero cajero : ControllerUsuario.listarCajeros()) {
            cajeroCombo.addItem(cajero);
        }

        metodoCombo = ComponentesGUI.combo(MetodoPago.values());
        montoTexto = ComponentesGUI.campoTexto();

        detalle = new JLabel(" ");
        detalle.setHorizontalAlignment(JLabel.CENTER);
        detalle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Pedido:", "Cajero:", "Método de pago:", "Monto recibido:"},
                new JComponent[]{pedidoCombo, cajeroCombo, metodoCombo, montoTexto});

        JButton facturarBTN = new JButton("Emitir factura");
        facturarBTN.addActionListener(e -> facturar());

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(EstilosGUI.COLOR_CLARO);
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(detalle, BorderLayout.CENTER);

        add(ComponentesGUI.titulo("Facturar pedido"), BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(facturarBTN), BorderLayout.SOUTH);

        mostrarResumen();
    }

    /** Pedidos que todavia se pueden facturar: no cancelados, con platillos y sin factura. */
    private java.util.List<Pedido> facturables() {
        java.util.List<Pedido> lista = new java.util.ArrayList<>();
        for (Pedido pedido : ControllerPedido.listarPedidos()) {
            if (pedido.getEstado() != EstadoPedido.CANCELADO
                    && !pedido.getPlatillos().isEmpty()
                    && ControllerFactura.buscarFacturaDePedido(pedido) == null) {
                lista.add(pedido);
            }
        }
        return lista;
    }

    /**
     * Repite el calculo de Factura.emitir() solo para mostrarlo antes de cobrar.
     * El calculo que manda es el de la factura: si este se desviara, el
     * controlador rechazaria el monto, nunca cobraria de menos.
     */
    private void mostrarResumen() {
        Pedido pedido = (Pedido) pedidoCombo.getSelectedItem();
        if (pedido == null) {
            detalle.setText("No hay pedidos pendientes por facturar.");
            montoTexto.setText("");
            return;
        }

        BigDecimal subtotal = pedido.calcularTotal();
        BigDecimal impuestos = subtotal.multiply(Factura.IMPUESTO_CONSUMO)
                                       .setScale(DECIMALES, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuestos);

        detalle.setText("<html><div style='text-align:center;'>Platillos: " + pedido.getPlatillos().size()
                + "<br>Subtotal del consumo: " + ComponentesGUI.moneda(subtotal)
                + "<br>Impuesto al consumo (8%): " + ComponentesGUI.moneda(impuestos)
                + "<br><b>Total a pagar: " + ComponentesGUI.moneda(total) + "</b></div></html>");
        montoTexto.setText(total.toPlainString());
    }

    private void facturar() {
        try {
            Pedido pedido = (Pedido) pedidoCombo.getSelectedItem();
            if (pedido == null) {
                throw new RuntimeException("Error: no hay pedidos pendientes por facturar.");
            }
            Cajero cajero = (Cajero) cajeroCombo.getSelectedItem();
            if (cajero == null) {
                throw new RuntimeException("Error: no hay cajeros registrados.");
            }

            BigDecimal monto = new BigDecimal(montoTexto.getText().trim());
            Factura factura = ControllerFactura.emitirFactura(
                    pedido, cajero, (MetodoPago) metodoCombo.getSelectedItem(), monto);

            BigDecimal cambio = monto.subtract(factura.getTotal());
            JOptionPane.showMessageDialog(this,
                    "Factura " + factura.getNumero() + " emitida.\n\n"
                            + "Subtotal:  " + ComponentesGUI.moneda(factura.getSubtotal()) + "\n"
                            + "Impuestos: " + ComponentesGUI.moneda(factura.getImpuestos()) + "\n"
                            + "Total:     " + ComponentesGUI.moneda(factura.getTotal()) + "\n"
                            + "Cambio:    " + ComponentesGUI.moneda(cambio) + "\n\n"
                            + "La mesa " + pedido.getMesa().getNumero() + " quedó LIBRE.",
                    "Factura emitida", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El monto recibido debe ser un número válido.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}

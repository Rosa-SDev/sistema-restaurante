package view;

import controller.ControllerFactura;
import controller.ControllerMesa;
import controller.ControllerPedido;
import controller.ControllerPlatillo;
import model.EstadoMesa;
import model.EstadoPedido;
import model.Factura;
import model.Pedido;
import model.Platillo;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Descuentos sobre la carta y reporte de totales del restaurante.
 *
 * Todo el dinero es BigDecimal y se muestra con ComponentesGUI.moneda(), que
 * usa el formato de moneda colombiano. Aqui no se convierte nada a double.
 */
public class GUICalculos extends JFrame {

    private static final int DECIMALES = 2;
    private static final BigDecimal DESCUENTO_MAXIMO = new BigDecimal("50");

    private JTextField idTexto, nombreTexto, porcentajeTexto;
    private JTextArea reporte;

    public GUICalculos() {
        ComponentesGUI.configurar(this, "Calculos y reportes", 620, 560);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();
        porcentajeTexto = ComponentesGUI.campoTexto();
        porcentajeTexto.setText("10");

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Id del platillo:", "o Nombre del platillo:", "Descuento (%):"},
                new JComponent[]{idTexto, nombreTexto, porcentajeTexto});

        JButton descuentoBTN = new JButton("Aplicar descuento");
        descuentoBTN.addActionListener(e -> aplicarDescuento());
        JButton totalesBTN = new JButton("Calcular totales");
        totalesBTN.addActionListener(e -> calcularTotales());

        reporte = new JTextArea();
        reporte.setEditable(false);
        reporte.setFont(new Font("Consolas", Font.PLAIN, 13));
        reporte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(EstilosGUI.COLOR_CLARO);
        panelNorte.add(ComponentesGUI.titulo("Descuentos y totales del restaurante"), BorderLayout.NORTH);
        panelNorte.add(formulario, BorderLayout.CENTER);
        panelNorte.add(ComponentesGUI.panelBotones(descuentoBTN, totalesBTN), BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(reporte), BorderLayout.CENTER);

        calcularTotales();
    }

    private void aplicarDescuento() {
        try {
            Platillo platillo = buscarPlatillo();
            if (platillo == null) {
                return;
            }

            BigDecimal porcentaje = new BigDecimal(porcentajeTexto.getText().trim());
            if (porcentaje.compareTo(BigDecimal.ZERO) <= 0
                    || porcentaje.compareTo(DESCUENTO_MAXIMO) > 0) {
                throw new RuntimeException("Error: el descuento debe estar entre 1% y 50%.");
            }

            // Aqui habia un "if (!(platillo instanceof IDescontable))". Sobra:
            // Platillo implementa IDescontable, asi que la condicion nunca se
            // cumplia. Un tipo descontable mas volveria util la comprobacion.
            BigDecimal precioAnterior = platillo.getPrecio();
            BigDecimal descuento = platillo.aplicarDescuento(porcentaje);

            // El descuento cambia el objeto; se avisa al controlador para que
            // notifique a los observadores y las tablas abiertas se repinten.
            ControllerPlatillo.actualizarPlatillo(platillo);

            JOptionPane.showMessageDialog(this,
                    platillo.getNombre() + "\n\n"
                            + "Precio anterior: " + ComponentesGUI.moneda(precioAnterior) + "\n"
                            + "Descuento (" + porcentaje + "%): -" + ComponentesGUI.moneda(descuento) + "\n"
                            + "Precio nuevo:    " + ComponentesGUI.moneda(platillo.getPrecio()),
                    "Descuento aplicado", JOptionPane.INFORMATION_MESSAGE);

            calcularTotales();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id y el porcentaje deben ser numeros validos.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    /** Busca por Id si viene, si no por nombre. Avisa y devuelve null si no hay nada. */
    private Platillo buscarPlatillo() {
        if (!idTexto.getText().trim().isEmpty()) {
            Platillo platillo = ControllerPlatillo.buscarPlatillo(
                    Integer.parseInt(idTexto.getText().trim()));
            if (platillo == null) {
                ComponentesGUI.aviso(this, "No se encontro un platillo con ese Id.");
            }
            return platillo;
        }

        if (!nombreTexto.getText().trim().isEmpty()) {
            java.util.List<Platillo> encontrados =
                    ControllerPlatillo.buscarPlatillo(nombreTexto.getText().trim());
            if (encontrados.isEmpty()) {
                ComponentesGUI.aviso(this, "No se encontro un platillo con ese nombre.");
                return null;
            }
            return encontrados.get(0);
        }

        ComponentesGUI.aviso(this, "Escriba el Id o el nombre del platillo.");
        return null;
    }

    private void calcularTotales() {
        StringBuilder texto = new StringBuilder();
        texto.append("REPORTE DEL RESTAURANTE\n");
        texto.append("=".repeat(50)).append("\n\n");

        texto.append("FACTURACION\n");
        texto.append(linea("Total facturado (sin anuladas)", ComponentesGUI.moneda(totalFacturado())));
        texto.append(linea("Facturas emitidas", String.valueOf(ControllerFactura.listarFacturas().size())));
        texto.append(linea("Facturas anuladas", String.valueOf(facturasAnuladas())));
        texto.append("\n");

        texto.append("PEDIDOS\n");
        texto.append(linea("Pedidos registrados", String.valueOf(ControllerPedido.listarPedidos().size())));
        texto.append(linea("Abiertos", String.valueOf(pedidosEn(EstadoPedido.ABIERTO))));
        texto.append(linea("Pagados", String.valueOf(pedidosEn(EstadoPedido.PAGADO))));
        texto.append(linea("Cancelados", String.valueOf(pedidosEn(EstadoPedido.CANCELADO))));
        texto.append("\n");

        texto.append("CARTA\n");
        texto.append(linea("Platillos en la carta", String.valueOf(ControllerPlatillo.listarPlatillos().size())));
        texto.append(linea("Disponibles", String.valueOf(platillosDisponibles())));
        texto.append(linea("Precio promedio", ComponentesGUI.moneda(precioPromedio())));
        texto.append("\n");

        texto.append("SALON\n");
        texto.append(linea("Mesas totales", String.valueOf(ControllerMesa.listarMesas().size())));
        texto.append(linea("Libres", String.valueOf(mesasEn(EstadoMesa.LIBRE))));
        texto.append(linea("Ocupadas", String.valueOf(mesasEn(EstadoMesa.OCUPADA))));
        texto.append(linea("Reservadas", String.valueOf(mesasEn(EstadoMesa.RESERVADA))));

        reporte.setText(texto.toString());
        reporte.setCaretPosition(0);
    }

    private String linea(String concepto, String valor) {
        return String.format("  %-34s %s%n", concepto, valor);
    }

    private BigDecimal totalFacturado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Factura factura : ControllerFactura.listarFacturas()) {
            if (!factura.isAnulada()) {
                total = total.add(factura.getTotal());
            }
        }
        return total.setScale(DECIMALES, RoundingMode.HALF_UP);
    }

    private int facturasAnuladas() {
        int anuladas = 0;
        for (Factura factura : ControllerFactura.listarFacturas()) {
            if (factura.isAnulada()) {
                anuladas++;
            }
        }
        return anuladas;
    }

    private int pedidosEn(EstadoPedido estado) {
        return ControllerPedido.buscarPedido(estado).size();
    }

    private int platillosDisponibles() {
        int disponibles = 0;
        for (Platillo platillo : ControllerPlatillo.listarPlatillos()) {
            if (platillo.isDisponible()) {
                disponibles++;
            }
        }
        return disponibles;
    }

    private BigDecimal precioPromedio() {
        java.util.List<Platillo> platillos = ControllerPlatillo.listarPlatillos();
        if (platillos.isEmpty()) {
            return BigDecimal.ZERO.setScale(DECIMALES);
        }
        BigDecimal suma = BigDecimal.ZERO;
        for (Platillo platillo : platillos) {
            suma = suma.add(platillo.getPrecio());
        }
        return suma.divide(new BigDecimal(platillos.size()), DECIMALES, RoundingMode.HALF_UP);
    }

    private int mesasEn(EstadoMesa estado) {
        return ControllerMesa.buscarMesa(estado.name()).size();
    }
}

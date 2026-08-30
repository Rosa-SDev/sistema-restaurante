package controller;

import model.Cajero;
import model.EstadoPedido;
import model.Factura;
import model.IActualizable;
import model.MetodoPago;
import model.Pedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Emision y anulacion de facturas.
 *
 * Este controlador es quien orquesta el cobro completo: emite la factura, cobra
 * el total CON impuestos, registra el pago en el pedido y solo entonces manda
 * cerrarlo. La liberacion de la mesa no se repite aqui: vive en
 * ControllerPedido.cerrarPedido(), que a su vez se apoya en Pedido.cerrar().
 *
 * El numero de factura lo genera este controlador, no la clase Factura, tal como
 * dice el javadoc del constructor.
 */
public class ControllerFactura {

    private static List<Factura> facturas = new ArrayList<>();
    private static List<IActualizable> observadores = new ArrayList<>();

    /** Alimenta el id y el numero consecutivo de las facturas emitidas. */
    private static int consecutivo = 0;

    /**
     * Emite la factura del pedido y cobra.
     *
     * El monto se coteja contra el total CON impuestos, que es lo que de verdad
     * debe pagar el cliente. Pedido.estaPagado() compara contra el consumo sin
     * impuestos, asi que un monto que cubre esta factura lo satisface siempre.
     *
     * El orden importa: la factura se emite primero porque hasta que no se
     * calculan los impuestos no se sabe cuanto hay que cobrar, pero no se guarda
     * ni se registra pago alguno hasta comprobar que el monto alcanza. Asi un
     * cobro insuficiente no deja una factura huerfana en la lista.
     */
    public static Factura emitirFactura(Pedido pedido, Cajero cajero,
                                        MetodoPago metodoPago, BigDecimal monto) throws RuntimeException {
        if (pedido == null) {
            throw new RuntimeException("Error: pedido nulo.");
        }
        if (cajero == null) {
            throw new RuntimeException("Error: la factura necesita un cajero.");
        }
        if (metodoPago == null) {
            throw new RuntimeException("Error: la factura necesita un método de pago.");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException("Error: no se puede facturar un pedido cancelado.");
        }
        if (pedido.getPlatillos().isEmpty()) {
            throw new RuntimeException("Error: no se puede facturar un pedido sin platillos.");
        }
        Factura vigente = buscarFacturaDePedido(pedido);
        if (vigente != null) {
            throw new RuntimeException("Error: el pedido " + pedido.getId()
                    + " ya tiene la factura " + vigente.getNumero() + ".");
        }

        consecutivo++;
        Factura factura = new Factura(consecutivo, numeroDeFactura(consecutivo), pedido, cajero);
        factura.emitir();

        if (monto == null || monto.compareTo(factura.getTotal()) < 0) {
            consecutivo--;
            throw new RuntimeException("Error: el monto no cubre el total de la factura ($"
                    + factura.getTotal() + ").");
        }

        facturas.add(factura);
        pedido.registrarPago(metodoPago, monto);
        // Un pedido ya PAGADO es el que se esta refacturando tras anular su
        // factura anterior: reemitir corrige el documento, no reabre el pedido
        // ni vuelve a mover la mesa. Solo se cierra el que sigue abierto.
        if (pedido.getEstado() != EstadoPedido.PAGADO) {
            ControllerPedido.cerrarPedido(pedido);
        }
        actualizar();
        return factura;
    }

    /**
     * Marca la factura como anulada. No cancela el pedido.
     *
     * Anular es un acto contable sobre el documento: el pedido ya ocurrio y la
     * comida ya se sirvio. Cancelarlo falsearia el registro y ademas dispararia
     * Pedido.cancelar(), que libera una mesa que puede seguir ocupada. Si hay que
     * corregir, se emite una factura nueva.
     */
    public static void anularFactura(Factura factura) throws RuntimeException {
        if (factura == null) {
            throw new RuntimeException("Error: factura nula.");
        }
        if (!facturas.contains(factura)) {
            throw new RuntimeException("Error: no existe esa factura.");
        }
        if (factura.isAnulada()) {
            throw new RuntimeException("Error: la factura " + factura.getNumero() + " ya está anulada.");
        }
        factura.anular();
        actualizar();
    }

    /**
     * Suma el total de las facturas vigentes.
     *
     * Las anuladas no cuentan: siguen en la lista como constancia de lo que se
     * facturo, pero ya no representan dinero cobrado.
     */
    public static BigDecimal calcularTotalFacturado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Factura factura : facturas) {
            if (!factura.isAnulada()) {
                total = total.add(factura.getTotal());
            }
        }
        return total;
    }

    public static int contarAnuladas() {
        int anuladas = 0;
        for (Factura factura : facturas) {
            if (factura.isAnulada()) {
                anuladas++;
            }
        }
        return anuladas;
    }

    public static List<Factura> listarFacturas() {
        return new ArrayList<>(facturas);
    }

    public static Factura buscarFactura(int id) {
        for (Factura factura : facturas) {
            if (factura.getId() == id) {
                return factura;
            }
        }
        return null;
    }

    public static Factura buscarFactura(String numero) {
        for (Factura factura : facturas) {
            if (factura.getNumero().equalsIgnoreCase(numero)) {
                return factura;
            }
        }
        return null;
    }

    /**
     * La factura viva de un pedido, o null si no tiene.
     *
     * Una factura anulada no cuenta: es justo el caso en que hay que poder emitir
     * una nueva. Por eso este es el metodo que decide si un pedido "ya esta
     * facturado", y por eso Pedido no necesita ningun campo para saberlo.
     */
    public static Factura buscarFacturaDePedido(Pedido pedido) {
        for (Factura factura : facturas) {
            if (factura.getPedido() == pedido && !factura.isAnulada()) {
                return factura;
            }
        }
        return null;
    }

    private static String numeroDeFactura(int consecutivo) {
        return String.format("F-%04d", consecutivo);
    }

    public static void addActualizable(IActualizable actualizable) {
        observadores.add(actualizable);
    }

    public static void removeActualizable(IActualizable actualizable) {
        observadores.remove(actualizable);
    }

    public static void actualizar() {
        for (IActualizable observador : observadores) {
            observador.actualizar();
        }
    }
}

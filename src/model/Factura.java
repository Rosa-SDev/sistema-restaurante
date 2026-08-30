package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Factura de un pedido.
 *
 * Aqui es donde viven los impuestos: Pedido.calcularTotal() devuelve el consumo
 * limpio (decision C) y esta clase le aplica el impuesto al consumo y congela las
 * tres cifras. El metodo de pago no esta aqui, esta en Pedido.
 */
public class Factura {

    /** Decision G. Constante y con dos decimales, nunca un double. */
    public static final BigDecimal IMPUESTO_CONSUMO = new BigDecimal("0.08");

    /** Todos los importes se guardan con dos decimales. */
    private static final int DECIMALES = 2;

    private int id;
    private String numero;
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private boolean anulada;

    private Pedido pedido;   // relacion 6
    private Cajero cajero;   // relacion 7

    /**
     * El numero lo genera ControllerFactura, no la clase.
     *
     * Los tres importes nacen en 0.00 y no en null. Cuando eran double Java los
     * inicializaba solo; BigDecimal es un objeto, asi que hay que hacerlo a mano
     * o una factura sin emitir hace reventar a cualquier tabla que opere con
     * ellos. Se les pone escala 2 desde el principio para que la tabla no muestre
     * "0" en unas filas y "0.00" en otras.
     */
    public Factura(int id, String numero, Pedido pedido, Cajero cajero) {
        this.id = id;
        this.numero = numero;
        this.pedido = pedido;
        this.cajero = cajero;
        this.subtotal = BigDecimal.ZERO.setScale(DECIMALES);
        this.impuestos = BigDecimal.ZERO.setScale(DECIMALES);
        this.total = BigDecimal.ZERO.setScale(DECIMALES);
        this.anulada = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    /** Null mientras la factura no se haya emitido: es una ausencia real, no un cero. */
    public LocalDateTime getFecha() {
        return fecha;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public boolean isAnulada() {
        return anulada;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Cajero getCajero() {
        return cajero;
    }

    public void setCajero(Cajero cajero) {
        this.cajero = cajero;
    }

    /**
     * Congela las tres cifras y la fecha. Es la unica via de escritura de
     * subtotal, impuestos, total y fecha, que por eso no tienen setter.
     *
     * Sobre la escala: subtotal llega ya en escala 2, que es el contrato de
     * Pedido.calcularTotal(), y no se vuelve a redondear. Los impuestos son la
     * unica linea que lo necesita, porque multiply SUMA las escalas de los
     * operandos: escala 2 por escala 2 da escala 4, o sea cuatro decimales en una
     * factura. El total no lleva setScale porque add toma la mayor de las dos
     * escalas y ambas ya son 2.
     *
     * Los impuestos se redondean ANTES de sumarlos para que el total sea, por
     * construccion, la suma exacta de las dos cifras que se imprimen. Asi el
     * cuadre no depende de que dos formas distintas de calcularlo coincidan.
     *
     * No llama a pedido.cerrar(): la factura no gobierna el ciclo de vida del
     * pedido. Esa orquestacion es de Cajero.emitirFactura().
     *
     * Ojo en las vistas: emitir() rellena fecha, pero antes vale null. Una
     * columna que formatee la fecha tiene que contemplarlo.
     */
    public void emitir() {
        this.fecha = LocalDateTime.now();
        this.subtotal = pedido.calcularTotal();
        this.impuestos = subtotal.multiply(IMPUESTO_CONSUMO)
                                 .setScale(DECIMALES, RoundingMode.HALF_UP);
        this.total = subtotal.add(impuestos);
    }

    /**
     * Marca la factura como anulada y nada mas. No toca subtotal, impuestos ni
     * total: anular deja constancia de que la factura ya no vale, no borra cuanto
     * decia. El prototipo anterior ponia el total en cero y con eso perdia el
     * historico de lo que se habia facturado.
     */
    public void anular() {
        this.anulada = true;
    }

    @Override
    public String toString() {
        return "Factura " + numero
                + " - subtotal $" + subtotal
                + " - impuestos $" + impuestos
                + " - total $" + total
                + (anulada ? " - ANULADA" : "");
    }
}

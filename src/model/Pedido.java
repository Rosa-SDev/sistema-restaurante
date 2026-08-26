package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de una mesa.
 *
 * No existe DetallePedido: la lista guarda Platillo con repeticion, de modo que
 * dos hamburguesas son dos elementos. La "Cantidad" vive en la vista y el bucle
 * que repite el platillo lo hace el controlador.
 *
 * El total que calcula esta clase es el consumo SIN impuestos. Los impuestos y
 * el total definitivo son responsabilidad de Factura.
 */
public class Pedido {

    /** Todos los importes se devuelven con dos decimales. */
    private static final int DECIMALES = 2;

    private int id;
    private LocalDateTime fechaHora;
    private EstadoPedido estado;
    private String observaciones;

    private MetodoPago metodoPago;
    private BigDecimal montoPagado;
    private LocalDateTime fechaPago;

    private Mesa mesa;          // relacion 3
    private Mesero mesero;      // relacion 1
    private Cocinero cocinero;  // relacion 5
    private Cliente cliente;    // relacion 2, puede ser null
    private List<Platillo> platillos;

    /**
     * El cocinero no entra por aqui: cuando el mesero toma el pedido todavia no
     * se sabe quien lo va a preparar. Se asigna en iniciarPreparacion().
     */
    public Pedido(int id, LocalDateTime fechaHora, Mesa mesa, Mesero mesero,
                  Cliente cliente, String observaciones) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.mesa = mesa;
        this.mesero = mesero;
        this.cliente = cliente;
        this.observaciones = observaciones;
        this.estado = EstadoPedido.ABIERTO;
        this.platillos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Mesero getMesero() {
        return mesero;
    }

    public void setMesero(Mesero mesero) {
        this.mesero = mesero;
    }

    public Cocinero getCocinero() {
        return cocinero;
    }

    /** Lo llama Cocinero.iniciarPreparacion(): ahi es cuando la relacion 5 se vuelve real. */
    public void setCocinero(Cocinero cocinero) {
        this.cocinero = cocinero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    /**
     * Devuelve una copia. La lista interna no sale de la clase: en el prototipo
     * anterior getDetalles() exponia la lista real y cualquiera podia mutarla.
     */
    public List<Platillo> getPlatillos() {
        return new ArrayList<>(platillos);
    }

    /** Agrega una unidad. Repetir el platillo es como se expresa la cantidad. */
    public void agregarPlatillo(Platillo pl) {
        platillos.add(pl);
    }

    /**
     * Quita UNA sola unidad, no todas las iguales: List.remove(Object) borra la
     * primera coincidencia y termina. Por eso no se usa removeAll ni removeIf,
     * que se llevarian las dos hamburguesas de golpe.
     */
    public void quitarPlatillo(Platillo pl) {
        platillos.remove(pl);
    }

    /**
     * Suma de los precios de los platillos, SIN impuestos (decision C).
     * Un pedido vacio devuelve 0.00, nunca null: la escala es siempre 2 para que
     * Factura y las vistas reciban siempre el mismo formato.
     */
    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Platillo pl : platillos) {
            total = total.add(pl.getPrecio());
        }
        return total.setScale(DECIMALES, RoundingMode.HALF_UP);
    }

    /**
     * Registra el hecho contable del pago: cuanto, como y cuando. No cambia el
     * estado; cerrar() es la transicion, y quien orquesta las dos es
     * Cajero.cobrar(). Es la unica via para tocar estos tres campos, que por eso
     * no tienen setter.
     */
    public void registrarPago(MetodoPago met, BigDecimal monto) {
        this.metodoPago = met;
        this.montoPagado = monto;
        this.fechaPago = LocalDateTime.now();
    }

    /**
     * Verdadero cuando hay un pago registrado que cubre al menos el consumo sin
     * impuestos. El cotejo contra el total CON impuestos le toca a Factura, que
     * es quien los conoce; por eso aqui la comparacion es >= y no ==.
     *
     * Los importes se comparan con compareTo y nunca con equals: equals tiene en
     * cuenta la escala, asi que 10.0 y 10.00 no serian iguales.
     */
    public boolean estaPagado() {
        if (metodoPago == null || montoPagado == null) {
            return false;
        }
        return montoPagado.compareTo(calcularTotal()) >= 0;
    }

    /**
     * Transicion terminal del pedido. Libera la mesa porque la relacion 3 se
     * propaga desde el lado que la conoce, igual que Reserva.confirmar() deja la
     * mesa en RESERVADA; si Reserva propagara y Pedido no, dos clases hermanas se
     * comportarian distinto sin motivo. No es validacion, asi que no choca con la
     * decision F: si se puede cerrar o no lo decide ControllerPedido.
     *
     * Ocupar la mesa no esta aqui: el diagrama no tiene un metodo abrir() donde
     * colgarlo y un constructor no deberia tener efectos sobre otro objeto, asi
     * que ocupar() lo llama ControllerPedido al crear el pedido.
     */
    public void cerrar() {
        this.estado = EstadoPedido.PAGADO;
        if (mesa != null) {
            mesa.liberar();
        }
    }

    /** Anula el pedido y libera la mesa, por el mismo motivo que cerrar(). */
    public void cancelar() {
        this.estado = EstadoPedido.CANCELADO;
        if (mesa != null) {
            mesa.liberar();
        }
    }

    @Override
    public String toString() {
        return "Pedido #" + id
                + " - " + estado
                + " - mesa " + (mesa != null ? mesa.getNumero() : "-")
                + " - " + platillos.size() + " platillos"
                + " - $" + calcularTotal();
    }
}

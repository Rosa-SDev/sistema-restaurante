package model;

import java.time.LocalDateTime;

/**
 * Reserva de una mesa a nombre de un cliente.
 *
 * El cliente es OBLIGATORIO: la relacion 8 es de cardinalidad 1, a diferencia de
 * Pedido.cliente, que es 0..1 y admite null porque a una mesa puede sentarse
 * alguien que no esta registrado. Una reserva sin titular no significa nada.
 *
 * Ya no existe CanalReserva: la clase desaparecio del diagrama corregido.
 *
 * La comprobacion numPersonas <= mesa.getCapacidad() que hacia el validar() del
 * prototipo anterior se muda a ControllerReserva en la Fase 2, junto con la de
 * que la mesa no este ya ocupada al confirmar. Aqui no se valida (decision F).
 */
public class Reserva {

    private int id;
    private LocalDateTime fechaHora;
    private int numPersonas;
    private EstadoReserva estado;

    private Cliente cliente;  // relacion 8, obligatorio
    private Mesa mesa;        // relacion 9
    private Mesero mesero;    // relacion 10

    public Reserva(int id, LocalDateTime fechaHora, int numPersonas,
                   Cliente cliente, Mesa mesa, Mesero mesero) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.numPersonas = numPersonas;
        this.cliente = cliente;
        this.mesa = mesa;
        this.mesero = mesero;
        this.estado = EstadoReserva.PENDIENTE;
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

    public int getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    /**
     * Por aqui es como se alcanza CUMPLIDA: el diagrama solo da confirmar() y
     * cancelar(), asi que ControllerReserva marca la reserva como cumplida cuando
     * el cliente llega y se le abre el pedido. Que la reserva se cumplio es una
     * regla de aplicacion, y esas viven en el controlador.
     */
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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

    /**
     * Confirma la reserva y retiene la mesa. Es incondicional a proposito: si la
     * mesa ya estuviera ocupada, la operacion tiene que rechazarse con un mensaje
     * para el usuario, y eso es validacion, que por la decision F le toca a
     * ControllerReserva. Una guarda aqui dejaria la reserva CONFIRMADA pero sin
     * mesa retenida, y en silencio, que es peor.
     */
    public void confirmar() {
        this.estado = EstadoReserva.CONFIRMADA;
        if (mesa != null) {
            mesa.setEstado(EstadoMesa.RESERVADA);
        }
    }

    /**
     * Cancela la reserva y suelta la mesa.
     *
     * Solo libera si la mesa esta en RESERVADA para no liberar una mesa que ya
     * esta ocupada por otro pedido: una reserva PENDIENTE nunca llego a retener
     * la mesa, y si entretanto llegaron comensales sin reserva, un liberar()
     * incondicional dejaria en LIBRE una mesa con gente sentada y pedido abierto.
     *
     * Es la unica desviacion de ARQUITECTURA.md en esta clase, que pide liberar
     * sin condicion; en el caso normal el comportamiento es identico.
     */
    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
        if (mesa != null && mesa.getEstado() == EstadoMesa.RESERVADA) {
            mesa.liberar();
        }
    }

    @Override
    public String toString() {
        return "Reserva #" + id
                + " - " + estado
                + " - " + (cliente != null ? cliente.getNombre() : "-")
                + " - mesa " + (mesa != null ? mesa.getNumero() : "-")
                + " - " + numPersonas + " personas"
                + " - " + fechaHora;
    }
}

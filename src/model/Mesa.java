package model;

/**
 * Mesa fisica del restaurante. Su estado cambia por tres caminos:
 * al abrirse un pedido (ocupar), al cerrarse o cancelarse (liberar)
 * y al confirmarse una reserva (setEstado con RESERVADA).
 */
public class Mesa {

    private int id;
    private int numero;
    private int capacidad;
    private EstadoMesa estado;

    public Mesa(int id, int numero, int capacidad, EstadoMesa estado) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoMesa getEstado() {
        return estado;
    }

    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }

    public void ocupar() {
        this.estado = EstadoMesa.OCUPADA;
    }

    public void liberar() {
        this.estado = EstadoMesa.LIBRE;
    }

    @Override
    public String toString() {
        return "Mesa " + numero + " (" + capacidad + " personas - " + estado + ")";
    }
}

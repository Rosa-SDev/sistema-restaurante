package model;

import java.time.LocalDateTime;

public class Mesero extends Usuario {

    public Mesero(int id, String nombre, String correo, String passwordHash) {
        super(id, nombre, correo, passwordHash);
    }

    // Se delega al controlador
    public Pedido crearPedido( Mesa m, Cliente c ) {
        return null;
    }

    // Se delega al controlador
    public Reserva registrarReserva(Cliente c, Mesa m, LocalDateTime fechaHora, int numPersonas ) {
        return null;
    }

    // Se delega al controlador
    public void cerrarpedido( Pedido p ) {

    }

    @Override
    public String toString() {
        return "Mesero: " + super.toString();
    }
}

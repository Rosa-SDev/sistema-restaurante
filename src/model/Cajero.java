package model;

public class Cajero extends Usuario {

    public Cajero(int id, String nombre, String correo, String passwordHash) {
        super(id, nombre, correo, passwordHash);
    }

    // Se delega al controlador, que calcula el total con impuestos
    public void cobrar(Pedido p, MetodoPago met) {

    }

    // Se delega al controlador
    public Factura emitirFactura( Pedido p ) {
        return null;
    }

    @Override
    public String toString() {
        return "Cajero: " + super.toString();
    }
}

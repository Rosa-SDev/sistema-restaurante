package model;

public class Cajero extends Usuario {

    public Cajero(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    // Registra el pago en el pedido
    public void cobrar( Pedido p, MetodoPago met ) {
        p.registrarPago(met, p.calcularTotal());
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

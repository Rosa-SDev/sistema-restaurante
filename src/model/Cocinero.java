package model;

public class Cocinero extends Usuario {

    public Cocinero(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    // Asigna este cocinero al pedido y lo pone en preparación
    public void iniciarPreparacion( Pedido p ) {
        p.setCocinero(this);
        p.setEstado(EstadoPedido.EN_PREPARACION);
    }

    // Marca el pedido como servido
    public void marcarServido( Pedido p ) {
        p.setEstado(EstadoPedido.SERVIDO);
    }

    @Override
    public String toString() {
        return "Cocinero: " + super.toString();
    }
}

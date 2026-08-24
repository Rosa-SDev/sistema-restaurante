package model;

public class Cajero extends Usuario {

    public Cajero(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    @Override
    public String toString() {
        return "Cajero: " + super.toString();
    }
}

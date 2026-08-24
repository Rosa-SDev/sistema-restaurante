package model;

public class Mesero extends Usuario {

    public Mesero(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    @Override
    public String toString() {
        return "Mesero: " + super.toString();
    }
}

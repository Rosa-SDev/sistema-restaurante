package model;

public class Cocinero extends Usuario {

    public Cocinero(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    @Override
    public String toString() {
        return "Cocinero: " + super.toString();
    }
}

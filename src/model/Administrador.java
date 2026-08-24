package model;

public class Administrador extends Usuario {

    public Administrador(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    @Override
    public String toString() {
        return "Administrador: " + super.toString();
    }

}

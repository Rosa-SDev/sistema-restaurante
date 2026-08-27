package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Administrador extends Usuario {

    public Administrador(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    // Se delega al controlador
    public void registrarUsuario( Usuario u ) {

    }

    // Se delega al controlador
    public void actualizarCarta( Platillo pl ) {

    }

    // Se delega al controlador
    public BigDecimal generarReporteVentas( LocalDate desde, LocalDate hasta ) {
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Administrador: " + super.toString();
    }

}

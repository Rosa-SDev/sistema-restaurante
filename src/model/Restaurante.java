package model;

import java.time.LocalDate;

public class Restaurante {

    private static Restaurante restaurante;

    private String razonSocial;
    private LocalDate fechaFundacion;
    private String nit;
    private String direccion;

    private Restaurante() {

    }

    // Singleton
    public static Restaurante getInstancia() {
        if (restaurante == null) {
            restaurante = new Restaurante();
            restaurante.setRazonSocial("Restaurante Sabor y Fuego S.A.S.");
            restaurante.setNit("901234567-8");
            restaurante.setFechaFundacion(LocalDate.of(2015,3,12));
            restaurante.setDireccion("Calle 5 # 12-34, Neiva");
        }

        return restaurante;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public LocalDate getFechaFundacion() {
        return fechaFundacion;
    }

    public void setFechaFundacion(LocalDate fechaFundacion) {
        this.fechaFundacion = fechaFundacion;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Restaurante{" +
                "\nrazonSocial='" + razonSocial + '\'' +
                ",\nnit='" + nit + '\'' +
                ",\nfechaFundacion=" + fechaFundacion +
                ",\ndireccion='" + direccion + '\'' +
                "\n}";
    }
}

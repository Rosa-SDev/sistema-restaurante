package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Un plato de la carta.
 *
 * La categoria es un String, no una clase: el diagrama corregido elimino
 * CategoriaPlatillo. El precio es BigDecimal, nunca double, porque los double
 * acumulan error de redondeo en operaciones de dinero.
 */
public class Platillo implements IDescontable {

    /** Todos los importes se guardan con dos decimales. */
    private static final int DECIMALES = 2;
    private static final BigDecimal CIEN = new BigDecimal("100");

    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precio;
    private boolean disponible;

    public Platillo(int id, String nombre, String descripcion, String categoria,
                    BigDecimal precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = normalizar(precio);
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    /** Operacion del diagrama. El precio no tiene setter: se cambia por aqui. */
    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        this.precio = normalizar(nuevoPrecio);
    }

    /** Operacion del diagrama. */
    public void cambiarDisponibilidad(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Rebaja el precio en el porcentaje indicado y devuelve cuanto se desconto.
     * Es la implementacion de IDescontable.
     */
    @Override
    public BigDecimal aplicarDescuento(BigDecimal porcentaje) {
        BigDecimal descuento = precio.multiply(porcentaje)
                                     .divide(CIEN, DECIMALES, RoundingMode.HALF_UP);
        this.precio = precio.subtract(descuento);
        return descuento;
    }

    /** Deja todos los importes con la misma escala para que las comparaciones sean fiables. */
    private static BigDecimal normalizar(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(DECIMALES)
                             : valor.setScale(DECIMALES, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}

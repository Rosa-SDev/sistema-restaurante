package view;

/**
 * Una columna de una tabla: el titulo que se ve en la cabecera y como se saca
 * el valor de una fila del dominio.
 *
 * Es la abstraccion de la que depende {@link AdaptadorTablaModelo}: gracias a
 * ella el adaptador no conoce Mesa, ni Pedido, ni ninguna clase del dominio.
 *
 * No lleva @FunctionalInterface: esa anotacion exige un unico metodo abstracto
 * y aqui hay dos.
 */
public interface Columna<T> {

    String titulo();

    Object valor(T fila);
}

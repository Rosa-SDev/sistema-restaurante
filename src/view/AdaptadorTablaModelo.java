package view;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Patron Adapter (Object Adapter).
 *
 *   Target  = javax.swing.table.TableModel, que es lo que Swing exige para
 *             pintar una JTable y no podemos modificar.
 *   Adaptee = List<T>, que es lo que producen los controladores del dominio.
 *   Adapter = esta clase, que traduce una cosa en la otra.
 *
 * Antes cada ventana de listado repetia su propio construirModelo(...); ahora
 * todas comparten esta clase y solo declaran sus columnas.
 *
 * El adaptador no importa ni una clase del paquete model: depende solo de la
 * abstraccion Columna<T>. Ese es el ejemplo de inversion de dependencias.
 */
public class AdaptadorTablaModelo<T> extends AbstractTableModel {

    private final List<T> datos;
    private final List<Columna<T>> columnas;

    public AdaptadorTablaModelo(List<T> datos, List<Columna<T>> columnas) {
        // copia defensiva: la tabla no se altera si despues cambia la lista original
        this.datos = new ArrayList<>(datos);
        this.columnas = new ArrayList<>(columnas);
    }

    /** Fabrica corta para declarar columnas de forma legible en las ventanas. */
    public static <T> Columna<T> col(String titulo, Function<T, Object> extractor) {
        return new Columna<T>() {
            @Override
            public String titulo() {
                return titulo;
            }

            @Override
            public Object valor(T fila) {
                return extractor.apply(fila);
            }
        };
    }

    @Override
    public int getRowCount() {
        return datos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.size();
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return columnas.get(columna).valor(datos.get(fila));
    }

    @Override
    public String getColumnName(int columna) {
        return columnas.get(columna).titulo();
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}

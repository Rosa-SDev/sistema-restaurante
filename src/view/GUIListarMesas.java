package view;

import controller.ControllerMesa;
import model.IActualizable;
import model.Mesa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Listado de mesas. Es observador del controlador (patron Observer): cuando
 * otra ventana cambia el estado de una mesa, esta tabla se repinta sola.
 *
 * El registro se deshace en windowClosed para no dejar observadores muertos
 * acumulados en el controlador cada vez que se abre y cierra la ventana.
 */
public class GUIListarMesas extends JFrame implements IActualizable {

    private JTable tabla;
    private JLabel resumen;

    public GUIListarMesas() {
        ComponentesGUI.configurar(this, "Listado de mesas", 520, 380);

        tabla = ComponentesGUI.tabla();
        resumen = new JLabel();
        resumen.setHorizontalAlignment(JLabel.CENTER);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        add(ComponentesGUI.titulo("Mesas del restaurante"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(resumen, BorderLayout.SOUTH);

        actualizar();
        ControllerMesa.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerMesa.removeActualizable(GUIListarMesas.this);
            }
        });
    }

    /** Columnas de una tabla de mesas. Compartidas con la ventana de busqueda. */
    public static List<Columna<Mesa>> columnas() {
        return List.of(
                col("ID", Mesa::getId),
                col("Numero", Mesa::getNumero),
                col("Capacidad", Mesa::getCapacidad),
                col("Estado", Mesa::getEstado));
    }

    @Override
    public void actualizar() {
        tabla.setModel(new AdaptadorTablaModelo<>(ControllerMesa.listarMesas(), columnas()));
        resumen.setText("Mesas libres: " + ControllerMesa.listarMesasLibres().size()
                + " de " + ControllerMesa.listarMesas().size());
    }
}

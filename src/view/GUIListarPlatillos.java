package view;

import controller.ControllerPlatillo;
import model.IActualizable;
import model.Platillo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Listado de la carta. Observador del controlador: se repinta sola cuando
 * cualquier otra ventana agrega, actualiza o elimina un platillo.
 */
public class GUIListarPlatillos extends JFrame implements IActualizable {

    private JTable tabla;
    private JLabel resumen;

    public GUIListarPlatillos() {
        ComponentesGUI.configurar(this, "Carta del restaurante", 700, 420);

        tabla = ComponentesGUI.tabla();
        resumen = new JLabel();
        resumen.setHorizontalAlignment(JLabel.CENTER);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        add(ComponentesGUI.titulo("Carta del restaurante"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(resumen, BorderLayout.SOUTH);

        actualizar();
        ControllerPlatillo.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerPlatillo.removeActualizable(GUIListarPlatillos.this);
            }
        });
    }

    /** Columnas de una tabla de platillos. Compartidas con la ventana de busqueda. */
    public static List<Columna<Platillo>> columnas() {
        return List.of(
                col("ID", Platillo::getId),
                col("Nombre", Platillo::getNombre),
                col("Categoria", Platillo::getCategoria),
                col("Descripcion", Platillo::getDescripcion),
                col("Precio", p -> ComponentesGUI.moneda(p.getPrecio())),
                col("Disponible", p -> p.isDisponible() ? "Si" : "No"));
    }

    @Override
    public void actualizar() {
        List<Platillo> platillos = ControllerPlatillo.listarPlatillos();
        tabla.setModel(new AdaptadorTablaModelo<>(platillos, columnas()));
        resumen.setText(String.format("Platillos: %d  |  Disponibles: %d  |  Precio promedio: %s",
                platillos.size(),
                ControllerPlatillo.contarDisponibles(),
                ComponentesGUI.moneda(ControllerPlatillo.calcularPrecioPromedio())));
    }
}
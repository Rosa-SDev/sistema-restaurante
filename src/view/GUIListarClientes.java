package view;

import controller.ControllerCliente;
import model.Cliente;
import model.IActualizable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

/**
 * Listado de clientes. Es observador del controlador (patron Observer):
 * cuando otra ventana cambia un cliente, esta tabla se repinta sola.
 *
 * El registro se deshace en windowClosed para no dejar observadores muertos
 * acumulados en el controlador cada vez que se abre y cierra la ventana.
 */
public class GUIListarClientes extends JFrame implements IActualizable {

    private JTable tabla;

    public GUIListarClientes() {
        ComponentesGUI.configurar(this, "Listado de clientes", 560, 380);

        tabla = ComponentesGUI.tabla();

        add(ComponentesGUI.titulo("Clientes registrados"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        actualizar();
        ControllerCliente.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerCliente.removeActualizable(GUIListarClientes.this);
            }
        });
    }

    /** Columnas de una tabla de clientes. Compartidas con la ventana de busqueda. */
    public static List<Columna<Cliente>> columnas() {
        return List.of(
                col("ID", Cliente::getId),
                col("Nombre", Cliente::getNombre),
                col("Documento", Cliente::getDocumento),
                col("Teléfono", Cliente::getTelefono));
    }

    @Override
    public void actualizar() {
        tabla.setModel(new AdaptadorTablaModelo<>(ControllerCliente.listarClientes(), columnas()));
    }
}
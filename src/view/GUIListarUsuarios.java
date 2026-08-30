package view;

import controller.ControllerUsuario;
import model.IActualizable;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static view.AdaptadorTablaModelo.col;

public class GUIListarUsuarios extends JFrame implements IActualizable {

    private JTable tabla;

    public GUIListarUsuarios() {
        ComponentesGUI.configurar(this, "Listado de usuarios", 700, 400);

        tabla = ComponentesGUI.tabla();

        add(ComponentesGUI.titulo("Usuarios registrados"), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        actualizar();
        ControllerUsuario.addActualizable(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ControllerUsuario.removeActualizable(GUIListarUsuarios.this);
            }
        });
    }

    public static List<Columna<Usuario>> columnas() {
        return List.of(
                col("ID", Usuario::getId),
                col("Nombre", Usuario::getNombre),
                col("Correo", Usuario::getCorreo),
                col("Rol", u -> u.getClass().getSimpleName()),
                col("Activo", u -> u.isActivo() ? "Sí" : "No"));
    }

    @Override
    public void actualizar() {
        tabla.setModel(new AdaptadorTablaModelo<>(ControllerUsuario.listarUsuarios(), columnas()));
    }
}

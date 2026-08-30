package view;

import controller.ControllerUsuario;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class GUIEliminarUsuario extends JFrame {

    private JTextField idTexto, nombreTexto;

    public GUIEliminarUsuario() {
        ComponentesGUI.configurar(this, "Eliminar usuario", 460, 260);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:", "Nombre:"},
                new JComponent[]{idTexto, nombreTexto});

        JButton eliminarBTN = new JButton("Eliminar");
        eliminarBTN.addActionListener(e -> eliminar());

        add(ComponentesGUI.titulo("Ingrese el ID o el nombre del usuario a eliminar"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(eliminarBTN), BorderLayout.SOUTH);
    }

    private void eliminar() {
        try {
            if (!idTexto.getText().trim().isEmpty()) {
                int id = Integer.parseInt(idTexto.getText().trim());
                Usuario usuario = ControllerUsuario.buscarUsuario(id);
                if (usuario == null) {
                    throw new RuntimeException("Error: no se encontró un usuario con ese ID.");
                }
                if (!ComponentesGUI.confirmar(this, "¿Seguro que desea eliminar a " + usuario.getNombre() + "?")) {
                    return;
                }
                ControllerUsuario.eliminarUsuario(id);

            } else if (!nombreTexto.getText().trim().isEmpty()) {
                String nombre = nombreTexto.getText().trim();
                if (ControllerUsuario.buscarUsuario(nombre).isEmpty()) {
                    throw new RuntimeException("Error: no se encontró un usuario con ese nombre.");
                }
                if (!ComponentesGUI.confirmar(this, "¿Seguro que desea eliminar a " + nombre + "?")) {
                    return;
                }
                ControllerUsuario.eliminarUsuario(nombre);

            } else {
                ComponentesGUI.aviso(this, "Escriba un ID o un nombre.");
                return;
            }

            ComponentesGUI.exito(this, "Usuario eliminado correctamente.");
            idTexto.setText("");
            nombreTexto.setText("");

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}

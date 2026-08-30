package view;

import controller.ControllerUsuario;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class GUIActualizarUsuario extends JFrame {

    private JTextField idTexto;

    public GUIActualizarUsuario() {
        ComponentesGUI.configurar(this, "Actualizar usuario", 440, 220);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();

        JPanel campos = ComponentesGUI.formulario(
                new String[]{"ID:"},
                new JComponent[]{idTexto});

        JButton cargarBTN = new JButton("Cargar datos");
        cargarBTN.addActionListener(e -> cargar());

        add(ComponentesGUI.titulo("Ingrese el ID del usuario que desea actualizar"), BorderLayout.NORTH);
        add(campos, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(cargarBTN), BorderLayout.SOUTH);
    }

    private void cargar() {
        try {
            Usuario usuario = ControllerUsuario.buscarUsuario(Integer.parseInt(idTexto.getText().trim()));

            if (usuario == null) {
                ComponentesGUI.aviso(this, "No se encontró un usuario con ese ID.");
                return;
            }

            GUIAgregarUsuario formulario = new GUIAgregarUsuario(true);
            formulario.cargarDatos(usuario);
            formulario.setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        }
    }
}

package view;

import controller.ControllerUsuario;
import model.Administrador;
import model.Cajero;
import model.Cocinero;
import model.Mesero;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class GUIAgregarUsuario extends JFrame {

    private static final String[] ROLES = {"Administrador", "Mesero", "Cocinero", "Cajero"};

    private JTextField idTexto, nombreTexto, correoTexto;
    private JPasswordField passwordTexto;
    private JComboBox<String> rolCombo;

    private final boolean esActualizar;

    public GUIAgregarUsuario( boolean esActualizar ) {
        this.esActualizar = esActualizar;
        String accion = esActualizar ? "Actualizar" : "Agregar";

        ComponentesGUI.configurar( this, accion + " usuario", 480, 340);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();
        correoTexto = ComponentesGUI.campoTexto();
        passwordTexto = new JPasswordField();
        passwordTexto.setBorder(EstilosGUI.GRAY_BORDER);
        rolCombo = ComponentesGUI.combo(ROLES);

        JPanel formulario = ComponentesGUI.formulario(
                new String[] {"Rol:", "ID:", "Nombre:", "Correo:", "Contraseña:"},
                new JComponent[] {rolCombo, idTexto, nombreTexto, correoTexto, passwordTexto}
        );

        JButton guardarBTN = new JButton(accion);
        guardarBTN.addActionListener( e -> guardar());

        add(ComponentesGUI.titulo(accion + " usuario"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(guardarBTN), BorderLayout.SOUTH);
    }

    private void guardar() {
        try {
            int id = Integer.parseInt(idTexto.getText().trim());
            String nombre = nombreTexto.getText().trim();
            String correo = correoTexto.getText().trim();
            String password = new String(passwordTexto.getPassword()).trim();

            // Aqui es donde existe la clave en texto plano, asi que es el unico
            // sitio que puede comprobar su longitud. Al actualizar, vacia significa
            // "no la cambies", asi que solo se exige al crear.
            if (!esActualizar && password.isEmpty()) {
                throw new RuntimeException("Error: la contrasena es obligatoria.");
            }

            if (esActualizar) {
                Usuario existente = ControllerUsuario.buscarUsuario(id);
                if (existente == null) {
                    throw new RuntimeException("Error: no existe un usuario con ese ID.");
                }

                existente.setNombre(nombre);
                existente.setCorreo(correo);

                if (!password.isEmpty()) {
                    existente.cambiarPassword(ControllerUsuario.hash(password));
                }

                ControllerUsuario.actualizarUsuario(existente);
                ComponentesGUI.exito(this, "Usuario actualizado con éxito.");

            } else {

                Usuario usuario = switch (rolCombo.getSelectedIndex()) {
                    case 0 -> new Administrador(id, nombre, correo, ControllerUsuario.hash(password));
                    case 1 -> new Mesero(id, nombre, correo, ControllerUsuario.hash(password));
                    case 2 -> new Cocinero(id, nombre, correo, ControllerUsuario.hash(password));
                    default -> new Cajero(id, nombre, correo, ControllerUsuario.hash(password));
                };

                ControllerUsuario.agregarUsuario(usuario);
                ComponentesGUI.exito(this, "Usuario agregado con éxito.");

            }
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");

        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());

        }
    }

    public void cargarDatos(Usuario usuario) {
        idTexto.setText(String.valueOf(usuario.getId()));
        idTexto.setEditable(false);
        nombreTexto.setText(usuario.getNombre());
        correoTexto.setText(usuario.getCorreo());
        // La contraseña no se muestra: el hash no se pinta en pantalla
        rolCombo.setSelectedItem(usuario.getClass().getSimpleName());
        rolCombo.setEnabled(false);

    }
}

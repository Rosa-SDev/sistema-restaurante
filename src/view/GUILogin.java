package view;

import controller.ControllerUsuario;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesion. Es el punto de entrada de la aplicacion:
 * hasta que alguien se autentica no se abre GUIPrincipal.
 *
 * El usuario conectado no se guarda aqui sino en Sesion, que es quien
 * mantiene el estado mientras la aplicacion esta abierta.
 */
public class GUILogin extends JFrame {

    private JTextField correoTexto;
    private JPasswordField claveTexto;

    public GUILogin() {
        ComponentesGUI.configurar(this, "Iniciar sesion", 420, 260);
        // Cerrar el login es salir de la aplicacion: todavia no hay nada abierto
        // detras, asi que reemplaza el DISPOSE_ON_CLOSE que deja configurar().
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        correoTexto = ComponentesGUI.campoTexto();
        claveTexto = new JPasswordField();
        claveTexto.setBorder(EstilosGUI.GRAY_BORDER);

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Correo:", "Contrasena:"},
                new JComponent[]{correoTexto, claveTexto});

        JButton entrarBTN = new JButton("Entrar");
        entrarBTN.addActionListener(e -> entrar());

        add(ComponentesGUI.titulo("Sistema de Restaurante"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(entrarBTN), BorderLayout.SOUTH);

        // Enter dentro del formulario equivale a pulsar Entrar
        getRootPane().setDefaultButton(entrarBTN);
    }

    private void entrar() {
        Usuario usuario = ControllerUsuario.autenticar(
                correoTexto.getText().trim(),
                new String(claveTexto.getPassword()));

        // Un unico mensaje para los tres casos: correo que no existe, clave
        // equivocada y usuario inactivo. Decir cual de ellos fallo le confirma
        // a cualquiera que correos estan registrados.
        if (usuario == null) {
            ComponentesGUI.error(this, "Correo o contrasena incorrectos.");
            claveTexto.setText("");
            return;
        }

        Sesion.iniciar(usuario);
        new GUIPrincipal().setVisible(true);
        dispose();
    }
}

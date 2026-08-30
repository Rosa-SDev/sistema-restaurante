package view;

import controller.ControllerCliente;
import model.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Pide el Id del cliente y abre el formulario de alta en modo actualizar,
 * ya con los datos cargados.
 */
public class GUIActualizarCliente extends JFrame {

    private JTextField idTexto;

    public GUIActualizarCliente() {
        ComponentesGUI.configurar(this, "Actualizar cliente", 440, 220);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:"},
                new JComponent[]{idTexto});

        JButton cargarBTN = new JButton("Cargar datos");
        cargarBTN.addActionListener(e -> cargar());

        add(ComponentesGUI.titulo("Ingrese el ID del cliente que desea actualizar"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(cargarBTN), BorderLayout.SOUTH);
    }

    private void cargar() {
        try {
            Cliente cliente = ControllerCliente.buscarCliente(Integer.parseInt(idTexto.getText().trim()));

            if (cliente == null) {
                ComponentesGUI.aviso(this, "No se encontró un cliente con ese ID.");
                return;
            }

            GUIAgregarCliente formulario = new GUIAgregarCliente(true);
            formulario.cargarDatos(cliente);
            formulario.setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        }
    }
}
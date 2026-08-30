package view;

import controller.ControllerCliente;
import model.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de alta de clientes. La misma ventana sirve para actualizar:
 * el constructor recibe que modo es, y cargarDatos() rellena los campos.
 */
public class GUIAgregarCliente extends JFrame {

    private JTextField idTexto, nombreTexto, documentoTexto, telefonoTexto;

    private final boolean esActualizar;

    public GUIAgregarCliente(boolean esActualizar) {
        this.esActualizar = esActualizar;
        String accion = esActualizar ? "Actualizar" : "Agregar";

        ComponentesGUI.configurar(this, accion + " cliente", 440, 320);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();
        documentoTexto = ComponentesGUI.campoTexto();
        telefonoTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:", "Nombre:", "Documento:", "Teléfono:"},
                new JComponent[]{idTexto, nombreTexto, documentoTexto, telefonoTexto});

        JButton guardarBTN = new JButton(accion);
        guardarBTN.addActionListener(e -> guardar());

        add(ComponentesGUI.titulo(accion + " cliente"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(guardarBTN), BorderLayout.SOUTH);
    }

    private void guardar() {
        try {
            Cliente cliente = new Cliente(
                    Integer.parseInt(idTexto.getText().trim()),
                    nombreTexto.getText().trim(),
                    documentoTexto.getText().trim(),
                    telefonoTexto.getText().trim());

            if (esActualizar) {
                ControllerCliente.actualizarCliente(cliente);
                ComponentesGUI.exito(this, "Cliente actualizado con éxito.");
            } else {
                ControllerCliente.agregarCliente(cliente);
                ComponentesGUI.exito(this, "Cliente agregado con éxito.");
            }
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    public void cargarDatos(Cliente cliente) {
        idTexto.setText(String.valueOf(cliente.getId()));
        idTexto.setEditable(false);
        nombreTexto.setText(cliente.getNombre());
        documentoTexto.setText(cliente.getDocumento());
        telefonoTexto.setText(cliente.getTelefono());
    }
}
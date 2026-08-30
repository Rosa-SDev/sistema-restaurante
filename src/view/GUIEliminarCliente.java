package view;

import controller.ControllerCliente;
import model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Baja de clientes por Id o por nombre.
 */
public class GUIEliminarCliente extends JFrame {

    private JTextField idTexto, nombreTexto;

    public GUIEliminarCliente() {
        ComponentesGUI.configurar(this, "Eliminar cliente", 460, 260);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:", "Nombre:"},
                new JComponent[]{idTexto, nombreTexto});

        JButton eliminarBTN = new JButton("Eliminar");
        eliminarBTN.addActionListener(e -> eliminar());

        add(ComponentesGUI.titulo("Ingrese el ID o el nombre del cliente a eliminar"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(eliminarBTN), BorderLayout.SOUTH);
    }

    private void eliminar() {
        try {
            if (!idTexto.getText().trim().isEmpty()) {
                int id = Integer.parseInt(idTexto.getText().trim());
                Cliente cliente = ControllerCliente.buscarCliente(id);
                if (cliente == null) {
                    throw new RuntimeException("Error: no se encontró un cliente con ese ID.");
                }
                if (!ComponentesGUI.confirmar(this, "¿Seguro que desea eliminar a " + cliente.getNombre() + "?")) {
                    return;
                }
                ControllerCliente.eliminarCliente(id);

            } else if (!nombreTexto.getText().trim().isEmpty()) {
                String nombre = nombreTexto.getText().trim();
                List<Cliente> encontrados = ControllerCliente.buscarCliente(nombre);
                if (encontrados.isEmpty()) {
                    throw new RuntimeException("Error: no se encontró un cliente con ese nombre.");
                }
                if (!ComponentesGUI.confirmar(this, "¿Seguro que desea eliminar a " + nombre + "?")) {
                    return;
                }
                ControllerCliente.eliminarCliente(nombre);

            } else {
                ComponentesGUI.aviso(this, "Escriba un ID o un nombre.");
                return;
            }

            ComponentesGUI.exito(this, "Cliente eliminado correctamente.");
            idTexto.setText("");
            nombreTexto.setText("");

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}
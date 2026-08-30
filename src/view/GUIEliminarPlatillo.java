package view;

import controller.ControllerPlatillo;
import model.Platillo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Baja de platillos por Id o por nombre.
 */
public class GUIEliminarPlatillo extends JFrame {

    private JTextField idTexto, nombreTexto;

    public GUIEliminarPlatillo() {
        ComponentesGUI.configurar(this, "Eliminar platillo", 460, 260);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:", "Nombre:"},
                new JComponent[]{idTexto, nombreTexto});

        JButton eliminarBTN = new JButton("Eliminar");
        eliminarBTN.addActionListener(e -> eliminar());

        add(ComponentesGUI.titulo("Ingrese el ID o el nombre del platillo a eliminar"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(eliminarBTN), BorderLayout.SOUTH);
    }

    private void eliminar() {
        try {
            if (!idTexto.getText().trim().isEmpty()) {
                int id = Integer.parseInt(idTexto.getText().trim());
                Platillo platillo = ControllerPlatillo.buscarPlatillo(id);
                if (platillo == null) {
                    throw new RuntimeException("Error: no se encontró un platillo con ese ID.");
                }
                if (!ComponentesGUI.confirmar(this, "¿Seguro que desea eliminar " + platillo.getNombre() + "?")) {
                    return;
                }
                ControllerPlatillo.eliminarPlatillo(id);

            } else if (!nombreTexto.getText().trim().isEmpty()) {
                String nombre = nombreTexto.getText().trim();
                List<Platillo> encontrados = ControllerPlatillo.buscarPlatillo(nombre);
                if (encontrados.isEmpty()) {
                    throw new RuntimeException("Error: no se encontró un platillo con ese nombre.");
                }
                if (!ComponentesGUI.confirmar(this, "¿Seguro que desea eliminar " + nombre + "?")) {
                    return;
                }
                ControllerPlatillo.eliminarPlatillo(nombre);

            } else {
                ComponentesGUI.aviso(this, "Escriba un ID o un nombre.");
                return;
            }

            ComponentesGUI.exito(this, "Platillo eliminado correctamente.");
            idTexto.setText("");
            nombreTexto.setText("");

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}
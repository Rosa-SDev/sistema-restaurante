package view;

import controller.ControllerPlatillo;
import model.Platillo;

import javax.swing.*;
import java.awt.*;

/**
 * Pide el Id del platillo y abre el formulario de alta en modo actualizar.
 */
public class GUIActualizarPlatillo extends JFrame {

    private JTextField idTexto;

    public GUIActualizarPlatillo() {
        ComponentesGUI.configurar(this, "Actualizar platillo", 440, 220);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:"},
                new JComponent[]{idTexto});

        JButton cargarBTN = new JButton("Cargar datos");
        cargarBTN.addActionListener(e -> cargar());

        add(ComponentesGUI.titulo("Ingrese el ID del platillo que desea actualizar"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(cargarBTN), BorderLayout.SOUTH);
    }

    private void cargar() {
        try {
            Platillo platillo = ControllerPlatillo.buscarPlatillo(Integer.parseInt(idTexto.getText().trim()));

            if (platillo == null) {
                ComponentesGUI.aviso(this, "No se encontró un platillo con ese ID.");
                return;
            }

            GUIAgregarPlatillo formulario = new GUIAgregarPlatillo(true);
            formulario.cargarDatos(platillo);
            formulario.setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        }
    }
}
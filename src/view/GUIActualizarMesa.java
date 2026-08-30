package view;

import controller.ControllerMesa;
import model.Mesa;

import javax.swing.*;
import java.awt.*;

/**
 * Pide el Id de la mesa y abre el formulario de alta en modo actualizar,
 * ya con los datos cargados.
 */
public class GUIActualizarMesa extends JFrame {

    private JTextField idTexto;

    public GUIActualizarMesa() {
        ComponentesGUI.configurar(this, "Actualizar mesa", 440, 220);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();

        JPanel campos = ComponentesGUI.formulario(
                new String[]{"ID:"},
                new JComponent[]{idTexto});

        JButton cargarBTN = new JButton("Cargar datos");
        cargarBTN.addActionListener(e -> cargar());

        add(ComponentesGUI.titulo("Ingrese el ID de la mesa que desea actualizar"), BorderLayout.NORTH);
        add(campos, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(cargarBTN), BorderLayout.SOUTH);
    }

    private void cargar() {
        try {
            Mesa mesa = ControllerMesa.buscarMesa(Integer.parseInt(idTexto.getText().trim()));

            if (mesa == null) {
                ComponentesGUI.aviso(this, "No se encontró una mesa con ese ID.");
                return;
            }

            GUIAgregarMesa formulario = new GUIAgregarMesa(true);
            formulario.cargarDatos(mesa);
            formulario.setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID debe ser un número entero.");
        }
    }
}

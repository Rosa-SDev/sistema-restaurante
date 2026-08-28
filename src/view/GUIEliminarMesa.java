package view;

import controller.ControllerMesa;
import model.Mesa;

import javax.swing.*;
import java.awt.*;

/**
 * Baja de mesas por Id. El controlador impide borrar una mesa que no
 * este LIBRE; aqui solo se muestra el mensaje que lanza.
 */
public class GUIEliminarMesa extends JFrame {

    private JTextField idTexto;

    public GUIEliminarMesa() {
        ComponentesGUI.configurar(this, "Eliminar mesa", 440, 230);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Id:"},
                new JComponent[]{idTexto});

        JButton eliminarBTN = new JButton("Eliminar");
        eliminarBTN.addActionListener(e -> eliminar());

        add(ComponentesGUI.titulo("Ingrese el Id de la mesa a eliminar"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(eliminarBTN), BorderLayout.SOUTH);
    }

    private void eliminar() {
        try {
            int id = Integer.parseInt(idTexto.getText().trim());
            Mesa mesa = ControllerMesa.buscarMesa(id);

            if (mesa == null) {
                throw new RuntimeException("Error: no se encontro una mesa con ese ID.");
            }
            if (!ComponentesGUI.confirmar(this, "Seguro que desea eliminar la mesa " + mesa.getNumero() + "?")) {
                return;
            }

            ControllerMesa.eliminarMesa(id);
            ComponentesGUI.exito(this, "Mesa eliminada correctamente.");
            idTexto.setText("");

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id debe ser un numero entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}

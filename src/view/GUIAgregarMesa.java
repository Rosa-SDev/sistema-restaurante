package view;

import controller.ControllerMesa;
import model.EstadoMesa;
import model.Mesa;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de alta de mesas. La misma ventana sirve para actualizar:
 * el constructor recibe que modo es, y cargarDatos() rellena los campos.
 */
public class GUIAgregarMesa extends JFrame {

    private JTextField idTexto, numeroTexto, capacidadTexto;
    private JComboBox<EstadoMesa> estadoCombo;

    private final boolean esActualizar;

    public GUIAgregarMesa(boolean esActualizar) {
        this.esActualizar = esActualizar;
        String accion = esActualizar ? "Actualizar" : "Agregar";

        ComponentesGUI.configurar(this, accion + " mesa", 440, 320);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        numeroTexto = ComponentesGUI.campoTexto();
        capacidadTexto = ComponentesGUI.campoTexto();
        estadoCombo = ComponentesGUI.combo(EstadoMesa.values());

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:", "Número:", "Capacidad:", "Estado:"},
                new JComponent[]{idTexto, numeroTexto, capacidadTexto, estadoCombo});

        JButton guardarBTN = new JButton(accion);
        guardarBTN.addActionListener(e -> guardar());

        add(ComponentesGUI.titulo(accion + " mesa"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(guardarBTN), BorderLayout.SOUTH);
    }

    private void guardar() {
        try {
            Mesa mesa = new Mesa(
                    Integer.parseInt(idTexto.getText().trim()),
                    Integer.parseInt(numeroTexto.getText().trim()),
                    Integer.parseInt(capacidadTexto.getText().trim()),
                    (EstadoMesa) estadoCombo.getSelectedItem());

            if (esActualizar) {
                ControllerMesa.actualizarMesa(mesa);
                ComponentesGUI.exito(this, "Mesa actualizada con éxito.");
            } else {
                ControllerMesa.agregarMesa(mesa);
                ComponentesGUI.exito(this, "Mesa agregada con éxito.");
            }
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El ID, el número y la capacidad deben ser números enteros.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    public void cargarDatos(Mesa mesa) {
        idTexto.setText(String.valueOf(mesa.getId()));
        idTexto.setEditable(false);
        numeroTexto.setText(String.valueOf(mesa.getNumero()));
        capacidadTexto.setText(String.valueOf(mesa.getCapacidad()));
        estadoCombo.setSelectedItem(mesa.getEstado());
    }
}

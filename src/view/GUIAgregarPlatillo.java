package view;

import controller.ControllerPlatillo;
import model.Platillo;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Formulario de alta de platillos. La misma ventana sirve para actualizar.
 *
 * La categoria es un campo de texto libre, no un combo: el modelo corregido
 * elimino CategoriaPlatillo (decision del equipo), asi que aqui no hay
 * ControllerCategoria del que tirar.
 */
public class GUIAgregarPlatillo extends JFrame {

    private JTextField idTexto, nombreTexto, descripcionTexto, categoriaTexto, precioTexto;
    private JCheckBox disponibleCasilla;

    private final boolean esActualizar;

    public GUIAgregarPlatillo(boolean esActualizar) {
        this.esActualizar = esActualizar;
        String accion = esActualizar ? "Actualizar" : "Agregar";

        ComponentesGUI.configurar(this, accion + " platillo", 480, 380);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        nombreTexto = ComponentesGUI.campoTexto();
        descripcionTexto = ComponentesGUI.campoTexto();
        categoriaTexto = ComponentesGUI.campoTexto();
        precioTexto = ComponentesGUI.campoTexto();
        disponibleCasilla = ComponentesGUI.casilla("Si");
        disponibleCasilla.setSelected(true);

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Id:", "Nombre:", "Descripcion:", "Categoria:", "Precio:", "Disponible:"},
                new JComponent[]{idTexto, nombreTexto, descripcionTexto, categoriaTexto,
                        precioTexto, disponibleCasilla});

        JButton guardarBTN = new JButton(accion);
        guardarBTN.addActionListener(e -> guardar());

        add(ComponentesGUI.titulo(accion + " platillo"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(guardarBTN), BorderLayout.SOUTH);
    }

    private void guardar() {
        try {
            Platillo platillo = new Platillo(
                    Integer.parseInt(idTexto.getText().trim()),
                    nombreTexto.getText().trim(),
                    descripcionTexto.getText().trim(),
                    categoriaTexto.getText().trim(),
                    new BigDecimal(precioTexto.getText().trim()),
                    disponibleCasilla.isSelected());

            if (esActualizar) {
                ControllerPlatillo.actualizarPlatillo(platillo);
                ComponentesGUI.exito(this, "Platillo actualizado con exito.");
            } else {
                ControllerPlatillo.agregarPlatillo(platillo);
                ComponentesGUI.exito(this, "Platillo agregado con exito.");
            }
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id debe ser entero y el precio un numero valido.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    public void cargarDatos(Platillo platillo) {
        idTexto.setText(String.valueOf(platillo.getId()));
        idTexto.setEditable(false);
        nombreTexto.setText(platillo.getNombre());
        descripcionTexto.setText(platillo.getDescripcion());
        categoriaTexto.setText(platillo.getCategoria());
        precioTexto.setText(platillo.getPrecio().toPlainString());
        disponibleCasilla.setSelected(platillo.isDisponible());
    }
}
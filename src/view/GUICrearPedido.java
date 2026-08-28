package view;

import controller.ControllerCliente;
import controller.ControllerMesa;
import controller.ControllerPedido;
import controller.ControllerUsuario;
import model.Cliente;
import model.Mesa;
import model.Mesero;
import model.Pedido;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Alta de un pedido: mesa, mesero y, si se conoce, cliente.
 *
 * El cocinero no se elige aqui. Cuando el mesero toma la comanda todavia no se
 * sabe quien la va a preparar: se asigna en Cocinero.iniciarPreparacion().
 *
 * El cliente es opcional (cardinalidad 0..1), por eso el combo trae una opcion
 * "Sin cliente". Es justo lo contrario de la reserva, donde es obligatorio.
 */
public class GUICrearPedido extends JFrame {

    private static final String SIN_CLIENTE = "Sin cliente";

    private JTextField idTexto, observacionesTexto;
    private JComboBox<Mesa> mesaCombo;
    private JComboBox<Mesero> meseroCombo;
    private JComboBox<Cliente> clienteCombo;

    public GUICrearPedido() {
        ComponentesGUI.configurar(this, "Crear pedido", 500, 330);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        observacionesTexto = ComponentesGUI.campoTexto();

        mesaCombo = new JComboBox<>();
        mesaCombo.setBorder(EstilosGUI.GRAY_BORDER);
        // Una mesa RESERVADA acepta pedido: esta esperando a alguien y abrirle la
        // cuenta al llegar es la transicion normal. Solo se excluyen las OCUPADAS.
        for (Mesa mesa : ControllerMesa.buscarMesa("LIBRE")) {
            mesaCombo.addItem(mesa);
        }
        for (Mesa mesa : ControllerMesa.buscarMesa("RESERVADA")) {
            mesaCombo.addItem(mesa);
        }

        meseroCombo = new JComboBox<>();
        meseroCombo.setBorder(EstilosGUI.GRAY_BORDER);
        meseroCombo.setRenderer(ComponentesGUI.renderer((Mesero m) -> m.getNombre()));
        for (Mesero mesero : ControllerUsuario.listarMeseros()) {
            meseroCombo.addItem(mesero);
        }

        clienteCombo = new JComboBox<>();
        clienteCombo.setBorder(EstilosGUI.GRAY_BORDER);
        clienteCombo.setRenderer(ComponentesGUI.renderer(
                (Cliente c) -> c == null ? SIN_CLIENTE : c.getNombre()));
        clienteCombo.addItem(null);
        for (Cliente cliente : ControllerCliente.listarClientes()) {
            clienteCombo.addItem(cliente);
        }

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"Id del pedido:", "Mesa:", "Mesero:", "Cliente (opcional):", "Observaciones:"},
                new JComponent[]{idTexto, mesaCombo, meseroCombo, clienteCombo, observacionesTexto});

        JButton crearBTN = new JButton("Crear pedido");
        crearBTN.addActionListener(e -> crear());

        add(ComponentesGUI.titulo("Crear pedido"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(crearBTN), BorderLayout.SOUTH);
    }

    private void crear() {
        try {
            Mesa mesa = (Mesa) mesaCombo.getSelectedItem();
            Mesero mesero = (Mesero) meseroCombo.getSelectedItem();

            if (mesa == null) {
                throw new RuntimeException("Error: no hay mesas disponibles para abrir un pedido.");
            }
            if (mesero == null) {
                throw new RuntimeException("Error: no hay meseros registrados.");
            }

            Pedido pedido = new Pedido(
                    Integer.parseInt(idTexto.getText().trim()),
                    LocalDateTime.now(),
                    mesa,
                    mesero,
                    (Cliente) clienteCombo.getSelectedItem(),
                    observacionesTexto.getText().trim());

            ControllerPedido.crearPedido(pedido);
            ComponentesGUI.exito(this, "Pedido creado. La mesa " + mesa.getNumero() + " quedo OCUPADA.\n"
                    + "Use 'Gestionar pedido' para agregarle platillos.");
            dispose();

        } catch (NumberFormatException ex) {
            ComponentesGUI.error(this, "El Id debe ser un numero entero.");
        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }
}

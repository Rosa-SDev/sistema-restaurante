package view;

import controller.ControllerCliente;
import controller.ControllerMesa;
import controller.ControllerReserva;
import controller.ControllerUsuario;
import model.Cliente;
import model.Mesa;
import model.Mesero;
import model.Reserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class GUIRegistrarReserva extends JFrame {

    private JTextField idTexto, numPersonasTexto;
    private JComboBox<Cliente> clienteCombo;
    private JComboBox<Mesa> mesaCombo;
    private JComboBox<Mesero> meseroCombo;
    private JSpinner fechaHoraSpinner;

    public GUIRegistrarReserva() {
        ComponentesGUI.configurar(this, "Registrar reserva", 500, 380);
        setResizable(false);

        idTexto = ComponentesGUI.campoTexto();
        numPersonasTexto = ComponentesGUI.campoTexto();

        clienteCombo = new JComboBox<>();
        clienteCombo.setBorder(EstilosGUI.GRAY_BORDER);
        clienteCombo.setRenderer(ComponentesGUI.renderer(Cliente::getNombre));
        for (Cliente cliente : ControllerCliente.listarClientes()) {
            clienteCombo.addItem(cliente);
        }

        mesaCombo = new JComboBox<>();
        mesaCombo.setBorder(EstilosGUI.GRAY_BORDER);
        for (Mesa mesa : ControllerMesa.listarMesas()) {
            mesaCombo.addItem(mesa);
        }

        meseroCombo = new JComboBox<>();
        meseroCombo.setBorder(EstilosGUI.GRAY_BORDER);
        meseroCombo.setRenderer(ComponentesGUI.renderer(Mesero::getNombre));
        for (Mesero mesero : ControllerUsuario.listarMeseros()) {
            meseroCombo.addItem(mesero);
        }

        // Arranca una hora por delante: una reserva se toma para mas tarde, y con
        // "ahora" por defecto el propio instante de abrir la ventana ya seria
        // pasado al pulsar Registrar. El modelo se deja sin minimo a proposito,
        // para poder bajar a una fecha pasada y provocar el rechazo.
        fechaHoraSpinner = new JSpinner(new SpinnerDateModel());
        fechaHoraSpinner.setValue(Date.from(LocalDateTime.now().plusHours(1)
                .atZone(ZoneId.systemDefault()).toInstant()));
        fechaHoraSpinner.setEditor(new JSpinner.DateEditor(fechaHoraSpinner, "dd/MM/yyyy HH:mm"));
        fechaHoraSpinner.setBorder(EstilosGUI.GRAY_BORDER);

        JPanel formulario = ComponentesGUI.formulario(
                new String[]{"ID:", "Cliente:", "Mesa:", "Mesero:", "Fecha y hora:", "Núm. personas:"},
                new JComponent[]{idTexto, clienteCombo, mesaCombo, meseroCombo,
                        fechaHoraSpinner, numPersonasTexto});

        JButton registrarBTN = new JButton("Registrar");
        registrarBTN.addActionListener(e -> registrar());

        add(ComponentesGUI.titulo("Registrar reserva"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(ComponentesGUI.panelBotones(registrarBTN), BorderLayout.SOUTH);
    }

    private void registrar() {
        try {
            Cliente cliente = (Cliente) clienteCombo.getSelectedItem();
            Mesa mesa = (Mesa) mesaCombo.getSelectedItem();
            Mesero mesero = (Mesero) meseroCombo.getSelectedItem();

            if (cliente == null) {
                throw new RuntimeException("Error: debe seleccionar un cliente.");
            }
            if (mesa == null) {
                throw new RuntimeException("Error: debe seleccionar una mesa.");
            }
            if (mesero == null) {
                throw new RuntimeException("Error: debe seleccionar un mesero.");
            }

            int id = enteroEnRango(idTexto, "el ID", 1, 999999);
            int numPersonas = enteroEnRango(numPersonasTexto, "el número de personas", 1, 99);

            Date fecha = (Date) fechaHoraSpinner.getValue();
            LocalDateTime fechaHora = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            Reserva reserva = new Reserva(
                    id,
                    fechaHora,
                    numPersonas,
                    cliente,
                    mesa,
                    mesero);

            ControllerReserva.registrarReserva(reserva);
            ComponentesGUI.exito(this, "Reserva registrada en estado PENDIENTE.\n"
                    + "Use 'Confirmar / cancelar' para cambiarla.");
            dispose();

        } catch (RuntimeException ex) {
            ComponentesGUI.error(this, ex.getMessage());
        }
    }

    /**
     * Lee un campo como entero dentro de un rango.
     *
     * Un solo mensaje para los dos fallos —texto que no es un numero, y numero
     * fuera del rango— porque para quien lo lee la correccion es la misma:
     * escribir un numero entre esos dos.
     */
    private int enteroEnRango(JTextField campo, String nombre, int minimo, int maximo) {
        String mensaje = "Error: " + nombre + " debe estar entre " + minimo + " y " + maximo + ".";
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException ex) {
            throw new RuntimeException(mensaje);
        }
        if (valor < minimo || valor > maximo) {
            throw new RuntimeException(mensaje);
        }
        return valor;
    }
}

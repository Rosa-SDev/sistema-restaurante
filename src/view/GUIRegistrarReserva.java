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

        fechaHoraSpinner = new JSpinner(new SpinnerDateModel());
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

            int id = entero(idTexto, "el ID");
            int numPersonas = entero(numPersonasTexto, "el número de personas");

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
     * Lee un campo como entero, nombrando el campo que falla.
     *
     * Antes los dos campos se parseaban dentro del mismo try, asi que un unico
     * catch acusaba a los dos a la vez y daba una razon que podia no ser la real.
     */
    private int entero(JTextField campo, String nombre) {
        String texto = campo.getText().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Error: " + nombre + " " + porQueNoEsEntero(texto));
        }
    }

    /** Distingue el texto que no es un numero del numero que no cabe en un int. */
    private String porQueNoEsEntero(String texto) {
        String digitos = texto.startsWith("-") ? texto.substring(1) : texto;
        boolean esNumero = !digitos.isEmpty();
        for (int i = 0; i < digitos.length(); i++) {
            if (!Character.isDigit(digitos.charAt(i))) {
                esNumero = false;
            }
        }
        if (esNumero) {
            return "no cabe en un número entero: el máximo es " + Integer.MAX_VALUE + ".";
        }
        return "debe ser un número entero.";
    }
}

package view;

import model.Restaurante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicacion: la barra de menus y el panel de bienvenida.
 *
 * Los menus de gestion nacen DESHABILITADOS a proposito. Sus ventanas todavia no
 * existen en su mayoria, y un item que llama a una clase inexistente no compila.
 * Cada uno se habilita cuando se le conecte su vista, en la fase de integracion,
 * junto con el filtrado por rol del usuario conectado.
 *
 * Por la misma razon aqui no hay "Cerrar sesion": es la unica opcion que
 * referenciaria a GUILogin, que aun no esta en develop.
 */
public class GUIPrincipal extends JFrame {

    private JMenuItem salir;
    private JMenuItem autores;
    private JMenuItem infoEmpresa;

    public GUIPrincipal() {
        ComponentesGUI.configurar(this, "Sistema de Restaurante", 600, 450);
        // La ventana principal no se cierra sin confirmar, asi que reemplaza el
        // DISPOSE_ON_CLOSE que deja configurar().
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        crearMenu();
        crearPanelInformacion();
        activarOpciones();
    }

    private void crearMenu() {
        JMenu archivo = new JMenu("Archivo");
        salir = new JMenuItem("Salir");
        archivo.add(salir);

        JMenu ayuda = new JMenu("Ayuda");
        autores = new JMenuItem("Autores");
        infoEmpresa = new JMenuItem("Restaurante");
        agregarItems(ayuda, autores, infoEmpresa);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(archivo);
        menuBar.add(menuPendiente("Usuarios", "Agregar", "Eliminar", "Actualizar", "Buscar", "Listar"));
        menuBar.add(menuPendiente("Clientes", "Agregar", "Eliminar", "Actualizar", "Buscar", "Listar"));
        menuBar.add(menuPendiente("Mesas", "Agregar", "Eliminar", "Actualizar", "Buscar", "Listar"));
        menuBar.add(menuPendiente("Carta", "Agregar platillo", "Eliminar platillo", "Actualizar platillo",
                "Buscar platillo", "Listar carta"));
        menuBar.add(menuPendiente("Pedidos", "Crear pedido", "Gestionar pedido", "Facturar pedido",
                "Buscar pedido", "Listar pedidos", "Listar facturas"));
        menuBar.add(menuPendiente("Reservas", "Registrar reserva", "Confirmar / cancelar", "Buscar reserva",
                "Listar reservas"));
        menuBar.add(menuPendiente("Operaciones", "Calculos"));
        menuBar.add(ayuda);

        setJMenuBar(menuBar);
    }

    /** Menu cuyas opciones ya se ven, pero todavia no tienen ventana que abrir. */
    private JMenu menuPendiente(String titulo, String... textos) {
        JMenuItem[] items = new JMenuItem[textos.length];
        for (int i = 0; i < textos.length; i++) {
            items[i] = new JMenuItem(textos[i]);
            items[i].setEnabled(false);
        }
        JMenu menu = new JMenu(titulo);
        agregarItems(menu, items);
        return menu;
    }

    private void agregarItems(JMenu menu, JMenuItem... items) {
        for (int i = 0; i < items.length; i++) {
            menu.add(items[i]);
            if (i < items.length - 1) {
                menu.addSeparator();
            }
        }
    }

    private void crearPanelInformacion() {
        Restaurante restaurante = Restaurante.getInstancia();

        JPanel panelDatos = new JPanel(new GridLayout(4, 1, 0, 10));
        panelDatos.setBackground(EstilosGUI.COLOR_CLARO);
        panelDatos.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));
        panelDatos.add(etiqueta("NIT: " + restaurante.getNit()));
        panelDatos.add(etiqueta("Fundacion: " + restaurante.getFechaFundacion()));
        panelDatos.add(etiqueta("Direccion: " + restaurante.getDireccion()));
        panelDatos.add(etiqueta("Use el menu superior para gestionar el restaurante"));

        add(ComponentesGUI.titulo(restaurante.getRazonSocial()), BorderLayout.NORTH);
        add(panelDatos, BorderLayout.CENTER);
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private void activarOpciones() {
        salir.addActionListener(e -> confirmarSalida());

        autores.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Rosa - Sebastian - Daniel - Danielito\n\nv.0.1",
                "Autores",
                JOptionPane.INFORMATION_MESSAGE));

        infoEmpresa.addActionListener(e -> JOptionPane.showMessageDialog(this,
                Restaurante.getInstancia().toString(),
                "Informacion del restaurante",
                JOptionPane.INFORMATION_MESSAGE));
    }

    private void confirmarSalida() {
        if (ComponentesGUI.confirmar(this, "Estas seguro de que quieres salir?")) {
            System.exit(0);
        }
    }
}

package view;

import model.Restaurante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Supplier;

/**
 * Ventana principal de la aplicacion: la barra de menus y el panel de bienvenida.
 *
 * Cada opcion de gestion abre su ventana. La ventana se construye en el momento
 * de pulsar el item, no al arrancar: si se crearan todas de una vez, cada listado
 * se registraria como observador desde el primer segundo y la aplicacion abriria
 * dos docenas de JFrame invisibles.
 *
 * El menu Reservas es la unica excepcion: sus opciones se ven pero estan
 * deshabilitadas porque todavia no existen las ventanas de reservas.
 * ControllerReserva si esta, asi que es un pendiente de vista, no de logica.
 *
 * El filtrado de estas opciones segun el rol del usuario conectado se hace
 * aparte, a partir de Sesion.getActual().
 *
 * Limitacion conocida: las ventanas ya abiertas sobreviven al cierre de sesion,
 * porque son JFrame independientes y no ventanas hijas de esta.
 */
public class GUIPrincipal extends JFrame {

    private JMenuItem cerrarSesion;
    private JMenuItem salir;
    private JMenuItem autores;
    private JMenuItem infoEmpresa;

    private JMenu menuUsuarios;
    private JMenu menuClientes;
    private JMenu menuMesas;
    private JMenu menuCarta;
    private JMenu menuPedidos;
    private JMenu menuReservas;
    private JMenu menuOperaciones;

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
        cerrarSesion = new JMenuItem("Cerrar sesion");
        salir = new JMenuItem("Salir");
        archivo.add(cerrarSesion);
        // Cerrar sesion deja la aplicacion viva y Salir la mata: no son lo mismo
        archivo.addSeparator();
        archivo.add(salir);

        JMenu ayuda = new JMenu("Ayuda");
        autores = new JMenuItem("Autores");
        infoEmpresa = new JMenuItem("Restaurante");
        agregarItems(ayuda, autores, infoEmpresa);

        menuUsuarios = menu("Usuarios",
                item("Agregar", () -> new GUIAgregarUsuario(false)),
                item("Eliminar", GUIEliminarUsuario::new),
                item("Actualizar", GUIActualizarUsuario::new),
                item("Buscar", GUIBuscarUsuario::new),
                item("Listar", GUIListarUsuarios::new));

        menuClientes = menu("Clientes",
                item("Agregar", () -> new GUIAgregarCliente(false)),
                item("Eliminar", GUIEliminarCliente::new),
                item("Actualizar", GUIActualizarCliente::new),
                item("Buscar", GUIBuscarCliente::new),
                item("Listar", GUIListarClientes::new));

        menuMesas = menu("Mesas",
                item("Agregar", () -> new GUIAgregarMesa(false)),
                item("Eliminar", GUIEliminarMesa::new),
                item("Actualizar", GUIActualizarMesa::new),
                item("Buscar", GUIBuscarMesa::new),
                item("Listar", GUIListarMesas::new));

        menuCarta = menu("Carta",
                item("Agregar platillo", () -> new GUIAgregarPlatillo(false)),
                item("Eliminar platillo", GUIEliminarPlatillo::new),
                item("Actualizar platillo", GUIActualizarPlatillo::new),
                item("Buscar platillo", GUIBuscarPlatillo::new),
                item("Listar carta", GUIListarPlatillos::new));

        menuPedidos = menu("Pedidos",
                item("Crear pedido", GUICrearPedido::new),
                item("Gestionar pedido", GUIGestionarPedido::new),
                item("Facturar pedido", GUIFacturarPedido::new),
                item("Buscar pedido", GUIBuscarPedido::new),
                item("Listar pedidos", GUIListarPedidos::new),
                item("Listar facturas", GUIListarFacturas::new));

        menuReservas = menuPendiente("Reservas", "Registrar reserva", "Confirmar / cancelar",
                "Buscar reserva", "Listar reservas");

        menuOperaciones = menu("Operaciones",
                item("Calculos", GUICalculos::new));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(archivo);
        menuBar.add(menuUsuarios);
        menuBar.add(menuClientes);
        menuBar.add(menuMesas);
        menuBar.add(menuCarta);
        menuBar.add(menuPedidos);
        menuBar.add(menuReservas);
        menuBar.add(menuOperaciones);
        menuBar.add(ayuda);

        setJMenuBar(menuBar);
    }

    /**
     * Item que abre una ventana al pulsarlo.
     *
     * Recibe un Supplier y no un JFrame ya construido para que la ventana nazca
     * en cada pulsacion: asi se abre siempre en blanco y, si el usuario la cerro,
     * la siguiente vez vuelve a aparecer.
     */
    private JMenuItem item(String texto, Supplier<JFrame> ventana) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> ventana.get().setVisible(true));
        return item;
    }

    private JMenu menu(String titulo, JMenuItem... items) {
        JMenu menu = new JMenu(titulo);
        agregarItems(menu, items);
        return menu;
    }

    /** Menu cuyas opciones ya se ven, pero todavia no tienen ventana que abrir. */
    private JMenu menuPendiente(String titulo, String... textos) {
        JMenuItem[] items = new JMenuItem[textos.length];
        for (int i = 0; i < textos.length; i++) {
            items[i] = new JMenuItem(textos[i]);
            items[i].setEnabled(false);
        }
        return menu(titulo, items);
    }

    /**
     * Agrega los items al menu, sin separadores.
     *
     * Los separadores se ponen a mano donde de verdad agrupan opciones distintas.
     * Puestos entre cada par, un menu de seis opciones sale con cinco lineas
     * dentro y no separa nada.
     */
    private void agregarItems(JMenu menu, JMenuItem... items) {
        for (JMenuItem item : items) {
            menu.add(item);
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
        cerrarSesion.addActionListener(e -> confirmarCierreDeSesion());

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

    /**
     * Cierra la sesion y devuelve el control al login.
     *
     * Cerrar sesion no es salir: la aplicacion sigue viva y otro usuario puede
     * entrar. Por eso hay dispose() y no System.exit().
     */
    private void confirmarCierreDeSesion() {
        if (ComponentesGUI.confirmar(this, "Quieres cerrar la sesion?")) {
            Sesion.cerrar();
            new GUILogin().setVisible(true);
            dispose();
        }
    }

    private void confirmarSalida() {
        if (ComponentesGUI.confirmar(this, "Estas seguro de que quieres salir?")) {
            System.exit(0);
        }
    }
}

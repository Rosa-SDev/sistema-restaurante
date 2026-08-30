package view;

import model.Administrador;
import model.Cajero;
import model.Cocinero;
import model.Mesero;
import model.Restaurante;
import model.Usuario;

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
 * Que opciones quedan habilitadas depende del rol del usuario conectado, que se
 * lee de Sesion.getActual(). Lo resuelve aplicarPermisos().
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
        aplicarPermisos();
    }

    private void crearMenu() {
        JMenu archivo = new JMenu("Archivo");
        cerrarSesion = new JMenuItem("Cerrar sesión");
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

        menuReservas = menu("Reservas",
                item("Registrar reserva", GUIRegistrarReserva::new),
                item("Gestionar reserva", GUIGestionarReserva::new),
                item("Buscar reserva", GUIBuscarReserva::new),
                item("Listar reservas", GUIListarReservas::new));

        menuOperaciones = menu("Operaciones",
                item("Cálculos", GUICalculos::new));

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

        JPanel panelDatos = new JPanel(new GridLayout(5, 1, 0, 10));
        panelDatos.setBackground(EstilosGUI.COLOR_CLARO);
        panelDatos.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));
        panelDatos.add(etiqueta(textoConectado()));
        panelDatos.add(etiqueta("NIT: " + restaurante.getNit()));
        panelDatos.add(etiqueta("Fundación: " + restaurante.getFechaFundacion()));
        panelDatos.add(etiqueta("Dirección: " + restaurante.getDireccion()));
        panelDatos.add(etiqueta("Use el menú superior para gestionar el restaurante"));

        add(ComponentesGUI.titulo(restaurante.getRazonSocial()), BorderLayout.NORTH);
        add(panelDatos, BorderLayout.CENTER);
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * Quien esta conectado y con que rol.
     *
     * El rol es el nombre de la clase del usuario: los cuatro roles no tienen
     * atributos propios, lo unico que los distingue es el tipo.
     */
    private String textoConectado() {
        Usuario usuario = Sesion.getActual();
        if (usuario == null) {
            return "Sin sesión iniciada";
        }
        return "Conectado: " + usuario.getNombre()
                + " (" + usuario.getClass().getSimpleName() + ")";
    }

    /**
     * Habilita los menus que le tocan al rol conectado.
     *
     * Se parte de todo apagado y se enciende lo justo. Si algun dia entra un rol
     * nuevo y nadie le escribe sus permisos, no vera nada: es un fallo visible.
     * Al reves, un rol sin permisos escritos lo veria todo.
     */
    private void aplicarPermisos() {
        deshabilitarTodo();

        Usuario usuario = Sesion.getActual();
        if (usuario == null) {
            return;
        }

        if (usuario instanceof Administrador) {
            habilitar(menuUsuarios);
            habilitar(menuClientes);
            habilitar(menuMesas);
            habilitar(menuCarta);
            habilitar(menuPedidos);
            habilitar(menuReservas);
            habilitar(menuOperaciones);

        } else if (usuario instanceof Mesero) {
            habilitar(menuClientes);
            habilitar(menuMesas, "Buscar", "Listar");
            habilitar(menuCarta, "Listar carta");
            habilitar(menuPedidos, "Crear pedido", "Gestionar pedido",
                    "Buscar pedido", "Listar pedidos");
            // la reserva lleva un mesero asignado, por eso el rol entra completo
            habilitar(menuReservas);

        } else if (usuario instanceof Cocinero) {
            habilitar(menuCarta, "Listar carta");
            habilitar(menuPedidos, "Buscar pedido", "Listar pedidos");

        } else if (usuario instanceof Cajero) {
            habilitar(menuClientes, "Buscar", "Listar");
            habilitar(menuCarta, "Listar carta");
            habilitar(menuPedidos, "Facturar pedido", "Buscar pedido",
                    "Listar pedidos", "Listar facturas");
            habilitar(menuOperaciones);
        }
    }

    private void deshabilitarTodo() {
        JMenu[] menus = {menuUsuarios, menuClientes, menuMesas, menuCarta,
                menuPedidos, menuReservas, menuOperaciones};
        for (JMenu menu : menus) {
            menu.setEnabled(false);
        }
    }

    /** Enciende el menu entero: el titulo y todas sus opciones. */
    private void habilitar(JMenu menu) {
        menu.setEnabled(true);
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (menu.getItem(i) != null) {
                menu.getItem(i).setEnabled(true);
            }
        }
    }

    /**
     * Enciende el menu y solo las opciones nombradas.
     *
     * Los permisos se escriben con el texto del item ("Listar carta"). Si alguien
     * renombra una opcion hay que cambiarla tambien aqui, o ese permiso deja de
     * aplicarse sin avisar.
     */
    private void habilitar(JMenu menu, String... textos) {
        menu.setEnabled(true);
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            // getItem() devuelve null en los separadores
            if (item != null) {
                item.setEnabled(contiene(textos, item.getText()));
            }
        }
    }

    private boolean contiene(String[] textos, String texto) {
        for (String candidato : textos) {
            if (candidato.equals(texto)) {
                return true;
            }
        }
        return false;
    }

    private void activarOpciones() {
        cerrarSesion.addActionListener(e -> confirmarCierreDeSesion());

        salir.addActionListener(e -> confirmarSalida());

        autores.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Rosa Peña · Sebastián Gallego · Daniel Guzmán · Daniel Vanegas\n\nv.0.1",
                "Autores",
                JOptionPane.INFORMATION_MESSAGE));

        infoEmpresa.addActionListener(e -> JOptionPane.showMessageDialog(this,
                Restaurante.getInstancia().toString(),
                "Información del restaurante",
                JOptionPane.INFORMATION_MESSAGE));
    }

    /**
     * Cierra la sesion y devuelve el control al login.
     *
     * Cerrar sesion no es salir: la aplicacion sigue viva y otro usuario puede
     * entrar. Por eso hay dispose() y no System.exit().
     */
    private void confirmarCierreDeSesion() {
        if (ComponentesGUI.confirmar(this, "¿Quieres cerrar la sesión?")) {
            Sesion.cerrar();
            new GUILogin().setVisible(true);
            dispose();
        }
    }

    private void confirmarSalida() {
        if (ComponentesGUI.confirmar(this, "¿Estás seguro de que quieres salir?")) {
            System.exit(0);
        }
    }
}

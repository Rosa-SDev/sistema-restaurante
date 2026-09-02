package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Campo de fecha y hora con calendario.
 *
 * Swing no trae un selector de fechas, asi que este componente lo resuelve sin
 * librerias externas: muestra la fecha elegida en un campo de solo lectura y,
 * al pulsar el boton, abre un dialogo con la rejilla del mes.
 *
 * El campo no se escribe a mano a proposito: si la fecha solo puede entrar por
 * el calendario, no existe la posibilidad de teclear un texto que no sea una
 * fecha valida, y desaparece toda una familia de errores de formato.
 *
 * Se usa como cualquier otro componente dentro de ComponentesGUI.formulario().
 */
public class SelectorFecha extends JPanel {

    private static final Locale ES = Locale.of("es", "CO");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] DIAS = {"L", "M", "X", "J", "V", "S", "D"};

    /** Si es false el dialogo no pide hora y la fija a las 00:00. */
    private final boolean conHora;

    private LocalDateTime fechaHora;
    private final JTextField campo;

    public SelectorFecha() {
        this(true);
    }

    public SelectorFecha(boolean conHora) {
        this.conHora = conHora;
        this.fechaHora = LocalDateTime.now().withSecond(0).withNano(0);

        setLayout(new BorderLayout(4, 0));
        setBackground(EstilosGUI.COLOR_CLARO);

        campo = new JTextField();
        campo.setEditable(false);
        campo.setBorder(EstilosGUI.GRAY_BORDER);
        campo.setBackground(Color.WHITE);

        JButton abrir = new JButton("Calendario");
        abrir.setMargin(new Insets(1, 6, 1, 6));
        abrir.addActionListener(e -> abrirCalendario());

        add(campo, BorderLayout.CENTER);
        add(abrir, BorderLayout.EAST);

        refrescarCampo();
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime valor) {
        if (valor != null) {
            this.fechaHora = valor.withSecond(0).withNano(0);
            refrescarCampo();
        }
    }

    private void refrescarCampo() {
        campo.setText(conHora
                ? fechaHora.format(FORMATO)
                : fechaHora.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void abrirCalendario() {
        DialogoCalendario dialogo = new DialogoCalendario(
                SwingUtilities.getWindowAncestor(this), fechaHora, conHora);
        dialogo.setVisible(true);
        if (dialogo.getElegida() != null) {
            setFechaHora(dialogo.getElegida());
        }
    }

    /** El calendario en si: navegacion de mes, rejilla de dias y, opcionalmente, hora. */
    private static class DialogoCalendario extends JDialog {

        private YearMonth mes;
        private LocalDate diaElegido;
        private LocalDateTime elegida;

        private final JLabel titulo = new JLabel("", SwingConstants.CENTER);
        private final JPanel rejilla = new JPanel(new GridLayout(0, 7, 2, 2));
        private final JSpinner horas;
        private final JSpinner minutos;

        DialogoCalendario(Window padre, LocalDateTime inicial, boolean conHora) {
            super(padre, "Seleccionar fecha", ModalityType.APPLICATION_MODAL);

            this.mes = YearMonth.from(inicial);
            this.diaElegido = inicial.toLocalDate();
            this.horas = new JSpinner(new SpinnerNumberModel(inicial.getHour(), 0, 23, 1));
            this.minutos = new JSpinner(new SpinnerNumberModel(inicial.getMinute(), 0, 59, 5));

            setLayout(new BorderLayout(0, 6));
            getContentPane().setBackground(EstilosGUI.COLOR_CLARO);
            setResizable(false);

            add(construirCabecera(), BorderLayout.NORTH);

            JPanel centro = new JPanel(new BorderLayout(0, 4));
            centro.setBackground(EstilosGUI.COLOR_CLARO);
            centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));
            centro.add(construirDiasDeLaSemana(), BorderLayout.NORTH);
            rejilla.setBackground(EstilosGUI.COLOR_CLARO);
            centro.add(rejilla, BorderLayout.CENTER);
            if (conHora) {
                centro.add(construirHora(), BorderLayout.SOUTH);
            }
            add(centro, BorderLayout.CENTER);

            JButton aceptar = new JButton("Aceptar");
            aceptar.addActionListener(e -> {
                LocalTime hora = conHora
                        ? LocalTime.of((Integer) horas.getValue(), (Integer) minutos.getValue())
                        : LocalTime.MIDNIGHT;
                elegida = LocalDateTime.of(diaElegido, hora);
                dispose();
            });
            JButton cancelar = new JButton("Cancelar");
            cancelar.addActionListener(e -> dispose());
            add(ComponentesGUI.panelBotones(aceptar, cancelar), BorderLayout.SOUTH);

            getRootPane().setDefaultButton(aceptar);
            pintarMes();
            pack();
            setLocationRelativeTo(padre);
        }

        LocalDateTime getElegida() {
            return elegida;
        }

        private JPanel construirCabecera() {
            JButton anterior = new JButton("<");
            anterior.addActionListener(e -> { mes = mes.minusMonths(1); pintarMes(); });
            JButton siguiente = new JButton(">");
            siguiente.addActionListener(e -> { mes = mes.plusMonths(1); pintarMes(); });

            titulo.setFont(new Font("Arial", Font.BOLD, 14));
            titulo.setForeground(EstilosGUI.COLOR);

            JPanel cabecera = new JPanel(new BorderLayout());
            cabecera.setBackground(EstilosGUI.COLOR_CLARO);
            cabecera.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
            cabecera.add(anterior, BorderLayout.WEST);
            cabecera.add(titulo, BorderLayout.CENTER);
            cabecera.add(siguiente, BorderLayout.EAST);
            return cabecera;
        }

        private JPanel construirDiasDeLaSemana() {
            JPanel fila = new JPanel(new GridLayout(1, 7, 2, 2));
            fila.setBackground(EstilosGUI.COLOR_CLARO);
            for (String d : DIAS) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(new Font("Arial", Font.BOLD, 11));
                fila.add(l);
            }
            return fila;
        }

        private JPanel construirHora() {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            fila.setBackground(EstilosGUI.COLOR_CLARO);
            fila.add(new JLabel("Hora:"));
            fila.add(horas);
            fila.add(new JLabel(":"));
            fila.add(minutos);
            return fila;
        }

        /** Redibuja la rejilla del mes actual, dejando marcado el dia elegido. */
        private void pintarMes() {
            String nombre = mes.getMonth().getDisplayName(TextStyle.FULL, ES);
            titulo.setText(nombre.substring(0, 1).toUpperCase(ES) + nombre.substring(1)
                    + " " + mes.getYear());

            rejilla.removeAll();

            // los huecos previos al dia 1: getValue() da lunes=1 ... domingo=7
            int hueco = mes.atDay(1).getDayOfWeek().getValue() - 1;
            for (int i = 0; i < hueco; i++) {
                rejilla.add(new JLabel(""));
            }

            for (int dia = 1; dia <= mes.lengthOfMonth(); dia++) {
                LocalDate fecha = mes.atDay(dia);
                JButton boton = new JButton(String.valueOf(dia));
                boton.setMargin(new Insets(2, 2, 2, 2));
                boton.setFocusable(false);
                if (fecha.equals(diaElegido)) {
                    boton.setBackground(EstilosGUI.COLOR);
                    boton.setForeground(Color.WHITE);
                    boton.setOpaque(true);
                }
                boton.addActionListener(e -> { diaElegido = fecha; pintarMes(); });
                rejilla.add(boton);
            }

            rejilla.revalidate();
            rejilla.repaint();
            pack();
        }
    }
}

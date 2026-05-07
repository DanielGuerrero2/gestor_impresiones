package proyecto_estructuras;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ventana principal de la interfaz gráfica del Sistema de Gestión de Impresión.
 * <p>
 * Extiende {@link JFrame} para proporcionar una interfaz visual interactiva que
 * permite:
 * <ul>
 * <li>Enviar trabajos de impresión inmediatos a la cola activa.</li>
 * <li>Programar trabajos para una fecha/hora futura.</li>
 * <li>Imprimir el siguiente trabajo de mayor prioridad.</li>
 * <li>Visualizar en tiempo real el estado de las colas (activa y
 * programados).</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Layout:</b> Utiliza un {@link BorderLayout} con tres zonas:
 * NORTH (formulario), CENTER (listas de colas), SOUTH (botón de impresión).
 * </p>
 *
 * <p>
 * <b>Timer:</b> Un {@link Timer} con intervalo de 2 segundos verifica
 * periódicamente
 * si algún trabajo programado debe moverse a la cola activa.
 * </p>
 *
 * @see GestorImpresion
 * @see TrabajoImpresion
 */
public class MainFrame extends JFrame {

    /** Instancia del gestor de impresión que contiene la lógica de negocio. */
    private GestorImpresion gestor;

    /** Modelo de datos para la lista visual de trabajos activos. */
    private DefaultListModel<String> activeListModel;

    /** Modelo de datos para la lista visual de trabajos programados. */
    private DefaultListModel<String> scheduledListModel;

    /** Campo de texto para ingresar el nombre del documento. */
    private JTextField docField;

    /** Selector desplegable para elegir el nivel de prioridad. */
    private JComboBox<TrabajoImpresion.Prioridad> priorityBox;

    /**
     * Spinner numérico para configurar el retraso en segundos (para trabajos
     * programados).
     */
    private JSpinner delaySpinner;

    /**
     * Crea e inicializa la ventana principal del sistema.
     * <p>
     * Configura las dimensiones, el layout, los componentes de la GUI,
     * y arranca un {@link Timer} que verifica trabajos programados cada 2 segundos.
     * </p>
     */
    public MainFrame() {
        this.gestor = new GestorImpresion();

        setTitle("Sistema de Impresión - Gestor Visual");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setLayout(new BorderLayout(10, 10));

        // Create UI components
        initUI();

        // Timer to update queue from scheduled
        Timer timer = new Timer(600000, (ActionEvent e) -> {
            gestor.verificarProgramados();
            updateLists();
        });
        timer.start();
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica.
     * <p>
     * Crea tres paneles principales:
     * <ul>
     * <li><b>Panel superior (NORTH):</b> Formulario con campo de documento,
     * selector de prioridad, spinner de retraso y botones de acción.</li>
     * <li><b>Panel central (CENTER):</b> Dos listas lado a lado mostrando
     * la cola activa y los trabajos programados.</li>
     * <li><b>Panel inferior (SOUTH):</b> Botón para imprimir el siguiente
     * trabajo.</li>
     * </ul>
     * </p>
     */
    private void initUI() {
        // --- Top Panel: Input ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nuevo Trabajo"));

        inputPanel.add(new JLabel("Documento:"));
        docField = new JTextField(12);
        inputPanel.add(docField);

        inputPanel.add(new JLabel("Prioridad:"));
        priorityBox = new JComboBox<>(TrabajoImpresion.Prioridad.values());
        inputPanel.add(priorityBox);

        inputPanel.add(new JLabel("Retraso (seg):"));
        // el JSpinner es para que el usuario pueda elegir el tiempo que quiere esperar
        // antes de que se imprima el documento
        // para trasformarlo en hora se multiplica por 3600
        delaySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 3600, 1));
        inputPanel.add(delaySpinner);

        JButton btnInmediato = new JButton("Inmediato");
        btnInmediato.addActionListener(e -> agregarInmediato());
        inputPanel.add(btnInmediato);

        JButton btnProgramar = new JButton("Programar");
        btnProgramar.addActionListener(e -> agregarProgramado());
        inputPanel.add(btnProgramar);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center Panel: Lists ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Active List
        activeListModel = new DefaultListModel<>();
        JList<String> activeList = new JList<>(activeListModel);
        JScrollPane activeScroll = new JScrollPane(activeList);
        activeScroll.setBorder(BorderFactory.createTitledBorder("Cola Activa (Ordenada por Prioridad)"));
        centerPanel.add(activeScroll);

        // Scheduled List
        scheduledListModel = new DefaultListModel<>();
        JList<String> scheduledList = new JList<>(scheduledListModel);
        JScrollPane scheduledScroll = new JScrollPane(scheduledList);
        scheduledScroll.setBorder(BorderFactory.createTitledBorder("Trabajos Programados (En espera)"));
        centerPanel.add(scheduledScroll);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel: Controls ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnImprimir = new JButton("Imprimir Siguiente");
        btnImprimir.setFont(new Font("Arial", Font.BOLD, 14));
        btnImprimir.addActionListener(e -> imprimirSiguiente());
        bottomPanel.add(btnImprimir);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Agrega un trabajo de impresión inmediato a la cola activa.
     * <p>
     * Valida que el campo de documento no esté vacío antes de enviar.
     * Muestra un diálogo de error si la validación falla.
     * </p>
     */
    private void agregarInmediato() {
        String doc = docField.getText().trim();
        if (doc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre de documento.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        TrabajoImpresion.Prioridad prio = (TrabajoImpresion.Prioridad) priorityBox.getSelectedItem();
        gestor.enviarTrabajoInmediato(doc, prio);
        docField.setText("");
        updateLists();
    }

    /**
     * Programa un trabajo de impresión para ser activado después de un retraso.
     * <p>
     * Valida que el campo de documento no esté vacío y que el retraso sea mayor a
     * 0.
     * El retraso se multiplica por 3600 para convertirlo a la unidad esperada.
     * Muestra diálogos de error si alguna validación falla.
     * </p>
     */
    private void agregarProgramado() {
        String doc = docField.getText().trim();
        if (doc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre de documento.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int delay = (Integer) delaySpinner.getValue() * 3600;
        if (delay <= 0) {
            JOptionPane.showMessageDialog(this, "El retraso debe ser mayor a 0 para programar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        TrabajoImpresion.Prioridad prio = (TrabajoImpresion.Prioridad) priorityBox.getSelectedItem();
        LocalDateTime time = LocalDateTime.now().plusSeconds(delay);

        gestor.programarTrabajo(doc, prio, time);
        docField.setText("");
        delaySpinner.setValue(0);
        updateLists();
    }

    /**
     * Imprime el siguiente trabajo de la cola activa (el de mayor prioridad).
     * <p>
     * Si la cola está vacía, muestra un mensaje informativo.
     * Si se imprime correctamente, muestra un diálogo de éxito con los detalles.
     * </p>
     */
    private void imprimirSiguiente() {
        TrabajoImpresion trabajo = gestor.imprimirSiguiente();
        if (trabajo == null) {
            JOptionPane.showMessageDialog(this, "La cola activa está vacía.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Se ha impreso:\nDocumento: " + trabajo.getNombreDocumento()
                    + "\nPrioridad: " + trabajo.getPrioridad(), "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
        updateLists();
    }

    /**
     * Actualiza las listas visuales de la GUI con el estado actual de las colas.
     * <p>
     * Limpia ambos {@link DefaultListModel} y los recarga con los datos actuales
     * obtenidos del {@link GestorImpresion}.
     * </p>
     */
    private void updateLists() {
        activeListModel.clear();
        List<TrabajoImpresion> activos = gestor.getTrabajosActivos();
        for (TrabajoImpresion t : activos) {
            activeListModel.addElement(t.toString());
        }

        scheduledListModel.clear();
        List<TrabajoImpresion> programados = gestor.getTrabajosProgramados();
        for (TrabajoImpresion t : programados) {
            scheduledListModel.addElement(t.toString());
        }
    }
}

package proyecto_estructuras;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Representa un trabajo de impresión dentro del sistema de gestión.
 * <p>
 * Cada trabajo tiene un identificador único auto-generado, un nombre de
 * documento,
 * un nivel de prioridad ({@link Prioridad}) y opcionalmente una fecha/hora
 * programada.
 * Implementa {@link Comparable} para permitir el ordenamiento automático por
 * prioridad
 * dentro de una {@link java.util.PriorityQueue}.
 * </p>
 *
 * <p>
 * <b>Inmutabilidad:</b> Los objetos de esta clase son efectivamente inmutables
 * después
 * de su construcción (no exponen setters), lo que garantiza consistencia en las
 * colas.
 * </p>
 *
 * @see GestorImpresion
 * @see Prioridad
 */
public class TrabajoImpresion implements Comparable<TrabajoImpresion> {

    /** Contador estático para generar IDs únicos auto-incrementales. */
    private static int contadorId = 1;

    /** Identificador único del trabajo de impresión. */
    private int id;

    /** Nombre del documento a imprimir. */
    private String nombreDocumento;

    /** Nivel de prioridad del trabajo. */
    private Prioridad prioridad;

    /**
     * Enumeración que define los niveles de prioridad para los trabajos de
     * impresión.
     * <p>
     * El orden de prioridad de mayor a menor es:
     * <ol>
     * <li>{@link #GERENCIA} — Prioridad máxima (valor 3)</li>
     * <li>{@link #URGENTE} — Prioridad media (valor 2)</li>
     * <li>{@link #NORMAL} — Prioridad baja (valor 1)</li>
     * </ol>
     * </p>
     */
    public enum Prioridad {
        /** Prioridad máxima: documentos de la gerencia. */
        GERENCIA,
        /** Prioridad media: documentos urgentes. */
        URGENTE,
        /** Prioridad baja: documentos normales. */
        NORMAL
    }

    /**
     * Fecha y hora programada para la impresión; {@code null} si el trabajo es
     * inmediato.
     */
    private LocalDateTime fechaHoraProgramada;

    /**
     * Crea un nuevo trabajo de impresión con fecha/hora programada.
     *
     * @param nombreDocumento     nombre del documento a imprimir (no puede ser nulo
     *                            ni vacío)
     * @param prioridad           nivel de prioridad del trabajo (no puede ser nulo)
     * @param fechaHoraProgramada fecha/hora en que el trabajo se activará;
     *                            {@code null} para inmediato
     * @throws NullPointerException     si {@code nombreDocumento} o
     *                                  {@code prioridad} son nulos
     * @throws IllegalArgumentException si {@code nombreDocumento} está vacío o
     *                                  contiene solo espacios
     */
    public TrabajoImpresion(String nombreDocumento, Prioridad prioridad, LocalDateTime fechaHoraProgramada) {
        Objects.requireNonNull(nombreDocumento, "El nombre del documento no puede ser nulo");
        Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
        if (nombreDocumento.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del documento no puede estar vacío");
        }

        this.id = contadorId++;
        this.nombreDocumento = nombreDocumento.trim();
        this.prioridad = prioridad;
        this.fechaHoraProgramada = fechaHoraProgramada;
    }

    /**
     * Crea un nuevo trabajo de impresión inmediato (sin programación).
     *
     * @param nombreDocumento nombre del documento a imprimir (no puede ser nulo ni
     *                        vacío)
     * @param prioridad       nivel de prioridad del trabajo (no puede ser nulo)
     * @throws NullPointerException     si {@code nombreDocumento} o
     *                                  {@code prioridad} son nulos
     * @throws IllegalArgumentException si {@code nombreDocumento} está vacío o
     *                                  contiene solo espacios
     */
    public TrabajoImpresion(String nombreDocumento, Prioridad prioridad) {
        this(nombreDocumento, prioridad, null);
    }

    /**
     * Retorna el identificador único del trabajo.
     *
     * @return el ID del trabajo
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna el nombre del documento asociado al trabajo.
     *
     * @return el nombre del documento
     */
    public String getNombreDocumento() {
        return nombreDocumento;
    }

    /**
     * Retorna el nivel de prioridad del trabajo.
     *
     * @return la prioridad ({@link Prioridad})
     */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * Retorna la fecha/hora programada para la impresión.
     *
     * @return la fecha programada, o {@code null} si el trabajo es inmediato
     */
    public LocalDateTime getFechaHoraProgramada() {
        return fechaHoraProgramada;
    }

    /**
     * Indica si el trabajo fue programado para una hora futura.
     *
     * @return {@code true} si el trabajo tiene una fecha programada; {@code false}
     *         si es inmediato
     */
    public boolean esProgramado() {
        return fechaHoraProgramada != null;
    }

    /**
     * Compara este trabajo con otro por prioridad para el ordenamiento en la cola.
     * <p>
     * El orden es descendente: los trabajos con mayor prioridad (GERENCIA) se
     * consideran
     * "menores" para que la {@link java.util.PriorityQueue} los coloque al frente.
     * </p>
     *
     * @param otro el trabajo con el cual comparar
     * @return un valor negativo si este trabajo tiene mayor prioridad,
     *         cero si son iguales, positivo si tiene menor prioridad
     */
    @Override
    public int compareTo(TrabajoImpresion otro) {
        return Integer.compare(obtenerValorPrioridad(otro.prioridad),
                obtenerValorPrioridad(this.prioridad));
    }

    /**
     * Convierte un nivel de prioridad a su valor numérico para la comparación.
     *
     * @param prioridad el nivel de prioridad a convertir
     * @return el valor numérico: GERENCIA=3, URGENTE=2, NORMAL=1, null/default=0
     */
    private int obtenerValorPrioridad(Prioridad prioridad) {
        if (prioridad == null)
            return 0;
        switch (prioridad) {
            case GERENCIA:
                return 3;
            case URGENTE:
                return 2;
            case NORMAL:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Retorna una representación legible del trabajo de impresión.
     * <p>
     * Formato: {@code ID:N | Doc:'nombre' | Prioridad:NIVEL | fecha_o_Inmediato}
     * </p>
     *
     * @return cadena formateada con los datos del trabajo
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaStr = (fechaHoraProgramada != null) ? fechaHoraProgramada.format(formatter) : "Inmediato";
        return String.format("ID:%d | Doc:'%s' | Prioridad:%s | %s",
                id, nombreDocumento, prioridad, fechaStr);
    }
}
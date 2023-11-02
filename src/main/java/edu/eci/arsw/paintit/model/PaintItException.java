package edu.eci.arsw.paintit.model;

public class PaintItException extends Exception {

    public static final String EXISTING_PLAYER = "El jugador con este nombre ya existe.";
    public static final String LONG_NAME = "El nombre del jugador debe tener menos de 20 caracteres.";
    public static final String SHORT_NAME = "El nombre del jugador debe tener al menos 3 caracteres.";
    public static final String OCCUPIED_BOX = "En esa casilla ya hay un jugador.";
    public static final String OFF_BOARD = "La posicion a la cual se quiere mover esta fuera del tablero de juego.";
    public static final String FULL_GAME = "El juego ya está lleno, no se pueden unir más juagdores.";
    public static final String EMPTY_GAME = "El juego no tiene jugadores.";
    public static final String NO_GAMES = "No hay juegos disponibles.";
    public static final String INVALID_MOVE = "Solo se puede mover arriba, abajo, a la derecha o a la izquierda de la casilla actual.";
    public static final String NONE_EXISTENT_PLAYER = "El jugador dado no existe en el juego.";
    public static final String GAME_FINISHED = "El juego ha finalizado.";

    /**
     * Constructor para objetos de clase PaintItException.
     *
     * @param message Mensaje de la excepción.
     */
    public PaintItException(String message) {
        super(message);
    }

}

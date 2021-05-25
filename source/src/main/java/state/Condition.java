package state;

/**
 * Set of automata states for bot:
 *  BEGIN - initial bot state,
 *  HALL - state after user type '/start'
 *  ROOMS - state after user type '/rooms'
 *  CATEGORIES - state after user has selected room
 *  FILES - state after user has selected category
 */
public enum Condition {
    BEGIN,
    HALL,
    ROOMS,
    ROOMS_CHOOSE,
    ROOMS_CREATE,
    CATS,
    CATS_CHOOSE,
    CATS_CREATE,
    FILES,
    FILES_GET,
    FILES_SEND
}

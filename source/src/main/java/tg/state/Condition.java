package tg.state;

/**
 * Set of automata states for bot:
 *  BEGIN - initial bot tg.state,
 *  HALL - tg.state after user type '/start'
 *  ROOMS - tg.state after user type '/rooms'
 *  CATEGORIES - tg.state after user has selected room
 *  FILES - tg.state after user has selected category
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

package pepse.world.ui;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;


/**
 * This class represents the board that displays information about the game, such as the player's energy, kills,
 * and time remaining.
 */
public class BoardInfo extends GameObject {
    private static final String IMAGE_LOCATION = "assets/ui/board-info.png";
    private static final Vector2 BOARD_LABEL_POSITION = new Vector2(0, 0);
    private static final Vector2 BOARD_SIZE = new Vector2(1.65f * 124, 1.65f * 170);

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public BoardInfo(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     Creates a new BoardInfo object and adds it to the given collection of game objects.
     @param gameObjects the collection of game objects to add the BoardInfo to
     @param imageReader an image reader to use for reading images from disk
     @param layer the layer at which to add the BoardInfo
     @return the newly created BoardInfo object
     */
    public static BoardInfo create(GameObjectCollection gameObjects,
                                   ImageReader imageReader,
                                   int layer) {
        BoardInfo boardInfo = new BoardInfo(BOARD_LABEL_POSITION,
                BOARD_SIZE,
                imageReader.readImage(IMAGE_LOCATION, true));
        gameObjects.addGameObject(boardInfo, layer);
        boardInfo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return boardInfo;
    }
}

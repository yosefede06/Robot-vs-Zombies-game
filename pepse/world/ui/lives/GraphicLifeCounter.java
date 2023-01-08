package pepse.world.ui.lives;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.util.Counter;
/**
 * This is the class Graphic life counter. It checks every frame when a player lost a life, and updates
 * the number of hearts in the window. When it takes the bonus heart, it applies it and actualizes the
 * number of hearts also.
 */
public class GraphicLifeCounter extends GameObject {
    private static final String PATH_HEART = "assets/ui/heart.png";
    private static final int MARGIN_X = 50;
    private static final float MARGIN_Y = 30;
    private final GameObjectCollection gameObjectCollection;
    private final int heartLayer;
    public Counter livesCounter;
    public int numOfLives;
    public final GameObject[] hearts;
    private final int LIMIT_LIVES = 4;
    private final static int HEART_DIMENSIONS = 20;
    private final static int NUM_LIFE_BEGINNING = 3;


    /**
     * This is the constructor for the graphic lives counter.
     * It creates a 0x0 sized object (to be able to call its update method in game),
     * Creates numOfLives hearts, and adds them to the game.
     * @param widgetTopLeftCorner the top left corner of the left most heart
     * @param widgetDimensions  the dimension of each heart
     * @param livesCounter the counter which holds current lives count
     * @param widgetRenderable the image renderable of the hearts
     * @param gameObjectsCollection  the collection of all game objects currently in the game
     * @param numOfLives number of current lives
     */
    public GraphicLifeCounter(Vector2 widgetTopLeftCorner, Vector2 widgetDimensions,
                              Counter livesCounter, Renderable widgetRenderable,
                              GameObjectCollection gameObjectsCollection, int numOfLives, int layer) {
        super(Vector2.ZERO, Vector2.ZERO, null);
        this.livesCounter = livesCounter;
        this.gameObjectCollection = gameObjectsCollection;
        this.numOfLives = numOfLives;
        this.hearts = new GameObject[LIMIT_LIVES];
        heartLayer = layer;
        // give position to all hearts
        Vector2[] positionHearts = new Vector2[LIMIT_LIVES];
        for(int i = 0 ; i < LIMIT_LIVES; i++) {
            positionHearts[i] = widgetTopLeftCorner.add(new Vector2((HEART_DIMENSIONS * i + MARGIN_X + 2 * i),MARGIN_Y));
            this.hearts[i] = new GameObject(positionHearts[i], widgetDimensions, widgetRenderable);
            if(i != NUM_LIFE_BEGINNING) {
                hearts[i].setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                this.gameObjectCollection.addGameObject(this.hearts[i], heartLayer);
            }
        }
    }

    /**
     * The create method is a static factory method for creating and adding a new GraphicLifeCounter object
     * to a collection of GameObjects.
     * @param livesCounter a Counter object that tracks the player's lives
     * @param gameObjects a GameObjectCollection object where the new GraphicLifeCounter object will be added
     * @param imageReader an ImageReader object used to read the image file for the heart icons
     * @param numOfLives an int representing the number of lives that the player has
     * @param layer  an int representing the layer on which the object should be added to the gameObjects
     *               collection
     * @return GraphicLifeCounter
     */
    public static GraphicLifeCounter create(Counter livesCounter,
                                            GameObjectCollection gameObjects,
                                            ImageReader imageReader,
                                            int numOfLives,
                                            int layer) {
        Renderable heart = imageReader.readImage(PATH_HEART, true);
        Vector2 vectorHeartXY = new Vector2(0,20);
        Vector2 vectorHeartDimensions = new Vector2(HEART_DIMENSIONS, HEART_DIMENSIONS);
        GraphicLifeCounter graphicLifeCounter =  new GraphicLifeCounter(vectorHeartXY,
                vectorHeartDimensions,
                livesCounter,
                heart,
                gameObjects,
                numOfLives,
                layer);
        gameObjects.addGameObject(graphicLifeCounter, layer);
        return graphicLifeCounter;
    }

    /**
     * This method is overwritten from GameObject.
     * It removes hearts from the screen if there are more
     * hearts than there are lives left
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(this.numOfLives > this.livesCounter.value()) {
            this.gameObjectCollection.removeGameObject(this.hearts[livesCounter.value()], heartLayer);
            numOfLives = livesCounter.value();
        }
    }
}
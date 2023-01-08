package pepse.world;

import danogl.components.RendererComponent;
import danogl.components.Transition.TransitionType;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;

/**
 * The Skill class represents a skill that can be used in the game world.
 * It stores an array of image locations and a corresponding array of Renderable objects,
 * as well as an ImageReader and RendererComponent for rendering the skill images.
 * It also has a TransitionType field to determine how the skill should behave when it finishes playing.
 */
public class Skill {
    private final static int RENDER_IMAGES_FRAME = 5; // Number of game frames to show each skill
    // image before transitioning to the next one
    private int renderFrame = 0; // Counter for game frames
    private int currRender = 0; // Index of the current skill image being rendered
    private final String[] imagesLocation; // Array of image file locations for the skill
    private final Renderable[] renderableImages; // Array of Renderable objects for the skill images
    private final ImageReader imageReader; // ImageReader object for reading in the skill images
    private final RendererComponent renderer; // RendererComponent for rendering the skill images
    private final TransitionType transitionType; // TransitionType for determining how the skill should
    // behave when it finishes playing

    /**
     * Constructor for the Skill class.
     *
     * @param imagesLocation   An array of strings representing the file locations of the skill images.
     * @param transitionType   A TransitionType enum value representing how the skill should behave when
     *                         it finishes playing.
     * @param imageReader      An ImageReader object for reading in the skill images.
     * @param renderer         A RendererComponent for rendering the skill images.
     */
    public Skill(String[] imagesLocation,
                 TransitionType transitionType,
                 ImageReader imageReader,
                 RendererComponent renderer) {
        this.imagesLocation = imagesLocation;
        this.transitionType = transitionType;
        renderableImages = new Renderable[imagesLocation.length];
        this.imageReader = imageReader;
        this.renderer = renderer;
        initiliazeRenderable();
    }

    /**
     * Initializes the renderableImages array by using the ImageReader to read in the skill images.
     */
    private void initiliazeRenderable() {
        for(int i = 0; i < imagesLocation.length; i++) {
            renderableImages[i] = imageReader.readImage(imagesLocation[i], true);
        }
    }

    /**
     * Gets the next skill image to be rendered and updates the renderer with it.
     *
     * @param reInitRender  A boolean value indicating whether to reset the rendering back to the first image.
     */
    public void getNextRender(boolean reInitRender) {
        if (reInitRender) currRender = 0;
        if(++renderFrame == RENDER_IMAGES_FRAME) {
            renderFrame = 0;
            ++currRender;
        }
        if(currRender == imagesLocation.length) {
            switch (transitionType) {
                case TRANSITION_LOOP:
                    currRender = 0;
                    break;
                case TRANSITION_ONCE:
                    --currRender;
                    break;
            }
        }
        renderer.setRenderable(renderableImages[currRender]);
    }

    /**
     * Determines if the skill is currently active.
     * A skill is considered active if it is still transitioning through its images or if it is set
     * to loop indefinitely.
     *
     * @return  A boolean value indicating whether the skill is currently active.
     */
    public boolean isSkillActive() {
        return !(transitionType == TransitionType.TRANSITION_ONCE && currRender == imagesLocation.length - 1);
    }
}


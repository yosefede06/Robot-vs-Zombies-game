package pepse.world.trees;

import danogl.gui.WindowController;
import danogl.gui.mouse.MouseActionParams;
import danogl.gui.mouse.MouseButton;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;


/**
 * This is a class called StickyNoteLeaf that extends the Leaf class.
 * It represents a leaf on a tree that is capable of displaying a message when clicked with the left mouse
 * button, and deleting the message when clicked with the right mouse button.
 */
public class StickyNoteLeaf extends Leaf {
    private static final float PROBABILITY_OF_NOTE_APPEARANCE = 0.05f;
    private final Random random;
    private int indexSeed;
    private boolean containsMessage;
    private String currNote;
    private final WindowController windowController;
    private final ArrayList<String> seedArray;
    private final Runnable updateTable;
    private static final Float CONTAINS_MESSAGE_ANGLE = 45f;
    private static final Float NOT_CONTAINS_MESSAGE_ANGLE = 10f;
    private static final Float CONTAINS_MESSAGE_TIME_TRANSITION_ANGLE = 0.5f;
    private static final Float NOT_CONTAINS_MESSAGE_TIME_TRANSITION_ANGLE = 2f;

    /**
     * Constructs a new StickyNoteLeaf instance with the given parameters.
     * It sets the position of the leaf using the superclass's constructor and initializes the random,
     * windowController, seedArray, and updateTable instance variables with the corresponding parameters.
     * It also sets the containsMessage instance variable to false and the currNote instance variable to
     * an empty string. If the seedArray is not empty and the probability of note appearance is satisfied,
     * the containsMessage variable is set to true and the currNote variable is set to a random element of
     * the seedArray. It also updates the angle of the leaf to the "contains message" angle.
     * @param position The position of the leaf, in window coordinates (pixels).
     * @param random A random number generator.
     * @param renderable The renderable representing the leaf.
     * @param windowController The window controller for the game window
     * @param seedArray  An array of strings containing the possible messages that can appear on the leaves.
     * @param updateTable  A runnable that updates the table of messages in the game window.
     */
    StickyNoteLeaf(Vector2 position,
                   Random random,
                   Renderable renderable,
                   WindowController windowController,
                   ArrayList<String> seedArray,
                   Runnable updateTable) {
        super(position, random, renderable);
        this.random = random;
        this.windowController = windowController;
        this.seedArray = seedArray;
        this.updateTable = updateTable;
        containsMessage = false;
        currNote = "";
        if(seedArray.size() != 0 && checkIfContainsNote()) {
            containsMessage = true;
            indexSeed = Math.round(random.nextFloat() * seedArray.size()) % seedArray.size();
            currNote = seedArray.get(indexSeed);
            updateLeafAngle(CONTAINS_MESSAGE_ANGLE, CONTAINS_MESSAGE_TIME_TRANSITION_ANGLE);
        }
    }

    /**
     * checkIfContainsNote is a method that returns a boolean indicating whether a sticky note
     * should be placed on this leaf or not.
     * @return If the random number is less than PROBABILITY_OF_NOTE_APPEARANCE,
     * then the method returns true,
     * indicating that a note should be placed on the leaf. Otherwise, it returns false.
     */
    private boolean checkIfContainsNote() {
        return random.nextDouble() >= 1 - PROBABILITY_OF_NOTE_APPEARANCE;
    }

    /**
     * Update the StickyNoteLeaf
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
    }

    /**
     * The onMouseAction method is a callback method that is called when a mouse event occurs on the object.
     * In this case, the method checks if the left mouse button was pressed and, if so,
     * either shows an input dialog to the user to write a new secret message,
     * or displays the current message in a message box. If the right mouse button was pressed,
     * the method removes the current message from the seedArray if it exists, updates the table,
     * drops the leaf and updates the angle of the leaf.
     * @param params info regarding the mouse's parameters in this frame and the action that occurred.
     */
    @Override
    public void onMouseAction(MouseActionParams params) {
        super.onMouseAction(params);
        if(params.getButton() == MouseButton.LEFT_BUTTON) {
            if(!containsMessage) {
                String newMessage = JOptionPane.showInputDialog("Write your secret message");
                if(newMessage != null) {
                    writeMessage(newMessage);
                }
            }
            else windowController.showMessageBox(currNote);

        }
        if(params.getButton() == MouseButton.RIGHT_BUTTON) {
            if(containsMessage) {
                seedArray.remove(indexSeed);
                updateTable.run();
            }
            dropLeaf();
            updateLeafAngle(NOT_CONTAINS_MESSAGE_ANGLE, NOT_CONTAINS_MESSAGE_TIME_TRANSITION_ANGLE);
            containsMessage = false;
            currNote = "";
        }
    }

    /**
     * The writeMessage method is used to write a message on the sticky note leaf.
     * @param note the message to be written on the leaf.
     */
    private void writeMessage(String note) {
        seedArray.add(note);
        updateTable.run();
        containsMessage = true;
        currNote = note;
        updateLeafAngle(CONTAINS_MESSAGE_ANGLE, CONTAINS_MESSAGE_TIME_TRANSITION_ANGLE);
    }
}



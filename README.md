yosefede06
r301299

*******************************
*        Screenshots          *
*******************************

<img align="center" src="https://github.com/yosefede06/Robot-vs-Zombies-game/blob/main/screen1.png" />
<img align="center" alt="HTML"  src="https://github.com/yosefede06/Robot-vs-Zombies-game/blob/main/screen2.png" />
<img align="center" alt="HTML"  src="https://github.com/yosefede06/Robot-vs-Zombies-game/blob/main/screen3.png" />
<img align="center" alt="HTML" src="https://github.com/yosefede06/Robot-vs-Zombies-game/blob/main/screen4.png" />

*******************************
*       Differences UML       *
*******************************

The main differences of our UML are the following:

   - Before:

        - Terrain: When we wrote the Terrain class, we initially didn't realize that we would be using the createInRange
        function again in other classes. As a result, we implemented it specifically for the Terrain class without
        considering the possibility of reuse. In order to determine the starting point for the ground, we created
        a list of y-coordinates and found the minimum y-value among all the blocks. However, we later realized that
        our world would actually be infinite, and that we would be adding new blocks one by one as the avatar moves
        rather than rendering the full window each time. This meant that our original implementation was not
        suitable, as it was using unnecessary memory. To set the bottom ground, we simply combined the output
        values from the perlin noise function and normalized them with the dimensions of the window.

        - Avatar: For the Avatar class, we started by adding multiple skills to the character. As we developed the game
        further, we noticed that we were using the same pieces of code for implementing certain skills. At the time,
        we didn't anticipate that we would be expanding the game as much as we did, so we hadn't considered the need
        for a dedicated Skill class. As a result, we had to go back and refactor our code to better organize the
        different skills and their implementations.

   - After:

        - Terrain, Tree and Zombies: We implemented the createInRange method by creating a class in the util module
        called SegmentCompletion. This class was responsible for computing the algorithm to fill a segment with the
        minimum number of blocks. We then used this class to create specific classes for terrain, tree, and
        zombies. These classes were able to use the logic defined in SegmentCompletion to generate the appropriate
        elements in the game or application.

        - Avatar, Bullet and Zombie: As we were implementing the logic for rendering images per frame to simulate the
        movement of a character, we realized that we needed to keep track of the current skill state of the character.
        We thought it would be most efficient to define this in a separate class, so that we could just send the
        necessary parameters to implement the logic of each skill we wanted to use. Specifically, we created classes
        for the avatar, bullet, and zombie to handle this logic. This allowed us to easily control the movement and
        behavior of these elements in the game or application.

*******************************
*       Infinite World        *
*******************************

        - We initially tried rendering the full window of our infinite world every time the avatar approached the end.
        However, we quickly realized that this approach was not effective because the rendering was not smooth.
        As a result, we decided to render blocks of the world one by one as the user moves to the right or left.
        We also maintained a frame of 5 blocks outside the window at all times to ensure that the user is unable
        to see the end of the world while we are updating the rendering.

*******************************
*     Tree Implementation     *
*******************************

        - We've used the SegmentCompletion to fill blocks for our tree implementation. For the top of the tree,
        We created a class called Leaf and the tree will create all of those leaves.
        In our case, with the bonus StickyNote, the tree will create StickyNotes, which is a class that extends Leaves
        and contains additional features.


*******************************
*      Our Decisions        *
*******************************

    - Our main dilemmas and decisions for the implementation were the followings:

        1 - Collisions with Terrain: To efficiently handle collisions with terrain, we have implemented two layers:
        TOP_TERRAIN_BLOCKS_LAYER and TERRAIN_LAYER. On PepseGameManager, we allow only the TOP_TERRAIN_BLOCKS_LAYER
        to collide with objects that have gravity, rather than looping through all terrain blocks.
        When creating blocks for the terrain, we add the top two blocks to the TOP_TERRAIN_BLOCKS_LAYER and the rest
        to the TERRAIN_LAYER. This helps us to optimize the collision detection process and ensure smooth gameplay.

        2 - Render of objects of the game: We have implemented a class that takes care of it:
        The Skill class is responsible for managing the animation of a skill or ability in the game,
        including loading the images, tracking the current frame and image, and updating and rendering
        the animation as needed.

        3 - In the leaf class there are several random parameters that we need to take care, such as the time were it
        starts resizing and moving angle, or the life time and dead time. In order to make the same world for the same
        seed, we have sent for classes like tree to leaf the random function already initialized with the seed.

        4 - The Zombies class represents a group of zombies in a game. It has a createInRange method that builds the
        zombies in a given range on the x-axis. The createInRange method iterates over the columns in the given range
        and checks if a zombie should be added at that position using the checkIfZombie method, which returns true with
        a probability of PROBABILITY_OF_ZOMBIE_APPEARANCE. If a zombie should be added, it calls the createZombie
        method to create a new Zombie object and add it to the game. The Zombies class also has fields for the
        imageReader, livesCounter, killsCounter, and gameObjects collections, as well as the zombiesLayer and seed
        values used to create the zombies.


*******************************
*           Bonus             *
*******************************

    For the bonus we added the following:

    1 - Zombies that walk around the field that if they touch the main character they take away a life.
    2 - Each sheet is a "sticky note" that when clicked can write text that when pressing enter becomes the text
        of the sheet. A subsequent click on the sheet shows the text. To indicate that the sheet contains text,
        it is turned 45 degrees. By pressing the right button on the sheet,
        it falls and is replaced by a new sheet without text.
    3 - We've creates a world based on a certain seed (string), it is not only the same as the one created by
        another person, but the sticky notes are also shared. This means that the contents of all the notes
        for a particular seed are synchronized in an HTML file. Running the file you will see a table of seeds
        and next to them a category. For example, for the seed "movies", the notes in the same shared world contain
        names of favorite movies from different users. When a user enters this seed at the start of execution,
        they will see the world where the notes are. By clicking the left button on a sheet, you can add your own movie,
        And by clicking the right button, you can make one of the populated sheets fall and restart.
    4 - The player becomes a glider when flying and both can shoot bullets that kill zombies.
    5 - We hold Life, time, energy and dead zombie counters that will determine the player's victory or defeat
    6 - We have decided to implement a robot and zombies because they are our favorite characters.
    7 - The game mode is to kill 10 zombies before time runs out, it is lost when time runs out or the 3
        lives given at the start of the game are lost.




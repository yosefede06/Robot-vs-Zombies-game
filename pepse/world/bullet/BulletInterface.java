package pepse.world.bullet;

/**
 * BulletInterface is an interface that represents the behavior of a bullet.
 * It defines a single method, update, which is called each frame to update the state of the bullet.
 */
public interface BulletInterface {
    void update(float deltaTime);
}

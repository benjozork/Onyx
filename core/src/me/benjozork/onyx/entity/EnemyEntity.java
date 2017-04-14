package me.benjozork.onyx.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import me.benjozork.onyx.entity.ai.AI;
import me.benjozork.onyx.entity.ai.AIConfiguration;
import me.benjozork.onyx.entity.ai.AIShootingConfiguration;
import me.benjozork.onyx.internal.GameManager;
import me.benjozork.onyx.game.GameScreenManager;
import me.benjozork.onyx.internal.PolygonLoader;
import me.benjozork.onyx.utils.Utils;

/**
 * @author Benjozork
 */
public class EnemyEntity extends LivingEntity {

    // Enemy textures

    private final Texture ENEMY_TEXTURE = new Texture("entity/enemy/texture_0.png");
    private final Texture FIRING_ENEMY_TEXTURE = new Texture("entity/enemy/texture_0.png");
    private final Texture MOVING_FIRING_ENEMY_TEXTURE = new Texture("entity/enemy/texture_0.png");
    private final Texture MOVING_ENEMY_TEXTURE = new Texture("entity/enemy/texture_0.png");

    private Sprite currentTexture = new Sprite(ENEMY_TEXTURE);

    private final float ANGLE_DELTA = 100, TARGET_ANGLE = 25, ANGLE_DELTA_TOLERANCE = 0.1f;

    private DrawState state = DrawState.IDLE;
    private Direction direction = Direction.STRAIGHT;

    private float spriteRotation;

    private AI ai;

    private boolean debug = true;

    public EnemyEntity(float x, float y) {
        super(x, y);
    }

    @Override
    public void init() {

        // Initialize hitbox

        bounds = PolygonLoader.getPolygon("Enemy", ENEMY_TEXTURE.getWidth(), ENEMY_TEXTURE.getHeight());

        // Flip texture upside down

        currentTexture.flip(false, true);

        // Setup AI

        AIConfiguration aiConfiguration = new AIConfiguration();
        aiConfiguration.strategy = AIConfiguration.AIStrategy.LINEAR;
        aiConfiguration.reluctance = AIConfiguration.ProjectileReluctance.GOD;
        aiConfiguration.source = this;
        aiConfiguration.target = GameScreenManager.getPlayer();
        AIShootingConfiguration shootingConfiguration = new AIShootingConfiguration();
        shootingConfiguration.minShootStreakDelay = 1.5f;
        shootingConfiguration.maxShootStreakDelay = 3f;
        shootingConfiguration.minShootStreakTime = 2f;
        shootingConfiguration.maxShootStreakTime = 4f;
        shootingConfiguration.shootResetTime = 5f;
        shootingConfiguration.shootInterval = 0.1f;
        shootingConfiguration.minShootImprecision = -25f;
        shootingConfiguration.maxShootImprecision = 25f;
        aiConfiguration.shootingConfig = shootingConfiguration;

        ai = new AI(aiConfiguration);
        type = Type.ENEMY;

    }

    @Override
    public void update() {

        ai.update(Utils.delta());

        if (velocity.x < -2)
            direction = Direction.RIGHT;
        else if (velocity.x > 2)
            direction = Direction.LEFT;
        else
            direction = Direction.STRAIGHT;

        if (direction == EnemyEntity.Direction.STRAIGHT) {
            if (spriteRotation < ANGLE_DELTA_TOLERANCE && spriteRotation > - ANGLE_DELTA_TOLERANCE) spriteRotation = 0f;
            if (spriteRotation < 0 * MathUtils.degreesToRadians)
                spriteRotation += (ANGLE_DELTA * MathUtils.degreesToRadians) * Utils.delta();
            else if (spriteRotation > 0 * MathUtils.degreesToRadians)
                spriteRotation -= (ANGLE_DELTA * MathUtils.degreesToRadians) * Utils.delta();
        } else if (direction == EnemyEntity.Direction.RIGHT) {
            if (spriteRotation < TARGET_ANGLE * MathUtils.degreesToRadians)
                spriteRotation += (ANGLE_DELTA * MathUtils.degreesToRadians) * Utils.delta();
        } else if (direction == EnemyEntity.Direction.LEFT) {
            if (spriteRotation > - TARGET_ANGLE * MathUtils.degreesToRadians)
                spriteRotation -= (ANGLE_DELTA * MathUtils.degreesToRadians) * Utils.delta();
        }


        if (state == EnemyEntity.DrawState.IDLE) {
            currentTexture.setTexture(ENEMY_TEXTURE);
        } else if (state == EnemyEntity.DrawState.MOVING) {
            currentTexture.setTexture(MOVING_ENEMY_TEXTURE);
        } else if (state == EnemyEntity.DrawState.FIRING) {
            currentTexture.setTexture(FIRING_ENEMY_TEXTURE);
        } else if (state == EnemyEntity.DrawState.FIRING_MOVING) {
            currentTexture.setTexture(MOVING_FIRING_ENEMY_TEXTURE);
        }

        currentTexture.setPosition(getX(), getY());
        currentTexture.setRotation(- spriteRotation * MathUtils.radiansToDegrees);

        bounds.setRotation(- spriteRotation * MathUtils.radiansToDegrees);

    }

    @Override
    public void draw() {
        SpriteBatch batch = GameManager.getBatch();
        currentTexture.draw(batch);
    }

    @Override
    public void dispose() {
        GameScreenManager.removeEntity(this);
    }

    /**
     * The current DrawState
     * @return the state
     */
    public DrawState getState() {
        return state;
    }

    /**
     * Sets the current DrawState
     * @param v the state to be used
     */
    public void setState(DrawState v) {
        this.state = v;
    }

    /**
     * The direction of the player
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Changes the direction of the player
     * @param v the direction to be used
     */
    public void setDirection(Direction v) {
        this.direction = v;
    }

    public boolean isFiring() {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }

    public enum DrawState {
        IDLE,
        FIRING,
        MOVING,
        FIRING_MOVING,
    }

    public enum Direction {
        STRAIGHT,
        RIGHT,
        LEFT
    }

}

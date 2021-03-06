package me.benjozork.onyx.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import me.benjozork.onyx.GameManager;
import me.benjozork.onyx.game.GameScreenManager;
import me.benjozork.onyx.utils.PolygonHelper;

/**
 * @author Benjozork
 */
public class ProjectileEntity extends Entity {

    private Vector2 origin;
    private Vector2 target;

    private SpriteBatch batch;

    private float damage = 0;

    private float speed = 1000;

    private Texture texture;

    public LivingEntity source;

    public ProjectileEntity(float x, float y, float targetx, float targety, String texturePath) {
        super(x, y);
        this.origin = new Vector2(x, y);
        this.target = new Vector2(targetx, targety);
        this.texture = new Texture(texturePath);
    }

    @Override
    public void init() {
        batch = GameManager.getBatch();

        // Initialize hitbox

        bounds = PolygonHelper.getPolygon(getX(), getY(), 10, 10);

        // Set velocity accordingly to target

        velocity.set(target.cpy().sub(getX(), getY()));

        velocity.nor().scl(speed);
    }

    @Override
    public void update() {
        if (getX() < - Gdx.graphics.getWidth()  || getX() > Gdx.graphics.getWidth() * 2 || getY() < - Gdx.graphics.getHeight() || getY() > Gdx.graphics.getHeight() * 2) {
            dispose();
        }

        if (GameScreenManager.getEnemies().size == 0) return;

        for (EnemyEntity enemy : GameScreenManager.getEnemies()) {
            if (this.collidesWith(enemy.getBounds()) && enemy.type != source.type) {
                enemy.damage(10f, source);
                this.dispose();
            }
        }

        PlayerEntity player = GameScreenManager.getLocalPlayerEntity();
        if (this.collidesWith(player.getBounds()) && player.type != source.type) {
            player.damage(10f, source);
            this.dispose();
        }
    }

    @Override
    public void draw() {
        GameManager.setIsShapeRendering(true);
        GameManager.getRenderer().setProjectionMatrix(GameManager.getWorldCamera().combined);
        if (source instanceof PlayerEntity) GameManager.getRenderer().setColor(Color.ORANGE);
        else if (source instanceof EnemyEntity) GameManager.getRenderer().setColor(Color.BLUE);
        GameManager.getRenderer().line(source.getPosition(), target);
        GameManager.setIsShapeRendering(false);
        batch.draw(texture, bounds.getX(), bounds.getY(), 10, 10);
    }

    @Override
    public void dispose() {
        GameScreenManager.removeEntity(this);
        texture.dispose();
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

}
package me.benjozork.onyx.game.entity.ai;

import me.benjozork.onyx.game.entity.LivingEntity;

/**
 * Describes an AI behavior
 * @author Benjozork
 */
public class AIConfiguration {

    /**
     * The strategy used by the AI
     */
    public AIStrategy strategy;

    /**
     * The degree of bullet avoidance
     */
    public ProjectileReluctance reluctance;

    /**
     * The source {@link LivingEntity} of the AI
     */
    public LivingEntity source;

    /**
     * The target {@link LivingEntity} of the AI
     */
    public LivingEntity target;

    /**
     * The {@link AIShootingConfiguration} to use for shooting mechanics
     */
    public AIShootingConfiguration shootingConfig;

    public float factor = 100f;

    /**
     * The different strategies by which an entity follows another entity
     */
    public enum AIStrategy {
        ACCELERATED,
        LINEAR
    }

    public enum ProjectileReluctance {
        NONE,
        LOW,
        MED,
        HIGH,
        GOD
    }

}

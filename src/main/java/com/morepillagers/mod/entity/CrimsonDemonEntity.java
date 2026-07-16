package com.morepillagers.mod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CrimsonDemonEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.crimson_demon.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.crimson_demon.walk");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("animation.crimson_demon.fly");
    private static final RawAnimation FIREBALL = RawAnimation.begin().thenPlay("animation.crimson_demon.fireball");

    private int attackCooldown = 0;

    public CrimsonDemonEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0) // Boss tier health
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_ARMOR, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new CrimsonDemonAttackGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (this.isDemonAttacking()) {
                return event.setAndContinue(FIREBALL);
            }
            if (this.getHealth() < this.getMaxHealth() * 0.5) {
                // Fly in second phase (under 50% health)
                return event.setAndContinue(FLY);
            }
            if (event.isMoving()) {
                return event.setAndContinue(WALK);
            }
            return event.setAndContinue(IDLE);
        }));
    }

    public boolean isDemonAttacking() {
        return this.attackCooldown > 30;
    }

    public void triggerAttack() {
        this.attackCooldown = 50;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class CrimsonDemonAttackGoal extends Goal {
        private final CrimsonDemonEntity demon;
        private int runTicks = 0;

        public CrimsonDemonAttackGoal(CrimsonDemonEntity demon) {
            this.demon = demon;
        }

        @Override
        public boolean canStart() {
            return this.demon.getTarget() != null && this.demon.attackCooldown <= 0;
        }

        @Override
        public void start() {
            this.runTicks = 0;
        }

        @Override
        public void tick() {
            var target = this.demon.getTarget();
            if (target == null) return;

            this.demon.getLookControl().lookAt(target, 30.0f, 30.0f);
            double distSq = this.demon.squaredDistanceTo(target);

            if (distSq < 400.0) {
                if (this.runTicks == 5) {
                    this.demon.triggerAttack();
                } else if (this.runTicks == 12) {
                    // Launch dual hellfire balls
                    Vec3d lookDir = this.demon.getRotationVec(1.0f);
                    SmallFireballEntity fireball = new SmallFireballEntity(
                            this.demon.getWorld(),
                            this.demon,
                            lookDir.x,
                            lookDir.y,
                            lookDir.z
                    );
                    fireball.setPosition(this.demon.getX() + lookDir.x * 1.2, this.demon.getEyeY(), this.demon.getZ() + lookDir.z * 1.2);
                    this.demon.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 1.5f, 0.6f);
                    this.demon.getWorld().spawnEntity(fireball);
                }
                this.runTicks++;
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.runTicks < 20 && this.demon.getTarget() != null;
        }
    }
}

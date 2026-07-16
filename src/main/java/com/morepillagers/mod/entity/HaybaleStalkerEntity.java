package com.morepillagers.mod.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HaybaleStalkerEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.haybale_stalker.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.haybale_stalker.walk");
    private static final RawAnimation BURST = RawAnimation.begin().thenPlay("animation.haybale_stalker.burst");

    private boolean isDisguised = true;
    private int burstCooldown = 0;

    public HaybaleStalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35) // Rapid when revealed
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new RevealAndAttackGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.burstCooldown > 0) {
            this.burstCooldown--;
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isDisguised) {
            this.revealStalker();
        }
        return super.damage(source, amount);
    }

    public void revealStalker() {
        if (this.isDisguised) {
            this.isDisguised = false;
            this.burstCooldown = 25;
            this.playSound(SoundEvents.BLOCK_GRASS_BREAK, 1.5f, 0.8f);
            this.playSound(SoundEvents.ENTITY_PILLAGER_CELEBRATE, 1.0f, 1.2f);
            // Spawn some golden wheat / hay particles in fully realized mod
        }
    }

    public boolean isBursting() {
        return this.burstCooldown > 0;
    }

    public boolean isDisguised() {
        return this.isDisguised;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (this.isBursting()) {
                return event.setAndContinue(BURST);
            }
            if (this.isDisguised()) {
                return event.setAndContinue(IDLE); // Stay still like a haybale block
            }
            if (event.isMoving()) {
                return event.setAndContinue(WALK);
            }
            return event.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class RevealAndAttackGoal extends Goal {
        private final HaybaleStalkerEntity stalker;

        public RevealAndAttackGoal(HaybaleStalkerEntity stalker) {
            this.stalker = stalker;
        }

        @Override
        public boolean canStart() {
            return this.stalker.getTarget() != null && this.stalker.isDisguised();
        }

        @Override
        public void start() {
            // Check if player is close enough to reveal
            double distSq = this.stalker.squaredDistanceTo(this.stalker.getTarget());
            if (distSq < 49.0) { // 7 blocks away
                this.stalker.revealStalker();
            }
        }
    }
}

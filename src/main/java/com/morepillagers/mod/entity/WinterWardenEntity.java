package com.morepillagers.mod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

public class WinterWardenEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.winter_warden.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.winter_warden.walk");
    private static final RawAnimation CAST = RawAnimation.begin().thenPlay("animation.winter_warden.cast");

    private int castCooldown = 0;

    public WinterWardenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0) // Boss tier health
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WinterWardenCastGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.1, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.castCooldown > 0) {
            this.castCooldown--;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (this.isCasting()) {
                return event.setAndContinue(CAST);
            }
            if (event.isMoving()) {
                return event.setAndContinue(WALK);
            }
            return event.setAndContinue(IDLE);
        }));
    }

    public boolean isCasting() {
        return this.castCooldown > 40; // High animation priority during initial casting phase
    }

    public void triggerCast() {
        this.castCooldown = 70;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class WinterWardenCastGoal extends Goal {
        private final WinterWardenEntity warden;
        private int runTicks = 0;

        public WinterWardenCastGoal(WinterWardenEntity warden) {
            this.warden = warden;
        }

        @Override
        public boolean canStart() {
            return this.warden.getTarget() != null && this.warden.castCooldown <= 0;
        }

        @Override
        public void start() {
            this.runTicks = 0;
        }

        @Override
        public void tick() {
            var target = this.warden.getTarget();
            if (target == null) return;

            this.warden.getLookControl().lookAt(target, 30.0f, 30.0f);
            double distSq = this.warden.squaredDistanceTo(target);

            if (distSq < 225.0) {
                if (this.runTicks == 5) {
                    this.warden.triggerCast();
                } else if (this.runTicks == 20) {
                    // Flash-freeze/Slowness zone around target player
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 1));
                    this.warden.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.5f, 0.5f);
                    this.warden.playSound(SoundEvents.ENTITY_PLAYER_HURT_FREEZE, 1.0f, 1.0f);
                }
                this.runTicks++;
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.runTicks < 25 && this.warden.getTarget() != null;
        }
    }
}

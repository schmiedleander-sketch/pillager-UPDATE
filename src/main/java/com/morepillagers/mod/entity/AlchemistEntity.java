package com.morepillagers.mod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AlchemistEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.alchemist.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.alchemist.walk");
    private static final RawAnimation THROW = RawAnimation.begin().thenPlay("animation.alchemist.throw");

    private int throwCooldown = 0;

    public AlchemistEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new AlchemistThrowPotionGoal(this));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.throwCooldown > 0) {
            this.throwCooldown--;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (this.isThrowing()) {
                return event.setAndContinue(THROW);
            }
            if (event.isMoving()) {
                return event.setAndContinue(WALK);
            }
            return event.setAndContinue(IDLE);
        }));
    }

    public boolean isThrowing() {
        return this.throwCooldown > 30; // Animating throw during first stage of cooldown
    }

    public void triggerThrowEffect() {
        this.throwCooldown = 50; // Total frames/ticks of throw and recover
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static class AlchemistThrowPotionGoal extends Goal {
        private final AlchemistEntity alchemist;
        private int runTicks = 0;

        public AlchemistThrowPotionGoal(AlchemistEntity alchemist) {
            this.alchemist = alchemist;
        }

        @Override
        public boolean canStart() {
            return this.alchemist.getTarget() != null && this.alchemist.throwCooldown <= 0;
        }

        @Override
        public void start() {
            this.runTicks = 0;
        }

        @Override
        public void tick() {
            var target = this.alchemist.getTarget();
            if (target == null) return;

            this.alchemist.getLookControl().lookAt(target, 30.0f, 30.0f);
            double distSq = this.alchemist.squaredDistanceTo(target);

            if (distSq < 256.0) {
                if (this.runTicks == 5) {
                    this.alchemist.triggerThrowEffect();
                } else if (this.runTicks == 10) {
                    // Spawn custom splashing harm/slowness potion towards target
                    Vec3d targetPos = target.getPos();
                    PotionEntity potion = new PotionEntity(this.alchemist.getWorld(), this.alchemist);
                    ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
                    PotionUtil.setPotion(potionStack, this.alchemist.getRandom().nextBoolean() ? Potions.HARMING : Potions.SLOWNESS);
                    potion.setItem(potionStack);

                    double dx = targetPos.x - this.alchemist.getX();
                    double dy = targetPos.y + target.getStandingEyeHeight() - 1.1 - potion.getY();
                    double dz = targetPos.z - this.alchemist.getZ();
                    double hDist = Math.sqrt(dx * dx + dz * dz);

                    potion.setVelocity(dx, dy + hDist * 0.2, dz, 0.75f, 8.0f);
                    this.alchemist.playSound(SoundEvents.ENTITY_SPLASH_POTION_THROW, 1.0f, 0.8f + this.alchemist.getRandom().nextFloat() * 0.4f);
                    this.alchemist.getWorld().spawnEntity(potion);
                }
                this.runTicks++;
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.runTicks < 15 && this.alchemist.getTarget() != null;
        }
    }
}

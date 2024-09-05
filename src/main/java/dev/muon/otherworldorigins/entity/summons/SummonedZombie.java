package dev.muon.otherworldorigins.entity.summons;

import dev.muon.otherworldorigins.entity.IFollowingSummon;
import dev.muon.otherworldorigins.entity.ISummon;
import dev.muon.otherworldorigins.entity.ModEntities;
import dev.muon.otherworldorigins.entity.goal.FollowSummonerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class SummonedZombie extends Zombie implements IFollowingSummon, ISummon {
    /*
     * Originally based off of Ars Nouveau, which is under the LGPL-v3.0 license
     */
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID;

    static {
        OWNER_UUID = IFollowingSummon.getOwnerUUIDAccessor(SummonedZombie.class);
    }
    public SummonedZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }


    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean isLimitedLifespan;
    private int limitedLifeTicks;

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
    }
    @Override
    public EntityType<?> getType() {
        return ModEntities.SUMMON_ZOMBIE.get();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        return null;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
    }

    @Override
    protected boolean shouldDropLoot() {return true;}

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new CopyOwnerTargetGoal<>(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, SummonedZombie.class){
            @Override
            protected boolean canAttack(@Nullable LivingEntity pPotentialTarget, TargetingConditions pTargetPredicate) {
                return pPotentialTarget != null && super.canAttack(pPotentialTarget, pTargetPredicate) && !pPotentialTarget.getUUID().equals(getOwnerUUID()) ;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, true,
                (LivingEntity entity) ->
                        (entity instanceof Mob mob && mob.getTarget() != null && mob.getTarget().equals(this.owner))
                                || (entity != null && entity.getKillCredit() != null && entity.getKillCredit().equals(this.owner))
        ));

        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.1f, true));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new FollowSummonerGoal(this, this.owner, 1.0, 9.0f, 3.0f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public void setWeapon(ItemStack item) {
        this.setItemSlot(EquipmentSlot.MAINHAND, item);
        this.reassessWeaponGoal();
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isLimitedLifespan && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(getWorld().damageSources().starve(), 20.0F);
        }
    }

    public Team getTeam() {
        if (this.getSummoner() != null) return getSummoner().getTeam();
        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        LivingEntity summoner = this.getSummoner();

        if (summoner != null) {
            if (pEntity instanceof ISummon summon && summon.getOwnerUUID() != null && summon.getOwnerUUID().equals(this.getOwnerUUID())) return true;
            return pEntity == summoner || summoner.isAlliedTo(pEntity);
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return true;
    }

    @Override
    public Level getWorld() {
        return this.level();
    }

    @Override
    public PathNavigation getPathNav() {
        return this.navigation;
    }

    @Override
    public Mob getSelfEntity() {
        return this;
    }

    public LivingEntity getSummoner() {
        return this.getOwnerFromID();
    }

    public LivingEntity getActualOwner() {
        return owner;
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.boundOrigin != null) {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }
        compound.putBoolean("isLimited", this.isLimitedLifespan);
        if (this.isLimitedLifespan) {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
        UUID ownerUuid = this.getOwnerUUID();
        if (ownerUuid != null) {
            compound.putUUID("OwnerUUID", ownerUuid);
        }
    }
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }
        if (compound.contains("isLimited")) {
            this.isLimitedLifespan = compound.getBoolean("isLimited");
        }
        if (compound.contains("LifeTicks")) {
            this.setLifeTicks(compound.getInt("LifeTicks"));
        }
        if (compound.hasUUID("OwnerUUID")) {
            this.setOwnerID(compound.getUUID("OwnerUUID"));
        }
    }

    @Override
    public void setLifeTicks(int lifeTicks) {
        this.limitedLifeTicks = lifeTicks;
    }
    @Override
    public int getTicksLeft() {
        return limitedLifeTicks;
    }
    @Override
    public void setIsLimitedLife(boolean bool) {
        this.isLimitedLifespan = bool;
    }

    public LivingEntity getOwnerFromID() {
        try {
            UUID uuid = this.getOwnerUUID();

            return uuid == null ? null : this.getWorld().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(OWNER_UUID, Optional.empty());
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public void reassessWeaponGoal() {
    }
}
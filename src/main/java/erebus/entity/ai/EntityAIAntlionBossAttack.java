package erebus.entity.ai;

import java.util.List;

import erebus.ModSounds;
import erebus.core.handler.configs.ConfigHandler;
import erebus.entity.EntityAntlionBoss;
import erebus.entity.EntityThrownSand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIAntlionBossAttack<T extends Entity> extends EntityAIBase {

	World worldObj;
	EntityCreature attacker;
	int attackTick;
	double speedTowardsTarget;
	boolean longMemory;
	Path entityPathEntity;
	Class<T> classTarget;
	private int attackTimer;
	private int failedPathFindingPenalty;
	private int shouldDo;
	private boolean jumpAttack;

	public EntityAIAntlionBossAttack(EntityCreature entity, Class<T> entityClass, double moveSpeed, boolean memory) {
		this(entity, moveSpeed, memory);
		classTarget = entityClass;
	}

	public EntityAIAntlionBossAttack(EntityCreature entity, double moveSpeed, boolean memory) {
		attacker = entity;
		worldObj = entity.getEntityWorld();
		speedTowardsTarget = moveSpeed;
		longMemory = memory;
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = attacker.getAttackTarget();
		if (entitylivingbase == null)
			return false;
		else if (!entitylivingbase.isEntityAlive())
			return false;
		else if (classTarget != null && !classTarget.isAssignableFrom(entitylivingbase.getClass()))
			return false;
		else if (--attackTimer <= 0) {
			entityPathEntity = attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
			attackTimer = 4 + attacker.getRNG().nextInt(7);
			return entityPathEntity != null;
		} else
			return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase entitylivingbase = attacker.getAttackTarget();
		return entitylivingbase == null ? false : !entitylivingbase.isEntityAlive() ? false : !longMemory ? !attacker.getNavigator().noPath() : attacker.isWithinHomeDistanceCurrentPosition();
	}

	@Override
	public void startExecuting() {
		attacker.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
		attackTimer = 0;
	}

	@Override
	public void resetTask() {
		attacker.getNavigator().clearPath();
	}

	@Override
	public void updateTask() {
		EntityLivingBase entitylivingbase = attacker.getAttackTarget();
		attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 20.0F, 20.0F);
		attacker.faceEntity(entitylivingbase, 50.0F, 50.0F);
		if ((longMemory || attacker.getEntitySenses().canSee(entitylivingbase)) && --attackTimer <= 0) {
			attackTimer = failedPathFindingPenalty + 4 + attacker.getRNG().nextInt(7);
			attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget);
			if (attacker.getNavigator().getPath() != null) {
				PathPoint finalPathPoint = attacker.getNavigator().getPath().getFinalPathPoint();
				if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1D)
					failedPathFindingPenalty = 0;
				else
					failedPathFindingPenalty += 10;
			} else
				failedPathFindingPenalty += 10;
		}
		attackTick = Math.max(attackTick - 1, 0);
		double distance = attacker.width * attacker.width + entitylivingbase.width;
		if (attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ) <= distance)
			if (attackTick <= 0) {
				attackTick = 20;
				worldObj.playSound(null, attacker.getPosition(), ModSounds.ANTLION_GROWL, SoundCategory.HOSTILE, 1.0F, 1.0F);
				attacker.attackEntityAsMob(entitylivingbase);
				entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(attacker), (float) (ConfigHandler.INSTANCE.mobAttackDamageMultiplier < 2 ? 8D : 8D * ConfigHandler.INSTANCE.mobAttackDamageMultiplier));
				entitylivingbase.addVelocity(-MathHelper.sin(attacker.rotationYaw * 3.141593F / 180.0F) * 0.3F, 0.1D, MathHelper.cos(attacker.rotationYaw * 3.141593F / 180.0F) * 0.3F);
			}

		if (attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ) > distance + 9D && attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ) < distance + 256.0D)
			if (attackTick <= 0) {
				++shouldDo;
				if (shouldDo <= 2)
					attackTick = 5;
				else {
					attackTick = 0;
					shouldDo = 0;
				}
				if (shouldDo == 1) {
					double direction = Math.toRadians(attacker.renderYawOffset);
					double targetX = entitylivingbase.posX - attacker.posX;
					double targetY = entitylivingbase.getEntityBoundingBox().minY + (double) (entitylivingbase.height) - (attacker.posY + (double) (attacker.height));
					double targetZ = entitylivingbase.posZ - attacker.posZ;
					EntityThrownSand thrownsand = new EntityThrownSand(worldObj, attacker);
					thrownsand.setPosition(attacker.posX - Math.sin(direction) * 3.5, attacker.posY + attacker.height, attacker.posZ + Math.cos(direction) * 3.5);
					thrownsand.shoot(targetX, targetY, targetZ, 0.7F, 0.0F);
					worldObj.spawnEntity(thrownsand);
					attackTick = 30;
				}
			}

		if (attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ) > distance && attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ) <= distance + 256.0D)
			if (attackTick <= 0) {
				int x = worldObj.rand.nextInt(4);
				if (x == 0) {
					attackTick = 60;
					attacker.motionY = 0.61999998688697815D;
					jumpAttack = true;
				} else if (x == 1 && !jumpAttack && attacker.onGround) {
					attackTick = 60;
					attacker.motionY = 0D;
					((EntityAntlionBoss) attacker).setBlam(10, (byte) 1);
				}
			}
		if (jumpAttack && attacker.collidedVertically && !(attacker.motionY > 0D)) {
			areaOfEffect();
			((EntityAntlionBoss) attacker).spawnBlamParticles();
			jumpAttack = false;
		}
	}

	protected Entity areaOfEffect() {
		List<?> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(8D, 1D, 8D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity) list.get(i);
			if (entity != null)
				if (entity instanceof EntityLivingBase && !(entity instanceof EntityAntlionBoss)) {
					entity.attackEntityFrom(DamageSource.causeMobDamage(attacker), (float) (ConfigHandler.INSTANCE.mobAttackDamageMultiplier < 2 ? 8D : 8D * ConfigHandler.INSTANCE.mobAttackDamageMultiplier));
					entity.addVelocity(-MathHelper.sin(attacker.rotationYaw * 3.141593F / 180.0F) * 1D, 0.4D, MathHelper.cos(attacker.rotationYaw * 3.141593F / 180.0F) * 1D);
					worldObj.playSound(null, entity.getPosition(), ModSounds.ANTLION_SLAM, SoundCategory.HOSTILE, 1.0F, 1.0F);
					worldObj.playSound(null, entity.getPosition(), ModSounds.ANTLION_EXPLODE, SoundCategory.HOSTILE, 1.0F, 1.0F);;
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 8 * 20, 0));
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 8 * 20, 0));
				}
		}
		return null;
	}
}

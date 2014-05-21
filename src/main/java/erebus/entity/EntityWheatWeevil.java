package erebus.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import erebus.ModItems;
import erebus.item.ItemErebusMaterial.DATA;

public class EntityWheatWeevil extends EntityCreature {

	public EntityWheatWeevil(World world) {
		super(world);
		stepHeight = 0.0F;
		setSize(1.0F, 0.5F);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAvoidEntity(this, EntityPlayer.class, 10.0F, 0.7D, 0.5D));
		tasks.addTask(2, new EntityAIAvoidEntity(this, EntityMob.class, 10.0F, 0.7D, 0.5D));
		tasks.addTask(3, new EntityAIWander(this, 0.5D));
		tasks.addTask(4, new EntityAIPanic(this, 0.7F));
		tasks.addTask(5, new EntityAILookIdle(this));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		float light = getBrightness(1.0F);
		if (light >= 0F)
			return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
		return super.getCanSpawnHere();
	}
	
	@Override
    public int getMaxSpawnedInChunk() {
        return 2;
    }

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D); // Movespeed
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D); // MaxHealth
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	protected String getLivingSound() {
		return "erebus:beetleSound";
	}

	@Override
	protected String getHurtSound() {
		return "erebus:beetleHurt";
	}

	@Override
	protected String getDeathSound() {
		return "erebus:squish";
	}

	@Override
	protected void playStepSound(int x, int y, int z, int blockID) {
		playSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		switch (rand.nextInt(5)) {
		case 0:
			entityDropItem(new ItemStack(ModItems.flowerSeeds, 1 + rand.nextInt(3) + looting, rand.nextInt(14)), 0F);
			break;
		case 1:
			ItemStack seed = ForgeHooks.getGrassSeed(worldObj);
			seed.stackSize = 1 + rand.nextInt(3) + looting;
			entityDropItem(seed, 0F);
			break;
		case 2:
			entityDropItem(new ItemStack(Item.pumpkinSeeds, 1 + rand.nextInt(3) + looting), 0F);
			break;

		case 3:
			entityDropItem(new ItemStack(Item.melonSeeds, 1 + rand.nextInt(3) + looting), 0F);
			break;

		case 4:
			entityDropItem(new ItemStack(Item.dyePowder, 1 + rand.nextInt(3) + looting, 3), 0F);
			break;
		}

		if (rand.nextInt(10) == 0) {
			int dropRareishType = rand.nextInt(7);
			switch (dropRareishType) {
			case 0:
				entityDropItem(new ItemStack(ModItems.turnip, 1 + looting), 0F);
				break;
			case 1:
				entityDropItem(new ItemStack(Item.netherStalkSeeds, 1 + looting), 0F);
				break;

			case 2:
				entityDropItem(new ItemStack(Item.wheat, 1 + looting), 0F);
				break;

			case 3:
				entityDropItem(new ItemStack(Item.reed, 1 + looting), 0F);
				break;

			case 4:
				entityDropItem(new ItemStack(ModItems.erebusMaterials, 1 + looting, DATA.bambooShoot.ordinal()), 0F);
				break;

			case 5:
				entityDropItem(new ItemStack(Item.carrot, 1 + looting), 0F);
				break;

			case 6:
				entityDropItem(new ItemStack(Item.potato, 1 + looting), 0F);
				break;
			}
		}
	}
}
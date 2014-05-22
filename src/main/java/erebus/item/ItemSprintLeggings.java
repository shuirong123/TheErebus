package erebus.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erebus.Erebus;
import erebus.ModItems;
import erebus.item.ItemErebusMaterial.DATA;

public class ItemSprintLeggings extends ItemArmor {
	public static final byte maxTier = 9;

	private int armtick;
	private String texture;

	public ItemSprintLeggings(ArmorMaterial enumarmormaterial, int k) {
		super(enumarmormaterial, 2, k);
		setCreativeTab(Erebus.tabErebusGear);
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == ModItems.erebusMaterials && material.getItemDamage() == DATA.bioVelocity.ordinal();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo) {
		textLines.add(EnumChatFormatting.GRAY + "Tier " + (1 + (is.stackTagCompound == null ? 0 : is.stackTagCompound.getByte("upgradeTier"))));
	}

	@Override
	public String getArmorTexture(ItemStack is, Entity entity, int slot, String type) {
		if (is.getItem() == ModItems.sprintLeggings) {
			if (armtick >= 0 && armtick <= 20)
				texture = "erebus:textures/models/armor/centipede0.png";
			if (armtick > 20 && armtick <= 40)
				texture = "erebus:textures/models/armor/centipede1.png";
			if (armtick > 40 && armtick <= 60)
				texture = "erebus:textures/models/armor/centipede2.png";
		}
		return texture;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack is) {
		if (player.isSprinting() && player.onGround) {
			byte tier = (byte) (3 + (is.stackTagCompound == null ? 0 : is.stackTagCompound.getByte("upgradeTier")));
			player.motionX *= 1D + tier * .0425D;
			player.motionZ *= 1D + tier * .0425D;
		}
		armtick++;
		if (armtick > 60 || player.isSprinting())
			armtick = 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		list.add(new ItemStack(id, 1, 0));

		ItemStack is = new ItemStack(id, 1, 0);
		(is.stackTagCompound = new NBTTagCompound()).setByte("upgradeTier", (byte) 8);
		list.add(is);
	}
}
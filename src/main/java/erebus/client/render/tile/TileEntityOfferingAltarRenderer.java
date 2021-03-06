package erebus.client.render.tile;

import erebus.Erebus;
import erebus.ModBlocks;
import erebus.client.model.block.ModelOfferingAltar;
import erebus.tileentity.TileEntityOfferingAltar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityOfferingAltarRenderer extends TileEntitySpecialRenderer<TileEntityOfferingAltar> {
	private final ResourceLocation TEXTURE = new ResourceLocation("erebus:textures/special/tiles/offering_altar.png");
	private final ModelOfferingAltar MODEL = new ModelOfferingAltar();

	public void renderTile(TileEntityOfferingAltar tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		if(state == null || state.getBlock() != ModBlocks.ALTAR_OFFERING)
			return;
		bindTexture(TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		MODEL.render();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
		renderItems(tile, partialTick, 0, 0, 0);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(TileEntityOfferingAltar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(tile == null || !tile.hasWorld()) {
			renderTileAsItem(x, y, z);
			return;
		}
		renderTile(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	private void renderTileAsItem(double x, double y, double z) {
		GlStateManager.pushMatrix();
		bindTexture(TEXTURE);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		MODEL.render();
		GlStateManager.popMatrix();
	}

	private void renderItems(TileEntityOfferingAltar tile, float partialTick, double x, double y, double z) {
		float angle = tile.time;
		if (tile.getStackInSlot(3).isEmpty()) {
			GlStateManager.translate(0F, 0.75, 0F);
			for (int i = 0; i < 3; i++) {
				ItemStack item = tile.getStackInSlot(i);
				if (!item.isEmpty()) {
					GlStateManager.pushMatrix();
					GlStateManager.rotate((float)120 * (i + 1) + tile.getWorld().getTotalWorldTime(), 0F, 1F, 0F);
					GlStateManager.translate(Math.cos(Math.toRadians(angle)), 0, 0);
					bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					if(item.getItem() instanceof ItemBlock)
						GlStateManager.scale(0.3, 0.3, 0.3);
					else
						GlStateManager.scale(0.5, 0.5, 0.5);
					GlStateManager.pushMatrix();
					GlStateManager.rotate(((float)120 * (i + 1) + tile.getWorld().getTotalWorldTime()), 1F, 1F, 1F);
					Minecraft.getMinecraft().getRenderItem().renderItem(item, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(item, (World) null, (EntityLivingBase) null));
					GlStateManager.popMatrix();
					GlStateManager.popMatrix();
					double a = -Math.toRadians((float)120 * (i + 1) + tile.getWorld().getTotalWorldTime() -90);
					double offSetX = -Math.sin(a) * Math.cos(Math.toRadians(angle));
					double offSetZ = Math.cos(a) * Math.cos(Math.toRadians(angle));
					if(tile.getWorld().getTotalWorldTime()%4 == 0)
						Erebus.PROXY.spawnCustomParticle(getParticleType(item.getItem()), tile.getWorld(), tile.getPos().getX() + 0.5F - offSetX , tile.getPos().getY() + 1.5F + (tile.getWorld().rand.nextFloat() - tile.getWorld().rand.nextFloat()) *0.1F, tile.getPos().getZ() + 0.5F - offSetZ, 0.0D, 0.0D, 0.0D);
				}
			}
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0.75F, 0);
			GlStateManager.rotate((float)tile.getWorld().getTotalWorldTime(), 0F, 1F, 0F);
			GlStateManager.scale(0.5, 0.5, 0.5);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Minecraft.getMinecraft().getRenderItem().renderItem(tile.getItemForRendering(3), Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(tile.getItemForRendering(3), (World) null, (EntityLivingBase) null));
			GlStateManager.popMatrix();
		}
	}

	public String getParticleType (Item itemIn) {
		if(itemIn == Item.getItemFromBlock(Blocks.OBSIDIAN))
			return "flame";
		if(itemIn == Items.DIAMOND)
			return "swampflame";
		if(itemIn == Items.EMERALD)
			return "swampflame_green";
		return "flame";
	}
}
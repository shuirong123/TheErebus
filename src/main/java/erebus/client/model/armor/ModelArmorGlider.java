package erebus.client.model.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelArmorGlider extends ModelBiped {

	ModelRenderer Body;
	ModelRenderer RArm;
	ModelRenderer LArm;
	ModelRenderer RWingbase;
	ModelRenderer LWingbase;
	ModelRenderer RWing;
	ModelRenderer LWing;

	public boolean isGliding;

	public ModelArmorGlider() {
		textureWidth = 64;
		textureHeight = 64;

		Body = new ModelRenderer(this, 19, 12);
		Body.addBox(-4F, 0F, -2F, 8, 11, 4);
		Body.setRotationPoint(0F, 0F, 0F);
		setRotation(Body, 0F, 0F, 0F);
		RArm = new ModelRenderer(this, 42, 0);
		RArm.addBox(-3F, -2F, -2F, 4, 5, 4);
		RArm.setRotationPoint(-5F, 2F, 0F);
		setRotation(RArm, 0F, 0F, 0F);
		LArm = new ModelRenderer(this, 0, 0);
		LArm.addBox(-1F, -2F, -2F, 4, 5, 4);
		LArm.setRotationPoint(5F, 2F, 0F);
		setRotation(LArm, 0F, 0F, 0F);
		RWingbase = new ModelRenderer(this, 52, 16);
		RWingbase.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3);
		RWingbase.setRotationPoint(-2F, 2F, 3.5F);
		setRotation(RWingbase, 0F, 0F, 0F);
		LWingbase = new ModelRenderer(this, 0, 16);
		LWingbase.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3);
		LWingbase.setRotationPoint(2F, 2F, 3.5F);
		setRotation(LWingbase, 0F, 0F, 0F);
		RWing = new ModelRenderer(this, 44, 33);
		RWing.addBox(-4.5F, 0F, -0.51F, 9, 14, 1);
		RWing.setRotationPoint(-2F, 3F, 3.5F);
		setRotation(RWing, 0F, 0F, 1.570796F);
		LWing = new ModelRenderer(this, 0, 33);
		LWing.addBox(-4.5F, 0F, -0.5F, 9, 14, 1);
		LWing.setRotationPoint(2F, 3F, 3.5F);
		setRotation(LWing, 0F, 0F, -1.570796F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		GlStateManager.pushMatrix();
        if (entity.isSneaking())
            GlStateManager.translate(0.0F, 0.2F, -0.05F);
		GlStateManager.translate(0.0F, -0.05F, 0.0F);
		GlStateManager.scale(1.1F, 1.2F, 1.3F);
		Body.render(unitPixel);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.15F, -0.05F, 0.0F);
		GlStateManager.scale(1.5F, 1.2F, 1.3F);
		RArm.render(unitPixel);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.15F, -0.05F, 0.0F);
		GlStateManager.scale(1.5F, 1.2F, 1.3F);
		LArm.render(unitPixel);
		GlStateManager.popMatrix();
		RWingbase.render(unitPixel);
		LWingbase.render(unitPixel);

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RWing.render(unitPixel);
		LWing.render(unitPixel);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, prevLimbSwing, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		RArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.5F * prevLimbSwing * 0.5F;
		LArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.5F * prevLimbSwing * 0.5F;
		if (!isGliding) {
			RWing.rotateAngleZ = 0F;
			LWing.rotateAngleZ = 0F;
			if (entity.prevPosX != entity.posX || entity.prevPosZ != entity.posZ) {
				RWing.rotateAngleX = 0.7F;
				LWing.rotateAngleX = 0.7F;
			} else {
				RWing.rotateAngleX = 0.0F;
				LWing.rotateAngleX = 0.0F;
			}
		}
		if (isGliding && !entity.onGround) {
			RWing.rotateAngleZ = 1.570796F;
			LWing.rotateAngleZ = -1.570796F;
		}
		if (entity.isSneaking()) {
			Body.rotateAngleX = 0.5F;
			RArm.rotateAngleX += 0.4F;
			LArm.rotateAngleX += 0.4F;
			RWingbase.rotateAngleX = 0.5F;
			LWingbase.rotateAngleX = 0.5F;
			if (!isGliding) {
				RWing.rotateAngleX = 0.5F;
				LWing.rotateAngleX = 0.5F;
			}
			RWingbase.rotationPointZ = 4.5F;
			LWingbase.rotationPointZ = 4.5F;
			RWing.rotationPointZ = 4.5F;
			LWing.rotationPointZ = 4.5F;
			RArm.rotationPointZ = -0.5F;
			LArm.rotationPointZ = -0.5F;
			RArm.rotationPointY = 4F;
			LArm.rotationPointY = 4F;
		} else {
			Body.rotateAngleX = 0.0F;
			RWingbase.rotateAngleX = 0.0F;
			LWingbase.rotateAngleX = 0.0F;
			RWingbase.rotationPointZ = 3.5F;
			LWingbase.rotationPointZ = 3.5F;
			RWing.rotationPointZ = 3.5F;
			LWing.rotationPointZ = 3.5F;
		}
	}
}

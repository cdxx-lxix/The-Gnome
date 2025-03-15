package com.mladich.ambientguysmod.entity.client;

// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings

import com.mladich.ambientguysmod.entity.ag_entities.RockeaterGnomeEntity;
import com.mladich.ambientguysmod.entity.animations.ModAnimationDefinitions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RockeaterGnomeModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart rockeatergnome;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart head_phys;
    private final ModelPart torso;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart right_arm;
    private final ModelPart left_arm;

    public RockeaterGnomeModel(ModelPart root) {
        this.rockeatergnome = root.getChild("rockeatergnome");
        this.head = this.rockeatergnome.getChild("head");
        this.hat = this.head.getChild("hat");
        this.head_phys = this.head.getChild("head_phys");
        this.torso = this.rockeatergnome.getChild("torso");
        this.right_leg = this.rockeatergnome.getChild("right_leg");
        this.left_leg = this.rockeatergnome.getChild("left_leg");
        this.right_arm = this.rockeatergnome.getChild("right_arm");
        this.left_arm = this.rockeatergnome.getChild("left_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rockeatergnome = partdefinition.addOrReplaceChild("rockeatergnome", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition head = rockeatergnome.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, 4.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(28, 0).addBox(3.0F, 4.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(40, 43).addBox(-3.0F, 4.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 16).addBox(-3.0F, 4.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 18).addBox(-2.0F, 1.0F, 2.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 34).addBox(-3.0F, 1.0F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(18, 34).addBox(2.0F, 1.0F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 44).addBox(-2.0F, 1.0F, -3.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 9).addBox(1.0F, -2.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 36).addBox(-1.0F, -2.0F, 1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 22).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(18, 31).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 9).addBox(-2.0F, -2.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(18, 27).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition head_phys = head.addOrReplaceChild("head_phys", CubeListBuilder.create().texOffs(40, 51).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition torso = rockeatergnome.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -3.0F, 8.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition right_leg = rockeatergnome.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, 5.0F, -6.0F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-2.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 8.0F, 2.0F));

        PartDefinition left_leg = rockeatergnome.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 25).addBox(-2.0F, 5.0F, -6.0F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(12, 43).addBox(-2.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 8.0F, 2.0F));

        PartDefinition right_arm = rockeatergnome.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 43).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition left_arm = rockeatergnome.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 43).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rockeatergnome.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @NotNull ModelPart root() {
        return rockeatergnome;
    }

    @Override
    public void setupAnim(@NotNull T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose); // Prevent weird shit

        this.applyHeadRotation(pNetHeadYaw, pHeadPitch, pAgeInTicks);

        this.animateWalk(ModAnimationDefinitions.ROCKEATER_WALK, pLimbSwing, pLimbSwingAmount, 1f, 2f);
        this.animate(((RockeaterGnomeEntity) pEntity).idleAnimationState, ModAnimationDefinitions.ROCKEATER_IDLE, pAgeInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -15.0F, 15.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }
}

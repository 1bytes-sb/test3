/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package minegame159.meteorclient.mixin;

import minegame159.meteorclient.modules.Modules;
import minegame159.meteorclient.modules.render.CameraClip;
import minegame159.meteorclient.modules.render.FreeRotate;
import minegame159.meteorclient.modules.render.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private boolean thirdPerson;


    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(CameraClip.class)) {
            info.setReturnValue(desiredCameraDistance);
        }
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdateTail(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (Modules.get().isActive(Freecam.class)) {
            this.thirdPerson = true;
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void onUpdateSetPosArgs(Args args) {
        Freecam freecam = Modules.get().get(Freecam.class);

        if (freecam.isActive()) {
            args.set(0, freecam.pos.x);
            args.set(1, freecam.pos.y);
            args.set(2, freecam.pos.z);
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        Freecam freecam = Modules.get().get(Freecam.class);
        FreeRotate freeRotate = Modules.get().get(FreeRotate.class);

        if (freecam.isActive()) {
            args.set(0, freecam.yaw);
            args.set(1, freecam.pitch);
        }

        if (freeRotate.isActive()) {
            args.set(0, freeRotate.cameraYaw);
            args.set(1, freeRotate.cameraPitch);
        }
    }
}

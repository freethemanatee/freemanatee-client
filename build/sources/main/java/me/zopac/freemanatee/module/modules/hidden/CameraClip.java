package me.zopac.freemanatee.module.modules.hidden;

import me.zopac.freemanatee.mixin.client.MixinEntityRenderer;
import me.zopac.freemanatee.module.Module;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.Vec3d;

/**
 * Created by 086 on 11/12/2017.
 * @see MixinEntityRenderer#rayTraceBlocks(WorldClient, Vec3d, Vec3d)
 */
@Module.Info(name = "CameraClip", category = Module.Category.HIDDEN, description = "Allows your camera to pass through blocks")
public class CameraClip extends Module {}

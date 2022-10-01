package com.pg85.otg.forge.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import com.pg85.otg.forge.gen.OTGNoiseChunkGenerator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocateCommand.class)
public abstract class MixinLocateCommand
{

	@Shadow
	public static int showLocateResult(CommandSourceStack arg, ResourceOrTagLocationArgument.Result<?> arg2, BlockPos arg3, Pair<BlockPos, ? extends Holder<?>> pair, String string)
	{
		return 0;
	}

	@Shadow @Final private static SimpleCommandExceptionType ERROR_FAILED;

	@Inject(method = "locate", at = @At("HEAD"), cancellable = true)
	private static void searchInSmallerRadius(CommandSourceStack source, ResourceOrTagLocationArgument.Result<ConfiguredStructureFeature<?, ?>> structure, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException
	{
		if (source.getLevel().getChunkSource().getGenerator() instanceof OTGNoiseChunkGenerator)
		{
			BlockPos blockpos = new BlockPos(source.getPosition());
			Registry<ConfiguredStructureFeature<?, ?>> registry = source.getLevel().registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
			HolderSet<ConfiguredStructureFeature<?, ?>> holders = structure.unwrap().map(a -> registry.getHolder(a).map((arg) -> HolderSet.direct(new Holder[]{arg})), registry::getTag).get();

			Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> blockpos1 = source.getLevel().getChunkSource().getGenerator().findNearestMapFeature(source.getLevel(), holders, blockpos, 20, false);
			if (blockpos1 == null)
			{
				throw ERROR_FAILED.create();
			} else {
				int ret = showLocateResult(source, structure, blockpos, blockpos1, "commands.locate.success");
				cir.setReturnValue(ret);
			}
		}
	}
}

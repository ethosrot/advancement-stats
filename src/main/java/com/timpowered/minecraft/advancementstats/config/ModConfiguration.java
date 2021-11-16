package com.timpowered.minecraft.advancementstats.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	
	public static final ForgeConfigSpec.ConfigValue<Boolean> doLazyAccounting;
	public static final ForgeConfigSpec.ConfigValue<Boolean> excludeRecipes;
	
	static {
		BUILDER.push("General");
		
		doLazyAccounting = BUILDER.comment("Defines how the server tallies new advancements... may save on performance (default:false)")
				.define("doLazyAccounting", false);
		excludeRecipes = BUILDER.comment("Instructs the server to ignore advancements without display text, e.g. recipes; keeps score to true advancements. (default:true)")
				.define("excludeRecipes", true);
		
		BUILDER.pop();
		SPEC = BUILDER.build();
		
	}
}
package com.timpowered.minecraft.advancementstats.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	
	public static final ForgeConfigSpec.ConfigValue<Boolean> doLazyAccounting;
	public static final ForgeConfigSpec.ConfigValue<Boolean> excludeRecipes;
	public static final ForgeConfigSpec.ConfigValue<Integer> challengePointValue;
	public static final ForgeConfigSpec.ConfigValue<Integer> goalPointValue;
	public static final ForgeConfigSpec.ConfigValue<Integer> taskPointValue;
	
	static {
		
		// Overall Configuration
		BUILDER.push("General");
		
		doLazyAccounting = BUILDER.comment("Defines how the server tallies new advancements... may save on performance (default:false)")
				.define("doLazyAccounting", false);
		excludeRecipes = BUILDER.comment("Instructs the server to ignore advancements without display text, e.g. recipes; keeps score to true advancements. (default:true)")
				.define("excludeRecipes", true);
		
		BUILDER.pop();
		
		// Pointwise Configuration
		BUILDER.push("PointValues");
		
		challengePointValue = BUILDER.comment("Defines the point value of CHALLENGE advancements (default:10)")
				.defineInRange("challengePointValue", 10, 0, 100);
		
		goalPointValue = BUILDER.comment("Defines the point value of GOAL advancements (default:5)")
				.defineInRange("goalPointValue", 5, 0, 100);
		
		taskPointValue = BUILDER.comment("Defines the point value of TASK advancements (default:2)")
				.defineInRange("taskPointValue", 2, 0, 100);
		BUILDER.pop();
		
		SPEC = BUILDER.build();
		
	}
}
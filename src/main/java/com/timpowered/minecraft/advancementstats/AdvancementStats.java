package com.timpowered.minecraft.advancementstats;

import com.timpowered.minecraft.advancementstats.config.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.advancements.*;
import java.util.Collection;

@Mod("advancementstats")
public class AdvancementStats {
	
	// Reference logger
	private static final Logger LOGGER = LogManager.getLogger("Advancement Stats");
	
	public AdvancementStats() {
		if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) {
			LOGGER.warn("This mod is intended to be used on dedicated servers!");
		}
		ModLoadingContext.get().registerConfig(Type.SERVER, ModConfiguration.SPEC, "advancementstats.toml");
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void advancement(AdvancementEvent event) {
		if(event.getPlayer().getServer().getPlayerList().getPlayerAdvancements((ServerPlayerEntity) event.getPlayer()).getOrStartProgress(event.getAdvancement()).isDone()) {
			final Scoreboard board = event.getPlayer().getServer().getScoreboard();
			
			// Make sure our scoreboard objective exists. If not, create it.
			if(board.getObjective("advancements") == null) {
				board.addObjective("advancements", ScoreCriteria.DUMMY, new StringTextComponent("Completed Advancements"), ScoreCriteria.RenderType.INTEGER);
			}
			
			if(ModConfiguration.doLazyAccounting.get()) {
				// Upon advancement event, simply add one, do not recount.
				if(ModConfiguration.excludeRecipes.get()) {
					if(event.getAdvancement().getDisplay() == null) {
						return;
					}
				}
				board.getOrCreatePlayerScore(event.getPlayer().getName().getString(), board.getOrCreateObjective("advancements")).increment();
			} else {
				// Upon advancement event, re-tally achievements and store result.
				int completedAdvancements = 0;
				final Collection<Advancement> serverAdvancements = event.getPlayer().getServer().getAdvancements().getAllAdvancements();
				for(Advancement advancement : serverAdvancements) {
					if(event.getPlayer().getServer().getPlayerList().getPlayerAdvancements((ServerPlayerEntity) event.getPlayer()).getOrStartProgress(advancement).isDone()) {
						if(ModConfiguration.excludeRecipes.get()) {
							if(advancement.getDisplay() == null) {
								continue;
							}
						}
						completedAdvancements++;
					}
				}
				board.getOrCreatePlayerScore(event.getPlayer().getName().getString(),  board.getOrCreateObjective("advancements")).setScore(completedAdvancements);
			}
		}
	}
}

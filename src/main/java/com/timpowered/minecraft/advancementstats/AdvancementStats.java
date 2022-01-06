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
	
	private void progressScoringPlayerObjective(AdvancementEvent event, int pointValue) {
		final Scoreboard board = event.getPlayer().getServer().getScoreboard();
		board.getOrCreatePlayerScore(event.getPlayer().getName().getString(), board.getOrCreateObjective("advancements")).add(pointValue);
	}
	
	private void setScoringPlayerObjective(AdvancementEvent event, int pointValue) {
		final Scoreboard board = event.getPlayer().getServer().getScoreboard();
		board.getOrCreatePlayerScore(event.getPlayer().getName().getString(), board.getOrCreateObjective("advancements")).setScore(pointValue);
	}
	
	private int getAdvancementPointValue(Advancement advancement) {
		if(advancement.getDisplay() == null) {
			if(ModConfiguration.excludeRecipes.get()) {
				return 0;
			} else {
				return 1;
			}
		}
		switch (advancement.getDisplay().getFrame()) {
			case CHALLENGE:
				return ModConfiguration.challengePointValue.get();
			case GOAL:
				return ModConfiguration.goalPointValue.get();
			case TASK:
				return ModConfiguration.taskPointValue.get();
			default:
				break;
		}
		return 0;
	}
	
	@SubscribeEvent
	public void advancement(AdvancementEvent event) {
		if(event.getPlayer().getServer().getPlayerList().getPlayerAdvancements((ServerPlayerEntity) event.getPlayer()).getOrStartProgress(event.getAdvancement()).isDone()) {
			final Scoreboard board = event.getPlayer().getServer().getScoreboard();
			if(board.getObjective("advancements") == null) {
				board.addObjective("advancements", ScoreCriteria.DUMMY, new StringTextComponent("Advancement Score"), ScoreCriteria.RenderType.INTEGER);
			}	
			if(ModConfiguration.doLazyAccounting.get()) {
				this.progressScoringPlayerObjective(event, this.getAdvancementPointValue(event.getAdvancement()));
			} else {
				int completedAdvancementsValue = 0;
				final Collection<Advancement> serverAdvancements = event.getPlayer().getServer().getAdvancements().getAllAdvancements();
				for(Advancement advancement : serverAdvancements) {
					if(event.getPlayer().getServer().getPlayerList().getPlayerAdvancements((ServerPlayerEntity) event.getPlayer()).getOrStartProgress(advancement).isDone()) {
						completedAdvancementsValue += this.getAdvancementPointValue(advancement);
					}
				}
				this.setScoringPlayerObjective(event, completedAdvancementsValue);
			}
		}
	}
}

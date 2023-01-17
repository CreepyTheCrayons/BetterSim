package dev.kyro.pitsim.aitems.mobdrops;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.enums.AuctionCategory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RottenFlesh extends StaticPitItem {

	public RottenFlesh() {
		hasDropConfirm = true;
		auctionCategory = AuctionCategory.DARKZONE_DROPS;
	}

	@Override
	public String getNBTID() {
		return "rotten-flesh";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("flesh", "rottenflesh"));
	}

	@Override
	public Material getMaterial() {
		return Material.ROTTEN_FLESH;
	}

	@Override
	public String getName() {
		return "&aRotten Flesh";
	}

	@Override
	public List<String> getLore() {
		return new ALoreBuilder(
				"&7Flesh gathered from the zombies",
				"&7of the Zombie Caves",
				"",
				"&5Tainted Item"
		).getLore();
	}
}

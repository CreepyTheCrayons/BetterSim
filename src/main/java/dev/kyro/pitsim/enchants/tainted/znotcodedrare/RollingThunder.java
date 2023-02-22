package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class RollingThunder extends PitEnchant {
	public static RollingThunder INSTANCE;

	public RollingThunder() {
		super("Rolling Thunder", true, ApplyType.SCYTHES,
				"rollingthunder", "roll", "rolling", "thunder");
		isTainted = true;
		INSTANCE = this;
	}



//	FallingBlock fallingBlock = new FallingBlock(block.getType(), block.getData(), blockLocation.add(0, 1, 0));
//	fallingBlock.setViewers(viewers);
//	fallingBlock.spawnBlock();
//	fallingBlock.setVelocity(vector);
//	fallingBlock.removeAfter((int) (delay + 5));


	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}
}

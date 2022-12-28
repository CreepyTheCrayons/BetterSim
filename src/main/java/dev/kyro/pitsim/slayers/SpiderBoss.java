package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.TempBlock;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpiderBoss extends PitBoss {
	public NPC npc;
	public Player entity;
	public Player target;
	public String name = "&c&lSpider Boss";
	public SubLevel subLevel = SubLevel.SPIDER_CAVE;
	public SimpleBoss boss;

	public SpiderBoss(Player target) throws Exception {
		super(target, SubLevel.SPIDER_CAVE, 16);
		this.target = target;

		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

		this.boss = new SimpleBoss(npc, target, subLevel, 3, SimpleSkin.SPIDER, this) {
			@Override
			protected void attackHigh() {

			}

			@Override
			protected void attackMedium() {
				if(target.getLocation().getBlock().getType() == null || target.getLocation().getBlock().getType() == Material.AIR) {
					new TempBlock(target.getLocation(), Material.WEB, 3);
				}
			}

			@Override
			protected void attackLow() {
				if(npc.getEntity() != null) {
					Vector dirVector = target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
					Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));
					npc.getEntity().setVelocity(pullVector.multiply((0.5 * 0.2) + 1.15));
				}

			}

			@Override
			protected void defend() {

			}
		};
		this.entity = (Player) npc.getEntity();
		this.target = target;

		boss.run();
	}

	@Override
	public void onAttack(AttackEvent.Apply event) throws Exception {
		boss.attackAbility(event);
	}

	@Override
	public void onDefend() {
		boss.defendAbility();
	}

	@Override
	public void onDeath() {
		boss.hideActiveBossBar();
		NoteBlockAPI.stopPlaying(target);
	}

	@Override
	public void setNPC(NPC npc) {
		this.npc = npc;
	}

	@Override
	public Player getEntity() {
		return (Player) npc.getEntity();
	}


}

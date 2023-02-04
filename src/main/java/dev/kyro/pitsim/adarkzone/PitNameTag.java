package dev.kyro.pitsim.adarkzone;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.NameTaggable;
import dev.kyro.pitsim.PitSim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PitNameTag {
	public static List<PitNameTag> activeNameTags = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitNameTag nameTag : activeNameTags) nameTag.update();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	public NameTaggable taggable;
	public NameTagType nameTagType;

	private final List<LivingEntity> entities = new ArrayList<>();
	private ArmorStand armorStand;

	private Location spawnLocation;

	public PitNameTag(NameTaggable taggable, NameTagType nameTagType) {
		this.taggable = taggable;
		this.nameTagType = nameTagType;
		this.spawnLocation = taggable.getTaggableEntity().getLocation();
		this.spawnLocation.setY(255);
		activeNameTags.add(this);
	}

	public PitNameTag addMob(RidingType ridingType) {
		entities.add(ridingType.createEntity(spawnLocation));
		return this;
	}

	public void attach() {
		LivingEntity lowerEntity = taggable.getTaggableEntity();
		for(LivingEntity nextEntity : entities) {
			lowerEntity.setPassenger(nextEntity);
			lowerEntity = nextEntity;
		}
		createArmorStand();
		lowerEntity.setPassenger(armorStand);
		update();
	}

	public void update() {
		LivingEntity baseEntity = taggable.getTaggableEntity();
		if(nameTagType == NameTagType.NAME) {
			setName(taggable.getDisplayName());
		} else if(nameTagType == NameTagType.NAME_AND_HEALTH) {
			int maxHealth = (int) baseEntity.getMaxHealth();
			int length = (int) Math.ceil(Math.min(Math.max(maxHealth - 20, 0) + Math.sqrt(maxHealth), 20));
			double percentFull = baseEntity.getHealth() / baseEntity.getMaxHealth();
			String healthBar = AUtil.createProgressBar("|", taggable.getChatColor(), ChatColor.GRAY, length, percentFull);
			setName(taggable.getDisplayName() + "&8 [" + healthBar + "&8]");
		}
	}

	public void remove() {
		for(LivingEntity entity : entities) entity.remove();
		armorStand.remove();
		activeNameTags.remove(this);
	}

	private void createArmorStand() {
		armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
		armorStand.setGravity(false);
		armorStand.setVisible(true);
		armorStand.setCustomNameVisible(true);
		armorStand.setRemoveWhenFarAway(false);
		armorStand.setVisible(false);
		armorStand.setSmall(true);
		armorStand.setMarker(true);
	}

	private void setName(String text) {
		armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
	}

	public List<LivingEntity> getEntities() {
		return entities;
	}

	public ArmorStand getArmorStand() {
		return armorStand;
	}

	public enum RidingType {
		SMALL_MAGMA_CUBE,
		BABY_RABBIT;

		public LivingEntity createEntity(Location spawnLocation) {
			LivingEntity livingEntity = null;
			switch(this) {
				case SMALL_MAGMA_CUBE:
					MagmaCube magmaCube = (MagmaCube) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.MAGMA_CUBE);
					magmaCube.setSize(1);
					livingEntity = magmaCube;
					break;
				case BABY_RABBIT:
					Rabbit rabbit = (Rabbit) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.RABBIT);
					rabbit.setBaby();
					livingEntity = rabbit;
			}
			livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
			livingEntity.setCustomNameVisible(false);
			livingEntity.setRemoveWhenFarAway(false);
			return livingEntity;
		}
	}

	public enum NameTagType {
		NAME,
		NAME_AND_HEALTH
	}
}

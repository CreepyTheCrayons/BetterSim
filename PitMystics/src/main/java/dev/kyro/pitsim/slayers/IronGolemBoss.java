package dev.kyro.pitsim.slayers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.ThrowBlockEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.ThrowableBlock;
import dev.kyro.pitsim.slayers.tainted.SimpleBoss;
import dev.kyro.pitsim.slayers.tainted.SimpleSkin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class IronGolemBoss extends PitBoss {
    public NPC npc;
    public Player entity;
    public Player target;
    public String name = "&c&lIron Golem";
    public SubLevel subLevel = SubLevel.GOLEM_CAVE;
    public SimpleBoss boss;

    public IronGolemBoss(Player target) {
        super(target, SubLevel.GOLEM_CAVE, 100);
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

        this.boss = new SimpleBoss(npc, target, subLevel, 5, SimpleSkin.IRON_GOLEM, this){

            @Override
            protected void attackHigh() {
                Sounds.CLEAVE1.play(IronGolemBoss.this.target);
                IronGolemBoss.this.target.setVelocity(new Vector(0, 10, 0));
            }

            @Override
            protected void attackMedium() {
                for(int i = 0; i < 5; i++){
                    Vector diff = target.getLocation().add(0.5, 1, 0.5).subtract(npc.getEntity().getLocation().clone().add(0, 1, 0)).toVector();
                    Location base = npc.getEntity().getLocation().clone().add(0, 1, 0)/* the origin, where you are moving away from */;
                    double add = diff.length(); //example amount
                    diff.divide(new Vector(add, add, add));

                    Sounds.RGM.play(target);

                    for (int j = 0; j < add; j++) {
                        base.add(diff);
                        target.getWorld().spigot().playEffect(base, Effect.MAGIC_CRIT, 0, 0, (float) 0, (float) 0 / 255, (float) 0 / 255, 1, 0, 64);
                    }

                    EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(npc.getEntity(), target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4);
                    Bukkit.getServer().getPluginManager().callEvent(damageEvent);
                    if (!damageEvent.isCancelled()) target.damage(8, target);
                }
            }

            @Override
            protected void attackLow(){
                Vector dirVector = IronGolemBoss.this.target.getLocation().toVector().subtract(npc.getEntity().getLocation().toVector()).setY(0);
                Vector pullVector = dirVector.clone().normalize().setY(0.2).multiply(0.5).add(dirVector.clone().multiply(0.03));

                if(npc.getEntity() != null)
                    ThrowBlockEvent.addThrowableBlock(new ThrowableBlock(npc.getEntity(), Material.ANVIL, pullVector.multiply((0.5 * 0.2) + 1.15)) {
                        @Override
                        public void run(EntityChangeBlockEvent event){
                            event.getEntity().getWorld().playEffect(event.getEntity().getLocation(), Effect.MAGIC_CRIT, 1);

                            for (Entity player : event.getEntity().getNearbyEntities(10, 10, 10)) {
                                
                                if(!(player instanceof Player)) continue;
                                Sounds.ANVIL_LAND.play((LivingEntity) player);
                                PitPlayer.getPitPlayer((Player) player).damage(10.0, (LivingEntity) this.owner);
                                player.setVelocity(new Vector(0, 3, 0));
                            }
                        }
                    });
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
    public Player getEntity() {
        return (Player) npc.getEntity();
    }

    @Override
    public void setNPC(NPC npc) {
        this.npc = npc;
    }
}
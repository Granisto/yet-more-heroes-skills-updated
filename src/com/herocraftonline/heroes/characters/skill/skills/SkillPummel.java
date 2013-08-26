/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.DebugTimer;
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.RootEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SafeFallEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.StunEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class SkillPummel extends ActiveSkill
/*     */ {
/*  33 */   private Set<Player> chargingPlayers = new HashSet();
/*     */ 
/*     */   public SkillPummel(Heroes paramHeroes)
/*     */   {
/*  37 */     super(paramHeroes, "Pummel");
/*  38 */     setDescription("You jump $3 blocks and pummel your targets on landing! AOE Radius:$2 Damage:$1");
/*  39 */     setUsage("/skill pummel");
/*  40 */     setArgumentRange(0, 1);
/*  41 */     setIdentifiers(new String[] { "skill pummel" });
/*  42 */     setTypes(new SkillType[] { SkillType.PHYSICAL, SkillType.MOVEMENT, SkillType.HARMFUL });
/*  43 */     Bukkit.getServer().getPluginManager().registerEvents(new ChargeEntityListener(this), this.plugin);
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  49 */     long stunDuration = SkillConfigManager.getUseSetting(hero, this, "stun-duration", 10000, false);
/*  50 */     if (stunDuration > 0L) {
/*  51 */       stunDuration = (long)(stunDuration + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000L;
/*  52 */       stunDuration = stunDuration > 0L ? stunDuration : 0L;
/*     */     }
/*  54 */     long slowDuration = SkillConfigManager.getUseSetting(hero, this, "slow-duration", 0, false);
/*  55 */     if (slowDuration > 0L) {
/*  56 */       slowDuration = (long)(slowDuration + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000L;
/*  57 */       slowDuration = slowDuration > 0L ? slowDuration : 0L;
/*     */     }
/*  59 */     long rootDuration = SkillConfigManager.getUseSetting(hero, this, "root-duration", 0, false);
/*  60 */     if (rootDuration > 0L) {
/*  61 */       rootDuration = (long)(rootDuration + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000L;
/*  62 */       rootDuration = rootDuration > 0L ? rootDuration : 0L;
/*     */     }
/*  64 */     long silenceDuration = SkillConfigManager.getUseSetting(hero, this, "silence-duration", 0, false);
/*  65 */     if (silenceDuration > 0L) {
/*  66 */       silenceDuration = (long)(silenceDuration + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000L;
/*  67 */       silenceDuration = silenceDuration > 0L ? silenceDuration : 0L;
/*     */     }
/*  69 */     long invulnDuration = SkillConfigManager.getUseSetting(hero, this, "safefall-duration", 0, false);
/*  70 */     if (invulnDuration > 0L) {
/*  71 */       invulnDuration = (long)(invulnDuration + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000L;
/*  72 */       invulnDuration = invulnDuration > 0L ? invulnDuration : 0L;
/*     */     }
/*  74 */     int damage = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  76 */     damage = damage > 0 ? damage : 0;
/*  77 */     int radius = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 2, false) + SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS_INCREASE.node(), 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  79 */     radius = radius > 0 ? radius : 0;
/*  80 */     int distance = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE.node(), 15, false) + SkillConfigManager.getUseSetting(hero, this, SkillSetting.MAX_DISTANCE_INCREASE.node(), 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  82 */     distance = distance > 0 ? distance : 0;
/*  83 */     String description = getDescription().replace("$1", damage + "").replace("$2", radius + "").replace("$3", distance + "");
/*  84 */     if (stunDuration > 0L) {
/*  85 */       description = description + " Stun:" + stunDuration + "s";
/*     */     }
/*  87 */     if (slowDuration > 0L) {
/*  88 */       description = description + " Slow:" + slowDuration + "s";
/*     */     }
/*  90 */     if (rootDuration > 0L) {
/*  91 */       description = description + " Root:" + rootDuration + "s";
/*     */     }
/*  93 */     if (silenceDuration > 0L) {
/*  94 */       description = description + " Silence:" + silenceDuration + "s";
/*     */     }
/*  96 */     if (invulnDuration > 0L) {
/*  97 */       description = description + " Safefall:" + invulnDuration + "s";
/*     */     }
/*     */ 
/* 102 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/* 104 */     if (cooldown > 0) {
/* 105 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/* 109 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 111 */     if (mana > 0) {
/* 112 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/* 116 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/* 118 */     if (healthCost > 0) {
/* 119 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/* 123 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 125 */     if (staminaCost > 0) {
/* 126 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/* 130 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/* 131 */     if (delay > 0) {
/* 132 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/* 136 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/* 137 */     if (exp > 0) {
/* 138 */       description = description + " XP:" + exp;
/*     */     }
/* 140 */     return description;
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/* 146 */     ConfigurationSection localConfigurationSection = super.getDefaultConfig();
/* 147 */     localConfigurationSection.set("stun-duration", Integer.valueOf(5000));
/* 148 */     localConfigurationSection.set("slow-duration", Integer.valueOf(0));
/* 149 */     localConfigurationSection.set("root-duration", Integer.valueOf(0));
/* 150 */     localConfigurationSection.set("silence-duration", Integer.valueOf(0));
/* 151 */     localConfigurationSection.set("safefall-duration", Integer.valueOf(0));
/* 152 */     localConfigurationSection.set("duration-increase", Integer.valueOf(0));
/* 153 */     localConfigurationSection.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/* 154 */     localConfigurationSection.set("damage-increase", Integer.valueOf(0));
/* 155 */     localConfigurationSection.set(SkillSetting.RADIUS.node(), Integer.valueOf(2));
/* 156 */     localConfigurationSection.set(SkillSetting.RADIUS_INCREASE.node(), Integer.valueOf(0));
/* 157 */     localConfigurationSection.set(SkillSetting.MAX_DISTANCE.node(), Integer.valueOf(15));
/* 158 */     localConfigurationSection.set(SkillSetting.MAX_DISTANCE_INCREASE.node(), Integer.valueOf(0));
/* 159 */     localConfigurationSection.set(SkillSetting.USE_TEXT.node(), "%hero% used %skill%!");
/* 160 */     return localConfigurationSection;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero paramHero, String[] paramArrayOfString)
/*     */   {
/* 166 */     final Player localPlayer = paramHero.getPlayer();
/* 167 */     Location localLocation1 = localPlayer.getLocation();
/* 168 */     int distance = (int)(SkillConfigManager.getUseSetting(paramHero, this, SkillSetting.MAX_DISTANCE.node(), 15, false) + SkillConfigManager.getUseSetting(paramHero, this, SkillSetting.MAX_DISTANCE_INCREASE.node(), 0.0D, false) * paramHero.getSkillLevel(this));
/*     */ 
/* 170 */     distance = distance > 0 ? distance : 0;
/* 171 */     Location localLocation2 = localPlayer.getTargetBlock(null, distance).getLocation();
/* 172 */     double d1 = localLocation2.getX() - localLocation1.getX();
/* 173 */     double d2 = localLocation2.getZ() - localLocation1.getZ();
/* 174 */     double d3 = Math.sqrt(d1 * d1 + d2 * d2);
/* 175 */     double d4 = localLocation2.distance(localLocation1) / 8.0D;
/* 176 */     d1 = d1 / d3 * d4;
/* 177 */     d2 = d2 / d3 * d4;
/* 178 */     localPlayer.setVelocity(new Vector(d1, 2.0D, d2));
/* 179 */     this.chargingPlayers.add(localPlayer);
/* 180 */     this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 185 */         localPlayer.setFallDistance(8.0F);
/*     */       }
/*     */     }
/*     */     , 1L);
/*     */ 
/* 189 */     broadcastExecuteText(paramHero);
/* 190 */     return SkillResult.NORMAL;
/*     */   }
/*     */ 
/*     */   public class ChargeEntityListener implements Listener
/*     */   {
/*     */     private final Skill skill;
/*     */ 
/*     */     public ChargeEntityListener(Skill arg2)
/*     */     {
/* 199 */       this.skill = arg2;
/*     */     }
/*     */ 
/*     */     @EventHandler
/*     */     public void onEntityDamage(EntityDamageEvent paramEntityDamageEvent)
/*     */     {
/* 205 */       Heroes.debug.startTask("HeroesSkillListener");
/* 206 */       if ((!paramEntityDamageEvent.getCause().equals(EntityDamageEvent.DamageCause.FALL)) || (!(paramEntityDamageEvent.getEntity() instanceof Player)) || (!SkillPummel.this.chargingPlayers.contains((Player)paramEntityDamageEvent.getEntity())))
/*     */       {
/* 208 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 209 */         return;
/*     */       }
/* 211 */       Player localPlayer1 = (Player)paramEntityDamageEvent.getEntity();
/* 212 */       Hero localHero1 = SkillPummel.this.plugin.getCharacterManager().getHero(localPlayer1);
/* 213 */       SkillPummel.this.chargingPlayers.remove(localPlayer1);
/* 214 */       paramEntityDamageEvent.setDamage(0);
/* 215 */       paramEntityDamageEvent.setCancelled(true);
/* 216 */       int i = (int)(SkillConfigManager.getUseSetting(localHero1, this.skill, SkillSetting.RADIUS.node(), 2, false) + SkillConfigManager.getUseSetting(localHero1, this.skill, SkillSetting.RADIUS_INCREASE.node(), 0.0D, false) * localHero1.getSkillLevel(this.skill));
/*     */ 
/* 218 */       i = i > 0 ? i : 0;
/* 219 */       long l1 = SkillConfigManager.getUseSetting(localHero1, this.skill, "stun-duration", 10000, false);
/* 220 */       if (l1 > 0L) {
/* 221 */         l1 = (long)(l1 + SkillConfigManager.getUseSetting(localHero1, this.skill, "duration-increase", 0.0D, false) * localHero1.getSkillLevel(this.skill));
/* 222 */         l1 = l1 > 0L ? l1 : 0L;
/*     */       }
/* 224 */       long l2 = SkillConfigManager.getUseSetting(localHero1, this.skill, "slow-duration", 0, false);
/* 225 */       if (l2 > 0L) {
/* 226 */         l2 = (long)(l2 + SkillConfigManager.getUseSetting(localHero1, this.skill, "duration-increase", 0.0D, false) * localHero1.getSkillLevel(this.skill));
/* 227 */         l2 = l2 > 0L ? l2 : 0L;
/*     */       }
/* 229 */       long l3 = SkillConfigManager.getUseSetting(localHero1, this.skill, "root-duration", 0, false);
/* 230 */       if (l3 > 0L) {
/* 231 */         l3 = (long)(l3 + SkillConfigManager.getUseSetting(localHero1, this.skill, "duration-increase", 0.0D, false) * localHero1.getSkillLevel(this.skill));
/* 232 */         l3 = l3 > 0L ? l3 : 0L;
/*     */       }
/* 234 */       long l4 = SkillConfigManager.getUseSetting(localHero1, this.skill, "silence-duration", 0, false);
/* 235 */       if (l4 > 0L) {
/* 236 */         l4 = (long)(l4 + SkillConfigManager.getUseSetting(localHero1, this.skill, "duration-increase", 0.0D, false) * localHero1.getSkillLevel(this.skill));
/* 237 */         l4 = l4 > 0L ? l4 : 0L;
/*     */       }
/* 239 */       int j = (int)(SkillConfigManager.getUseSetting(localHero1, this.skill, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(localHero1, this.skill, "damage-increase", 0.0D, false) * localHero1.getSkillLevel(this.skill));
/*     */ 
/* 241 */       j = j > 0 ? j : 0;
/* 242 */       long l5 = SkillConfigManager.getUseSetting(localHero1, this.skill, "safefall-duration", 0, false);
/* 243 */       if (l5 > 0L) {
/* 244 */         l5 = (long)(l5 + SkillConfigManager.getUseSetting(localHero1, this.skill, "duration-increase", 0.0D, false) * localHero1.getSkillLevel(this.skill));
/* 245 */         l5 = l5 > 0L ? l5 : 0L;
/* 246 */         if (l5 > 0L) {
/* 247 */           localHero1.addEffect(new SafeFallEffect(this.skill, l5));
/*     */         }
/*     */       }
/* 250 */       Iterator<Entity> localIterator = localPlayer1.getNearbyEntities(i, i, i).iterator();
/* 251 */       while (localIterator.hasNext())
/*     */       {
/* 253 */         Entity localEntity = (Entity)localIterator.next();
/* 254 */         if ((localEntity instanceof LivingEntity))
/*     */         {
/* 257 */           LivingEntity localLivingEntity = (LivingEntity)localEntity;
/* 258 */           if (Skill.damageCheck(localPlayer1, localLivingEntity))
/*     */           {
/* 261 */             if ((localEntity instanceof Player))
/*     */             {
/* 263 */               Player localPlayer2 = (Player)localEntity;
/* 264 */               Hero localHero2 = SkillPummel.this.plugin.getCharacterManager().getHero(localPlayer2);
/* 265 */               if (l1 > 0L) {
/* 266 */                 localHero2.addEffect(new StunEffect(this.skill, l1));
/*     */               }
/* 268 */               if (l2 > 0L) {
/* 269 */                 localHero2.addEffect(new SlowEffect(this.skill, l2, 2, true, localPlayer2.getDisplayName() + " has been slowed by " + localPlayer1.getDisplayName(), localPlayer2.getDisplayName() + " is no longer slowed by " + localPlayer1.getDisplayName(), localHero1));
/*     */               }
/* 271 */               if (l3 > 0L) {
/* 272 */                 localHero2.addEffect(new RootEffect(this.skill, l3));
/*     */               }
/* 274 */               if (l4 > 0L) {
/* 275 */                 localHero2.addEffect(new SilenceEffect(this.skill, l4));
/*     */               }
/* 277 */               if (j > 0) {
/* 278 */                 Skill.damageEntity(localLivingEntity, localPlayer1, j, DamageCause.ENTITY_ATTACK);
/*     */               }
/*     */             }
/* 281 */             if (j > 0) {
/* 282 */               Skill.damageEntity(localLivingEntity, localPlayer1, j, DamageCause.ENTITY_ATTACK);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 287 */       Heroes.debug.stopTask("HeroesSkillListener");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillPummel.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillPummel
 * JD-Core Version:    0.6.2
 */
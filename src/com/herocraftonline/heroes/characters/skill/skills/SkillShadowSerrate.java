/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
/*     */ import com.herocraftonline.heroes.characters.party.HeroParty;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import org.bukkit.Effect;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Creature;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ 
/*     */ public class SkillShadowSerrate extends ActiveSkill
/*     */ {
/*     */   private SkillShadowSerrate blind;
/*     */ 
/*     */   public SkillShadowSerrate(Heroes plugin)
/*     */   {
/*  30 */     super(plugin, "ShadowSerrate");
/*  31 */     setDescription("Utilizing the power of the shadows, targets within a radius of $1 gets damaged for $3 physical damage + players will get silenced for $2 seconds.");
/*  32 */     setUsage("/skill shadowserrate, or sserrate");
/*  33 */     setArgumentRange(0, 0);
/*  34 */     setIdentifiers(new String[] { "skill shadowserrate", "skill sserrate" });
/*  35 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL, SkillType.DEBUFF, SkillType.INTERRUPT });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  40 */     ConfigurationSection node = super.getDefaultConfig();
/*  41 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  42 */     node.set("duration-increase", Integer.valueOf(0));
/*  43 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  44 */     node.set("radius-increase", Integer.valueOf(0));
/*  45 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  46 */     node.set("damage-increase", Integer.valueOf(0));
/*  47 */     return node;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  52 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  54 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  56 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  58 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  61 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  63 */     if (cooldown > 0) {
/*  64 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  68 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  70 */     if (mana > 0) {
/*  71 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  75 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  77 */     if (healthCost > 0) {
/*  78 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  82 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  84 */     if (staminaCost > 0) {
/*  85 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  89 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  90 */     if (delay > 0) {
/*  91 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  95 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  96 */     if (exp > 0) {
/*  97 */       description = description + " XP:" + exp;
/*     */     }
/*  99 */     return description;
/*     */   }
/*     */ 
/*     */   public void init() {
/* 103 */     super.init();
/* 104 */     this.blind = this;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args) {
/* 108 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 110 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 112 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 114 */     Player player = hero.getPlayer();
/* 115 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 116 */       if ((e instanceof Creature)) {
/* 117 */         Creature c = (Creature)e;
/* 118 */         damageEntity(c, player, damage, DamageCause.ENTITY_ATTACK);
/*     */       }
/* 120 */       else if ((e instanceof Player)) {
/* 121 */         Player p = (Player)e;
/* 122 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 125 */           if (damageCheck(player, p)) {
/* 126 */             damageEntity(p, player, damage, DamageCause.ENTITY_ATTACK);
/*     */ 
/* 128 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 129 */             tHero.addEffect(new SilenceEffect(this.blind, duration));
/*     */           }
/*     */         }
/*     */       }
/* 133 */     player.getWorld().playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
/* 134 */     player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 0.0F);
/* 135 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 90);
/* 136 */     broadcastExecuteText(hero);
/* 137 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillShadowSerrate.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillShadowSerrate
 * JD-Core Version:    0.6.2
 */
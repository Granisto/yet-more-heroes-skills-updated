/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.NauseaEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import com.herocraftonline.heroes.characters.skill.TargettedSkill;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Creature;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ 
/*     */ public class SkillFlyingknee extends TargettedSkill
/*     */ {
/*     */   public SkillFlyingknee(Heroes plugin)
/*     */   {
/*  21 */     super(plugin, "Flyingknee");
/*  22 */     setDescription("You run towards your target and knee your target in the stomach, causing them to feel nauseous. Damage:$1, Nausea:$3s");
/*  23 */     setUsage("/skill flyingknee");
/*  24 */     setArgumentRange(0, 0);
/*  25 */     setIdentifiers(new String[] { "skill flyingknee" });
/*  26 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.SILENCABLE, SkillType.HARMFUL, SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.DEBUFF });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  31 */     double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 6, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0.0D, false) * hero.getSkillLevel(this);
/*     */ 
/*  33 */     damage = damage > 0.0D ? damage : 0.0D;
/*  34 */     int radius = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 3, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  36 */     radius = radius > 0 ? radius : 0;
/*  37 */     int duration = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 3000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  39 */     duration = duration > 0 ? duration : 0;
/*  40 */     String description = getDescription().replace("$1", damage + "").replace("$2", radius + "").replace("$3", duration + "");
/*     */ 
/*  43 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  45 */     if (cooldown > 0) {
/*  46 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  50 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  52 */     if (mana > 0) {
/*  53 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  57 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  59 */     if (healthCost > 0) {
/*  60 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  64 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  66 */     if (staminaCost > 0) {
/*  67 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  71 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  72 */     if (delay > 0) {
/*  73 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  77 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  78 */     if (exp > 0) {
/*  79 */       description = description + " XP:" + exp;
/*     */     }
/*  81 */     return description;
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  86 */     ConfigurationSection node = super.getDefaultConfig();
/*  87 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(3));
/*  88 */     node.set("radius-increase", Double.valueOf(0.0D));
/*  89 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(3000));
/*  90 */     node.set("duration-increase", Integer.valueOf(0));
/*  91 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(6));
/*  92 */     node.set("damage-increase", Double.valueOf(0.0D));
/*  93 */     return node;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, LivingEntity target, String[] args)
/*     */   {
/*  98 */     Player player = hero.getPlayer();
/*  99 */     if (((target instanceof Player)) && (((Player)target).equals(player))) {
/* 100 */       return SkillResult.INVALID_TARGET;
/*     */     }
/* 102 */     player.teleport(target.getLocation());
/* 103 */     broadcastExecuteText(hero, target);
/* 104 */     int radius = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 3, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 106 */     radius = radius > 0 ? radius : 0;
/* 107 */     long duration = (long)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 3000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 109 */     duration = duration > 0L ? duration : 0L;
/* 110 */     int damage = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 6, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 112 */     damage = damage > 0 ? damage : 0;
/* 113 */     for (Entity e : hero.getPlayer().getNearbyEntities(radius, radius, radius)) {
/* 114 */       if ((e instanceof Player)) {
/* 115 */         Player tPlayer = (Player)e;
/* 116 */         Hero tHero = this.plugin.getCharacterManager().getHero(tPlayer);
/* 117 */         if (damageCheck(player, tPlayer)) {
/* 118 */           if (duration > 0L) {
/* 119 */             tHero.addEffect(new NauseaEffect(this, duration, "", ""));
/*     */           }
/* 121 */           if (damage > 0) {
/* 122 */             damageEntity(tPlayer, player, damage, DamageCause.ENTITY_ATTACK);
/*     */           }
/*     */         }
/*     */       }
/* 126 */       else if ((e instanceof Creature)) {
/* 127 */         LivingEntity le = (LivingEntity)e;
/* 128 */         if (damage > 0) {
/* 129 */           damageEntity(le, player, damage, DamageCause.ENTITY_ATTACK);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 134 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillFlyingknee.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillFlyingknee
 * JD-Core Version:    0.6.2
 */
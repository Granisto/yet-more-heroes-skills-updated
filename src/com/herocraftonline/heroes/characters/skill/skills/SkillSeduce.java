/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;
/*     */ import com.herocraftonline.heroes.characters.effects.Effect;
/*     */ import com.herocraftonline.heroes.characters.effects.EffectType;
/*     */ import com.herocraftonline.heroes.characters.effects.PeriodicExpirableEffect;
/*     */ import com.herocraftonline.heroes.characters.party.HeroParty;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class SkillSeduce extends ActiveSkill
/*     */ {
/*     */   private String applyText;
/*     */   private String expireText;
/*  29 */   private HashMap<Player, Player> affectedPlayers = new HashMap();
/*     */ 
/*     */   public SkillSeduce(Heroes plugin) {
/*  32 */     super(plugin, "Seduce");
/*  33 */     setDescription("You force a player to come and follow you for $1s.");
/*  34 */     setUsage("/skill seduce");
/*  35 */     setArgumentRange(0, 0);
/*  36 */     setIdentifiers(new String[] { "skill seduce" });
/*     */ 
/*  38 */     setTypes(new SkillType[] { SkillType.DEBUFF, SkillType.COUNTER, SkillType.MOVEMENT, SkillType.PHYSICAL });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  43 */     long duration = ()(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this)) / 1000L;
/*     */ 
/*  45 */     duration = duration > 0L ? duration : 0L;
/*  46 */     String description = getDescription().replace("$1", duration + "");
/*     */ 
/*  49 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  51 */     if (cooldown > 0) {
/*  52 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  56 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  58 */     if (mana > 0) {
/*  59 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  63 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  65 */     if (healthCost > 0) {
/*  66 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  70 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  72 */     if (staminaCost > 0) {
/*  73 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  77 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  78 */     if (delay > 0) {
/*  79 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  83 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  84 */     if (exp > 0) {
/*  85 */       description = description + " XP:" + exp;
/*     */     }
/*  87 */     return description;
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  92 */     ConfigurationSection node = super.getDefaultConfig();
/*  93 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  94 */     node.set("duration-increase", Integer.valueOf(0));
/*  95 */     node.set("exp-per-creature-seduced", Integer.valueOf(0));
/*  96 */     node.set("exp-per-player-seduced", Integer.valueOf(0));
/*  97 */     node.set(SkillSetting.APPLY_TEXT.node(), "%target% is being seduced!");
/*  98 */     node.set(SkillSetting.EXPIRE_TEXT.node(), "%target% is free!");
/*  99 */     return node;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 104 */     super.init();
/* 105 */     this.applyText = SkillConfigManager.getUseSetting(null, this, SkillSetting.APPLY_TEXT.node(), "%target% is being seduced!").replace("%target%", "$1");
/* 106 */     this.expireText = SkillConfigManager.getUseSetting(null, this, SkillSetting.EXPIRE_TEXT.node(), "%target% is free!").replace("%target%", "$1");
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 112 */     List entities = hero.getPlayer().getNearbyEntities(3.0D, 3.0D, 3.0D);
/* 113 */     Player player = hero.getPlayer();
/* 114 */     double expCreature = SkillConfigManager.getUseSetting(hero, this, "exp-per-creature-seduced", 0, false);
/* 115 */     double expPlayer = SkillConfigManager.getUseSetting(hero, this, "exp-per-player-seduced", 0, false);
/* 116 */     double exp = 0.0D;
/* 117 */     long duration = ()(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 119 */     duration = duration > 0L ? duration : 0L;
/* 120 */     for (Entity n : entities) {
/* 121 */       if ((n instanceof org.bukkit.entity.Monster)) {
/* 122 */         ((org.bukkit.entity.Monster)n).setTarget(hero.getPlayer());
/* 123 */         exp += expCreature;
/* 124 */       } else if (((n instanceof Player)) && (n != player) && (Skill.damageCheck(player, (LivingEntity)n))) {
/* 125 */         CurseEffect cEffect = new CurseEffect(this, duration, hero.getPlayer());
/* 126 */         Hero tHero = this.plugin.getCharacterManager().getHero((Player)n);
/* 127 */         tHero.addEffect(cEffect);
/* 128 */         exp += expPlayer;
/*     */       }
/*     */     }
/* 131 */     if (exp > 0.0D) {
/* 132 */       if (hero.hasParty())
/* 133 */         hero.getParty().gainExp(exp, HeroClass.ExperienceType.SKILL, player.getLocation());
/*     */       else {
/* 135 */         hero.gainExp(exp, HeroClass.ExperienceType.SKILL, player.getLocation());
/*     */       }
/*     */     }
/* 138 */     for (Effect e : hero.getEffects()) {
/* 139 */       if ((e.isType(EffectType.DISABLE)) || (e.isType(EffectType.ROOT)) || (e.isType(EffectType.STUN))) {
/* 140 */         hero.removeEffect(e);
/*     */       }
/*     */     }
/*     */ 
/* 144 */     broadcastExecuteText(hero);
/* 145 */     return SkillResult.NORMAL;
/*     */   }
/*     */ 
/*     */   public class CurseEffect extends PeriodicExpirableEffect {
/*     */     private Player caster;
/*     */ 
/*     */     public CurseEffect(Skill skill, long duration, Player caster) {
/* 152 */       super("Seduce", 20L, duration);
/* 153 */       this.types.add(EffectType.HARMFUL);
/* 154 */       this.types.add(EffectType.PHYSICAL);
/* 155 */       this.caster = caster;
/*     */     }
/*     */ 
/*     */     public void applyToHero(Hero hero)
/*     */     {
/* 160 */       super.applyToHero(hero);
/* 161 */       Player player = hero.getPlayer();
/* 162 */       SkillSeduce.this.affectedPlayers.put(player, this.caster);
/* 163 */       broadcast(player.getLocation(), SkillSeduce.this.applyText, new Object[] { player.getDisplayName() });
/*     */     }
/*     */ 
/*     */     public void removeFromHero(Hero hero)
/*     */     {
/* 168 */       super.removeFromHero(hero);
/*     */ 
/* 170 */       Player player = hero.getPlayer();
/* 171 */       if (SkillSeduce.this.affectedPlayers.containsKey(player)) {
/* 172 */         SkillSeduce.this.affectedPlayers.remove(player);
/* 173 */         broadcast(player.getLocation(), SkillSeduce.this.expireText, new Object[] { player.getDisplayName() });
/*     */       }
/*     */     }
/*     */ 
/*     */     public void tickHero(Hero hero)
/*     */     {
/* 179 */       Player player = hero.getPlayer();
/*     */       try {
/* 181 */         if (player.getLocation().distance(this.caster.getLocation()) > 2.0D)
/* 182 */           player.teleport(this.caster);
/*     */       }
/*     */       catch (IllegalArgumentException iae)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     public void tickMonster(com.herocraftonline.heroes.characters.Monster mnstr)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillSeduce.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillSeduce
 * JD-Core Version:    0.6.2
 */
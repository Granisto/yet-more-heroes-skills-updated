/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.party.HeroParty;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import java.util.List;
/*     */ import org.bukkit.Effect;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Monster;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class SkillEarthStrike extends ActiveSkill
/*     */ {
/*     */   public SkillEarthStrike(Heroes plugin)
/*     */   {
/*  28 */     super(plugin, "EarthStrike");
/*  29 */     setUsage("/skill earthstrike");
/*  30 */     setArgumentRange(0, 0);
/*  31 */     setIdentifiers(new String[] { "skill earthstrike" });
/*  32 */     setTypes(new SkillType[] { SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.EARTH, SkillType.FORCE });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  37 */     ConfigurationSection node = super.getDefaultConfig();
/*  38 */     node.set("BaseDamage", Integer.valueOf(3));
/*  39 */     node.set("LevelMultiplier", Double.valueOf(0.0D));
/*  40 */     node.set("Targets", Integer.valueOf(4));
/*  41 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(5));
/*  42 */     node.set("JumpMultiplier", Double.valueOf(0.0D));
/*  43 */     return node;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  48 */     int bDmg = SkillConfigManager.getUseSetting(hero, this, "BaseDamage", 3, false);
/*  49 */     float bMulti = (float)SkillConfigManager.getUseSetting(hero, this, "LevelMultiplier", 0.0D, false);
/*  50 */     int targets = SkillConfigManager.getUseSetting(hero, this, "Targets", 4, false);
/*  51 */     int newDmg = (int)(bMulti <= 0.0F ? bDmg : bDmg + bMulti * hero.getLevel());
/*     */ 
/*  53 */     String base = String.format("You are enraged and bash the ground dealing %s damage and sending %s nearby enemies into the air", new Object[] { Integer.valueOf(newDmg), Integer.valueOf(targets) });
/*     */ 
/*  55 */     StringBuilder description = new StringBuilder(base);
/*     */ 
/*  58 */     int initCD = SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false);
/*  59 */     int redCD = SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*  60 */     int CD = (initCD - redCD) / 1000;
/*  61 */     if (CD > 0) {
/*  62 */       description.append(new StringBuilder().append(" CD:").append(CD).append("s").toString());
/*     */     }
/*     */ 
/*  65 */     int initM = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 0, false);
/*  66 */     int redM = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*  67 */     int manaUse = initM - redM;
/*  68 */     if (manaUse > 0) {
/*  69 */       description.append(new StringBuilder().append(" M:").append(manaUse).toString());
/*     */     }
/*     */ 
/*  72 */     int initHP = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false);
/*  73 */     int redHP = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, 0, true) * hero.getSkillLevel(this);
/*  74 */     int HPCost = initHP - redHP;
/*  75 */     if (HPCost > 0) {
/*  76 */       description.append(new StringBuilder().append(" HP:").append(HPCost).toString());
/*     */     }
/*     */ 
/*  79 */     int initF = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false);
/*  80 */     int redF = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*  81 */     int foodCost = initF - redF;
/*  82 */     if (foodCost > 0) {
/*  83 */       description.append(new StringBuilder().append(" FP:").append(foodCost).toString());
/*     */     }
/*     */ 
/*  86 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  87 */     if (delay > 0) {
/*  88 */       description.append(new StringBuilder().append(" W:").append(delay).toString());
/*     */     }
/*     */ 
/*  91 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  92 */     if (exp > 0) {
/*  93 */       description.append(new StringBuilder().append(" XP:").append(exp).toString());
/*     */     }
/*     */ 
/*  96 */     return description.toString();
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 101 */     Player player = hero.getPlayer();
/* 102 */     int bDmg = SkillConfigManager.getUseSetting(hero, this, "BaseDamage", 3, false);
/* 103 */     float bMulti = (float)SkillConfigManager.getUseSetting(hero, this, "LevelMultiplier", 0.0D, false);
/* 104 */     int targets = SkillConfigManager.getUseSetting(hero, this, "Targets", 4, false);
/* 105 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 5, false);
/* 106 */     float jMod = (float)SkillConfigManager.getUseSetting(hero, this, "JumpMultiplier", 0.0D, false);
/* 107 */     int newDmg = (int)(bMulti <= 0.0F ? bDmg : bDmg + bMulti * hero.getLevel());
/*     */ 
/* 109 */     List nearby = player.getNearbyEntities(radius, radius, radius);
/* 110 */     HeroParty hParty = hero.getParty();
/* 111 */     int hitsLeft = targets;
/* 112 */     Vector flyer = new Vector(0.0F, jMod, 0.0F);
/* 113 */     if (hParty != null)
/* 114 */       for (Entity entity : nearby) {
/* 115 */         if (hitsLeft <= 0) break;
/* 116 */         if ((!(entity instanceof Player)) || (!hParty.isPartyMember((Player)entity)))
/*     */         {
/* 119 */           if ((entity instanceof Monster)) {
/* 120 */             addSpellTarget(entity, hero);
/* 121 */             damageEntity((LivingEntity)entity, player, newDmg, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/* 122 */             entity.setVelocity(flyer);
/* 123 */             hitsLeft--;
/*     */           }
/* 125 */           if ((entity instanceof Player)) {
/* 126 */             if (damageCheck(player, (LivingEntity)entity)) {
/* 127 */               addSpellTarget(entity, hero);
/* 128 */               damageEntity((LivingEntity)entity, player, newDmg, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/*     */             }
/* 130 */             entity.setVelocity(flyer);
/* 131 */             hitsLeft--;
/*     */           }
/*     */         }
/*     */       }
/* 135 */     else for (Entity entity : nearby) {
/* 136 */         if (hitsLeft <= 0) break;
/* 137 */         if ((entity instanceof Monster)) {
/* 138 */           addSpellTarget(entity, hero);
/* 139 */           damageEntity((LivingEntity)entity, player, newDmg, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/* 140 */           entity.setVelocity(flyer);
/* 141 */           hitsLeft--;
/*     */         }
/* 143 */         if ((entity instanceof Player)) {
/* 144 */           if (damageCheck(player, (LivingEntity)entity)) {
/* 145 */             addSpellTarget(entity, hero);
/* 146 */             damageEntity((LivingEntity)entity, player, newDmg, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/*     */           }
/* 148 */           entity.setVelocity(flyer);
/* 149 */           hitsLeft--;
/*     */         }
/*     */       }
/*     */ 
/* 153 */     player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 0.0F);
/* 154 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 98, 1);
/* 155 */     player.getWorld().createExplosion(player.getLocation(), 0.0F, false);
/* 156 */     broadcast(player.getLocation(), "$1 causes a massive shockwave with his weapon!", new Object[] { player.getDisplayName() });
/* 157 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillEarthStrike.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillEarthStrike
 * JD-Core Version:    0.6.2
 */
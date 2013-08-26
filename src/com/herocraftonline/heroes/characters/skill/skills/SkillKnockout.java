/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.BlindEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.NauseaEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import com.herocraftonline.heroes.characters.skill.TargettedSkill;
/*     */ import com.herocraftonline.heroes.util.Util;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class SkillKnockout extends TargettedSkill
/*     */ {
/*     */   private String useText;
/*     */ 
/*     */   public SkillKnockout(Heroes plugin)
/*     */   {
/*  24 */     super(plugin, "Knockout");
/*  25 */     setDescription("You knockout your target by hitting their chin");
/*  26 */     setUsage("/skill knockout <target>");
/*  27 */     setArgumentRange(0, 1);
/*  28 */     setIdentifiers(new String[] { "skill knockout" });
/*  29 */     setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.DEBUFF });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero) {
/*  33 */     StringBuilder descr = new StringBuilder(getDescription());
/*     */ 
/*  35 */     double silenceSec = SkillConfigManager.getUseSetting(hero, this, "nausea-duration", 3000, false) / 1000.0D;
/*  36 */     if (silenceSec > 0.0D) {
/*  37 */       descr.append(" + vision impairment for ");
/*  38 */       descr.append(Util.formatDouble(silenceSec));
/*  39 */       descr.append("s");
/*     */     }
/*  41 */     double blindSec = SkillConfigManager.getUseSetting(hero, this, "blind-duration", 3000, false) / 1000.0D;
/*  42 */     if (blindSec > 0.0D) {
/*  43 */       descr.append(" + vison blindness for ");
/*  44 */       descr.append(Util.formatDouble(blindSec));
/*  45 */       descr.append("s");
/*     */     }
/*     */ 
/*  48 */     double cdSec = SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN, 15000, false) / 1000.0D;
/*  49 */     if (cdSec > 0.0D) {
/*  50 */       descr.append(" CD:");
/*  51 */       descr.append(Util.formatDouble(cdSec));
/*  52 */       descr.append("s");
/*     */     }
/*     */ 
/*  55 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA, 20, false);
/*  56 */     if (mana > 0) {
/*  57 */       descr.append(" M:");
/*  58 */       descr.append(mana);
/*     */     }
/*     */ 
/*  61 */     return descr.toString();
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig() {
/*  65 */     ConfigurationSection defaultConfig = super.getDefaultConfig();
/*  66 */     defaultConfig.set(SkillSetting.USE_TEXT.node(), "%player% used Knockout!");
/*  67 */     defaultConfig.set(SkillSetting.COOLDOWN.node(), Integer.valueOf(15000));
/*  68 */     defaultConfig.set(SkillSetting.MANA.node(), Integer.valueOf(20));
/*  69 */     defaultConfig.set(SkillSetting.MAX_DISTANCE.node(), Integer.valueOf(5));
/*  70 */     defaultConfig.set(SkillSetting.DAMAGE.node(), Integer.valueOf(1));
/*  71 */     defaultConfig.set("nausea-duration", Integer.valueOf(3000));
/*  72 */     defaultConfig.set("blind-duration", Integer.valueOf(3000));
/*  73 */     return defaultConfig;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/*  78 */     super.init();
/*  79 */     this.useText = SkillConfigManager.getRaw(this, SkillSetting.USE_TEXT, "%player% used Knockout!").replace("%player%", "$1");
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, LivingEntity target, String[] args)
/*     */   {
/*  84 */     target.setVelocity(new Vector(Math.random() * 0.8D - 0.4D, 0.16D, Math.random() * 0.8D - 0.4D));
/*     */ 
/*  86 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 1, false);
/*  87 */     damageEntity(target, hero.getPlayer(), damage, DamageCause.ENTITY_ATTACK, false);
/*     */ 
/*  89 */     if ((target instanceof Player)) {
/*  90 */       Player targetPlayer = (Player)target;
/*  91 */       Hero targetHero = this.plugin.getCharacterManager().getHero(targetPlayer);
/*     */ 
/*  93 */       int silenceDuration = SkillConfigManager.getUseSetting(hero, this, "nausea-duration", 3000, false);
/*  94 */       int blindDuration = SkillConfigManager.getUseSetting(hero, this, "blind-duration", 3000, false);
/*  95 */       if (silenceDuration > 0) {
/*  96 */         targetHero.addEffect(new NauseaEffect(this, silenceDuration, "", ""));
/*  97 */         targetHero.addEffect(new BlindEffect(this, blindDuration, "", ""));
/*     */       }
/*     */     }
/*     */ 
/* 101 */     broadcast(target.getLocation(), this.useText, new Object[] { hero.getName() });
/* 102 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillKnockout.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillKnockout
 * JD-Core Version:    0.6.2
 */
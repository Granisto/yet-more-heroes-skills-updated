/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import com.herocraftonline.heroes.characters.skill.TargettedSkill;
/*     */ import com.herocraftonline.heroes.util.Util;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ 
/*     */ public class SkillTrianglechoke extends TargettedSkill
/*     */ {
/*     */   private String useText;
/*     */   private SkillTrianglechoke slow;
/*     */ 
/*     */   public SkillTrianglechoke(Heroes plugin)
/*     */   {
/*  25 */     super(plugin, "Trianglechoke");
/*  26 */     setDescription("You choke your target with your legs");
/*  27 */     setUsage("/skill trianglechoke <target>");
/*  28 */     setArgumentRange(0, 1);
/*  29 */     setIdentifiers(new String[] { "skill trianglechoke" });
/*  30 */     setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.DEBUFF });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero) {
/*  34 */     StringBuilder descr = new StringBuilder(getDescription());
/*     */ 
/*  36 */     double silenceSec = SkillConfigManager.getUseSetting(hero, this, "silence-duration", 3000, false) / 1000.0D;
/*  37 */     if (silenceSec > 0.0D) {
/*  38 */       descr.append(", making them silenced for ");
/*  39 */       descr.append(Util.formatDouble(silenceSec));
/*  40 */       descr.append("s");
/*     */     }
/*  42 */     double blindSec = SkillConfigManager.getUseSetting(hero, this, "slow-duration", 3000, false) / 1000.0D;
/*  43 */     if (blindSec > 0.0D) {
/*  44 */       descr.append(" and severe slow ");
/*  45 */       descr.append(Util.formatDouble(blindSec));
/*  46 */       descr.append("s, they basically get in a sleeper hold.");
/*     */     }
/*     */ 
/*  49 */     double cdSec = SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN, 15000, false) / 1000.0D;
/*  50 */     if (cdSec > 0.0D) {
/*  51 */       descr.append(" CD:");
/*  52 */       descr.append(Util.formatDouble(cdSec));
/*  53 */       descr.append("s");
/*     */     }
/*     */ 
/*  56 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA, 20, false);
/*  57 */     if (mana > 0) {
/*  58 */       descr.append(" M:");
/*  59 */       descr.append(mana);
/*     */     }
/*     */ 
/*  62 */     return descr.toString();
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig() {
/*  66 */     ConfigurationSection defaultConfig = super.getDefaultConfig();
/*  67 */     defaultConfig.set(SkillSetting.USE_TEXT.node(), "%player% used Knockout!");
/*  68 */     defaultConfig.set(SkillSetting.COOLDOWN.node(), Integer.valueOf(15000));
/*  69 */     defaultConfig.set(SkillSetting.MANA.node(), Integer.valueOf(20));
/*  70 */     defaultConfig.set(SkillSetting.MAX_DISTANCE.node(), Integer.valueOf(5));
/*  71 */     defaultConfig.set(SkillSetting.DAMAGE.node(), Integer.valueOf(1));
/*  72 */     defaultConfig.set("silence-duration", Integer.valueOf(3000));
/*  73 */     defaultConfig.set("slow-duration", Integer.valueOf(3000));
/*  74 */     return defaultConfig;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/*  79 */     super.init();
/*  80 */     this.useText = SkillConfigManager.getRaw(this, SkillSetting.USE_TEXT, "%player% used Knockout!").replace("%player%", "$1");
/*  81 */     this.slow = this;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, LivingEntity target, String[] args)
/*     */   {
/*  86 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 1, false);
/*  87 */     damageEntity(target, hero.getPlayer(), damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, false);
/*     */ 
/*  89 */     if ((target instanceof Player)) {
/*  90 */       Player targetPlayer = (Player)target;
/*  91 */       Hero targetHero = this.plugin.getCharacterManager().getHero(targetPlayer);
/*     */ 
/*  93 */       int silenceDuration = SkillConfigManager.getUseSetting(hero, this, "silence-duration", 3000, false);
/*  94 */       int blindDuration = SkillConfigManager.getUseSetting(hero, this, "slow-duration", 3000, false);
/*  95 */       if (silenceDuration > 0) {
/*  96 */         targetHero.addEffect(new SilenceEffect(this, silenceDuration));
/*  97 */         targetHero.addEffect(new SlowEffect(this.slow, blindDuration, 6, false, "", "", hero));
/*     */       }
/*     */     }
/*     */ 
/* 101 */     broadcast(target.getLocation(), this.useText, new Object[] { hero.getName() });
/* 102 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillTrianglechoke.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillTrianglechoke
 * JD-Core Version:    0.6.2
 */
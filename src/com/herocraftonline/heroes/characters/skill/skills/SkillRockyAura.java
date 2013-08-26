/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.EffectType;
/*     */ import com.herocraftonline.heroes.characters.effects.PeriodicEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Effect;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ 
/*     */ public class SkillRockyAura extends ActiveSkill
/*     */ {
/*     */   private String applyText;
/*     */   private String expireText;
/*     */ 
/*     */   public SkillRockyAura(Heroes plugin)
/*     */   {
/*  26 */     super(plugin, "RockyAura");
/*  27 */     setDescription("Toggleable-passive: You stomp on the ground dealing $1 damage with a radius of $2 blocks");
/*  28 */     setUsage("/skill rockyaura");
/*  29 */     setArgumentRange(0, 0);
/*  30 */     setIdentifiers(new String[] { "skill rockyaura", "skill raura" });
/*  31 */     setTypes(new SkillType[] { SkillType.EARTH, SkillType.SILENCABLE, SkillType.HARMFUL, SkillType.BUFF });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  36 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  38 */     int damage = (int)(SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1.0D, false) + SkillConfigManager.getUseSetting(hero, this, "tick-damage-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  40 */     damage = damage > 0 ? damage : 0;
/*  41 */     int radius = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 10, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  43 */     radius = radius > 1 ? radius : 1;
/*  44 */     int mana = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 1, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  46 */     mana = mana > 0 ? mana : 0;
/*  47 */     String description = getDescription().replace("$1", damage + "").replace("$2", radius + "");
/*  48 */     if (mana > 0) {
/*  49 */       description = description + " M:" + mana;
/*     */     }
/*  51 */     return description;
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  56 */     ConfigurationSection node = super.getDefaultConfig();
/*  57 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  58 */     node.set("duration-increase", Integer.valueOf(0));
/*  59 */     node.set("on-text", "%hero% is stomping the ground creating a %skill%!");
/*  60 */     node.set("off-text", "%hero% is no longer creating a %skill%!");
/*  61 */     node.set("tick-damage", Integer.valueOf(1));
/*  62 */     node.set("tick-damage-increase", Integer.valueOf(0));
/*  63 */     node.set(SkillSetting.PERIOD.node(), Integer.valueOf(1000));
/*  64 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  65 */     node.set("radius-increase", Integer.valueOf(0));
/*  66 */     node.set(SkillSetting.MANA.node(), Integer.valueOf(1));
/*  67 */     node.set(SkillSetting.MANA_REDUCE.node(), Double.valueOf(0.0D));
/*  68 */     return node;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/*  73 */     super.init();
/*  74 */     this.applyText = SkillConfigManager.getRaw(this, "on-text", "%hero% is stomping the ground creating a %skill%!").replace("%hero%", "$1").replace("%skill%", "$2");
/*  75 */     this.expireText = SkillConfigManager.getRaw(this, "off-text", "%hero% is no longer creating a %skill%!").replace("%hero%", "$1").replace("%skill%", "$2");
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/*  80 */     if (hero.hasEffect("RockyAura")) {
/*  81 */       hero.removeEffect(hero.getEffect("RockyAura"));
/*     */     } else {
/*  83 */       long period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD.node(), 1000, false);
/*  84 */       int tickDamage = (int)(SkillConfigManager.getUseSetting(hero, this, "tick-damage", 1.0D, false) + SkillConfigManager.getUseSetting(hero, this, "tick-damage-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  86 */       tickDamage = tickDamage > 0 ? tickDamage : 0;
/*  87 */       int range = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 1.0D, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  89 */       range = range > 1 ? range : 1;
/*  90 */       int mana = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 1, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  92 */       mana = mana > 0 ? mana : 0;
/*  93 */       long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  95 */       hero.addEffect(new IcyAuraEffect(this, period, duration, tickDamage, range, mana));
/*     */     }
/*  97 */     return SkillResult.NORMAL;
/*     */   }
/*     */   public class IcyAuraEffect extends PeriodicEffect { private int tickDamage;
/*     */     private int range;
/*     */     private int mana;
/* 105 */     private boolean firstTime = true;
/*     */     private final long duration;
/*     */ 
/* 109 */     public IcyAuraEffect(SkillRockyAura skill, long period, long duration, int tickDamage, int range, int manaLoss) { super(skill,"RockyAura", period);
/* 110 */       this.tickDamage = tickDamage;
/* 111 */       this.range = range;
/* 112 */       this.mana = manaLoss;
/* 113 */       this.duration = duration;
/* 114 */       this.types.add(EffectType.DISPELLABLE);
/* 115 */       this.types.add(EffectType.BENEFICIAL);
/* 116 */       this.types.add(EffectType.SLOW);
/*     */     }
/*     */ 
/*     */     public void applyToHero(Hero hero)
/*     */     {
/* 121 */       this.firstTime = true;
/* 122 */       super.applyToHero(hero);
/* 123 */       Player player = hero.getPlayer();
/* 124 */       broadcast(player.getLocation(), SkillRockyAura.this.applyText, new Object[] { player.getDisplayName(), "RockyAura" });
/*     */     }
/*     */ 
/*     */     public void removeFromHero(Hero hero)
/*     */     {
/* 129 */       super.removeFromHero(hero);
/* 130 */       Player player = hero.getPlayer();
/* 131 */       broadcast(player.getLocation(), SkillRockyAura.this.expireText, new Object[] { player.getDisplayName(), "RockyAura" });
/*     */     }
/*     */ 
/*     */     public void tickHero(Hero hero)
/*     */     {
/* 136 */       super.tickHero(hero);
/* 137 */       Player player = hero.getPlayer();
/*     */ 
/* 139 */       for (Entity entity : player.getNearbyEntities(this.range, this.range, this.range)) {
/* 140 */         if ((entity instanceof LivingEntity)) {
/* 141 */           LivingEntity lEntity = (LivingEntity)entity;
/*     */ 
/* 143 */           Skill.damageEntity(lEntity, player, this.tickDamage, DamageCause.MAGIC);
/*     */         }
/*     */       }
/* 146 */       Location le = player.getLocation();
/* 147 */       for (int i = 0; i < 9; i++) {
/* 148 */         le.getWorld().playEffect(le, Effect.STEP_SOUND, 7, i);
/* 149 */         le.getWorld().playSound(le, Sound.ZOMBIE_METAL, 1.0F, i);
/*     */       }
/* 151 */       for (int i = 0; i < 9; i++) {
/* 152 */         le.getWorld().playEffect(le, Effect.STEP_SOUND, 7, i);
/*     */       }
/* 154 */       for (int i = 0; i < 9; i++) {
/* 155 */         le.getWorld().playEffect(le, Effect.STEP_SOUND, 7, i);
/*     */       }
/* 157 */       if ((this.mana > 0) && (!this.firstTime)) {
/* 158 */         if (hero.getMana() - this.mana < 0)
/* 159 */           hero.setMana(0);
/*     */         else
/* 161 */           hero.setMana(hero.getMana() - this.mana);
/*     */       }
/* 163 */       else if (this.firstTime) {
/* 164 */         this.firstTime = false;
/*     */       }
/* 166 */       if (hero.getMana() < this.mana)
/* 167 */         hero.removeEffect(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillRockyAura.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillRockyAura
 * JD-Core Version:    0.6.2
 */
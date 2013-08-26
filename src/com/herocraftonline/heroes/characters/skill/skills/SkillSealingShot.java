/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.Monster;
/*     */ import com.herocraftonline.heroes.characters.effects.common.ImbueEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Arrow;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ import org.bukkit.event.entity.EntityShootBowEvent;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class SkillSealingShot extends ActiveSkill
/*     */ {
/*     */   public SkillSealingShot(Heroes plugin)
/*     */   {
/*  28 */     super(plugin, "SealingShot");
/*  29 */     setDescription("You shoot a arrow that will seal the player from using skills, also does percentage damage! Best if used in PVP");
/*  30 */     setUsage("/skill SealingShot");
/*  31 */     setArgumentRange(0, 0);
/*  32 */     setIdentifiers(new String[] { "skill SealingShot" });
/*  33 */     setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.BUFF });
/*     */ 
/*  35 */     Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  41 */     ConfigurationSection node = super.getDefaultConfig();
/*  42 */     node.set(SkillSetting.AMOUNT.node(), Double.valueOf(0.2D));
/*  43 */     node.set("amount-increase", Double.valueOf(0.0D));
/*  44 */     node.set("hst-amount", Integer.valueOf(0));
/*  45 */     node.set(SkillSetting.CHANCE.node(), Double.valueOf(0.1D));
/*  46 */     node.set(SkillSetting.CHANCE_LEVEL.node(), Double.valueOf(0.0D));
/*  47 */     node.set("hst-chance", Integer.valueOf(0));
/*  48 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(1000));
/*  49 */     node.set(SkillSetting.DURATION_INCREASE.node(), Integer.valueOf(0));
/*  50 */     node.set("hst-duration", Integer.valueOf(0));
/*  51 */     return node;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/*  57 */     hero.addEffect(new SealingShotBuff(this));
/*  58 */     broadcastExecuteText(hero);
/*  59 */     return SkillResult.NORMAL;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  65 */     return getDescription();
/*     */   }
/*     */ 
/*     */   public class SkillEntityListener
/*     */     implements Listener
/*     */   {
/*     */     private final Skill skill;
/*     */ 
/*     */     public SkillEntityListener(Skill skill)
/*     */     {
/*  80 */       this.skill = skill;
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.HIGHEST)
/*     */     public void onEntityDamage(WeaponDamageEvent event) {
/*  85 */       if ((!event.isCancelled()) && (event.getCause().equals(DamageCause.PROJECTILE)) && 
/*  86 */         ((event.getDamager() instanceof Hero))) {
/*  87 */         Hero hero = (Hero)event.getDamager();
/*  88 */         float amount = (float)(SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.AMOUNT.node(), 0.2D, false) + SkillConfigManager.getUseSetting(hero, this.skill, "amount-increase", 0.0D, false) * hero.getSkillLevel(this.skill));
/*     */ 
/*  90 */         amount = amount > 0.0F ? amount : 0.0F;
/*  91 */         float chance = (float)(SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.CHANCE.node(), 0.1D, false) + SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.CHANCE_LEVEL.node(), 0.0D, false) * hero.getSkillLevel(this.skill));
/*     */ 
/*  93 */         chance = chance > 0.0F ? chance : 0.0F;
/*  94 */         if (hero.hasEffect("SealingShotBuff")) {
/*  95 */           if ((event.getEntity() instanceof LivingEntity)) {
/*  96 */             int duration = SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.DURATION.node(), 1000, false) + SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.DURATION_INCREASE.node(), 0, false) * hero.getSkillLevel(this.skill);
/*     */ 
/*  98 */             if ((event.getEntity() instanceof Player)) {
/*  99 */               Hero thero = SkillSealingShot.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
/* 100 */               if (Math.random() < chance) {
/* 101 */                 thero.addEffect(new SilenceEffect(this.skill, duration));
/* 102 */                 event.setDamage(thero.getPlayer().getMaxHealth() * amount * 2.0F > event.getDamage() ? (int)(thero.getPlayer().getMaxHealth() * amount * 2.0F) : event.getDamage());
/*     */               }
/*     */               else {
/* 105 */                 event.setDamage(thero.getPlayer().getMaxHealth() * amount > event.getDamage() ? (int)(thero.getPlayer().getMaxHealth() * amount) : event.getDamage());
/*     */               }
/*     */ 
/*     */             }
/* 109 */             else if (Math.random() < chance) {
/* 110 */               event.setDamage((((LivingEntity)event.getEntity()).getMaxHealth() * amount * 2.0F) > event.getDamage() / 5 ? (((LivingEntity)event.getEntity()).getMaxHealth() * amount * 2.0F) : event.getDamage() / 5);
/*     */             }
/*     */             else
/*     */             {
/* 114 */               event.setDamage((((LivingEntity)event.getEntity()).getMaxHealth() * amount) > event.getDamage() / 5 ? (((LivingEntity)event.getEntity()).getMaxHealth() * amount) : event.getDamage() / 5);
/*     */             }
/*     */ 
/* 118 */             event.setDamage(event.getDamage() / 5 > 1 ? event.getDamage() / 5 : 1);
/*     */           }
/* 120 */           hero.removeEffect(hero.getEffect("SealingShotBuff"));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onEntityShootBow(EntityShootBowEvent event)
/*     */     {
/* 128 */       if ((event.isCancelled()) || (!(event.getEntity() instanceof Player)) || (!(event.getProjectile() instanceof Arrow))) return;
/* 129 */       Hero hero = SkillSealingShot.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
/* 130 */       if (hero.hasEffect("SealingShotBuff"))
/* 131 */         event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(5));
/*     */     }
/*     */   }
/*     */ 
/*     */   public class SealingShotBuff extends ImbueEffect
/*     */   {
/*     */     public SealingShotBuff(Skill skill)
/*     */     {
/*  72 */       super(skill, "SealingShotBuff");
/*  73 */       setDescription("SealingShot");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillSealingShot.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillSealingShot
 * JD-Core Version:    0.6.2
 */
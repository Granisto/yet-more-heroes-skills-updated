/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.DebugTimer;
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.EffectType;
/*     */ import com.herocraftonline.heroes.characters.effects.common.ImbueEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Arrow;
/*     */ import org.bukkit.entity.Creature;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ import org.bukkit.event.entity.EntityShootBowEvent;
/*     */ import org.bukkit.event.entity.ProjectileHitEvent;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class SkillLightningArrow extends ActiveSkill
/*     */ {
/*     */   public SkillLightningArrow(Heroes plugin)
/*     */   {
/*  28 */     super(plugin, "LightningArrow");
/*  29 */     setDescription("You imbue your arrows with lightning!");
/*  30 */     setUsage("/skill lightningarrow");
/*  31 */     setArgumentRange(0, 0);
/*  32 */     setIdentifiers(new String[] { "skill lightningarrow", "skill larrow" });
/*  33 */     setTypes(new SkillType[] { SkillType.LIGHTNING, SkillType.BUFF });
/*     */ 
/*  35 */     Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  41 */     ConfigurationSection node = super.getDefaultConfig();
/*  42 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(5));
/*  43 */     node.set("block-dmg", Integer.valueOf(0));
/*  44 */     node.set("mana-per-shot", Integer.valueOf(1));
/*  45 */     node.set("radius", Integer.valueOf(5));
/*  46 */     return node;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/*  52 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 60000, false);
/*  53 */     int numAttacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
/*  54 */     hero.addEffect(new LightningArrowBuff(this, duration, numAttacks));
/*  55 */     broadcastExecuteText(hero);
/*  56 */     return SkillResult.NORMAL;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  62 */     return getDescription();
/*     */   }
/*     */ 
/*     */   public class SkillEntityListener
/*     */     implements Listener
/*     */   {
/*     */     private final Skill skill;
/*     */ 
/*     */     public SkillEntityListener(Skill skill)
/*     */     {
/*  79 */       this.skill = skill;
/*     */     }
/*     */ 
/*     */     @EventHandler
/*     */     public void onProjectileHit(ProjectileHitEvent projectile) {
/*  84 */       Heroes.debug.startTask("HeroesSkillListener");
/*     */ 
/*  86 */       if (!(projectile.getEntity() instanceof Arrow)) {
/*  87 */         Heroes.debug.stopTask("HeroesSkillListener");
/*  88 */         return;
/*     */       }
/*     */ 
/*  91 */       Arrow arrow = (Arrow)projectile.getEntity();
/*     */ 
/*  93 */       if (!(arrow.getShooter() instanceof Player)) {
/*  94 */         Heroes.debug.stopTask("HeroesSkillListener");
/*  95 */         return;
/*     */       }
/*     */ 
/*  98 */       Player player = (Player)arrow.getShooter();
/*  99 */       Hero hero = SkillLightningArrow.this.plugin.getCharacterManager().getHero(player);
/* 100 */       if (!hero.hasEffect("LightningArrowBuff")) {
/* 101 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 102 */         return;
/*     */       }
/*     */ 
/* 105 */       int radius = (int)Math.pow(SkillConfigManager.getUseSetting(hero, this.skill, "radius", 5, false), 2.0D);
/*     */ 
/* 107 */       float damage = SkillConfigManager.getUseSetting(hero, this.skill, "DAMAGE", 5, false);
/* 108 */       float blockdamage = damage;
/* 109 */       int block_dmg = SkillConfigManager.getUseSetting(hero, this.skill, "block-dmg", 0, false);
/*     */ 
/* 111 */       if (block_dmg == 0)
/*     */       {
/* 113 */         blockdamage = 0.0F;
/*     */ 
/* 115 */         for (Entity t_entity : player.getWorld().getEntities()) {
/* 116 */           if ((t_entity instanceof Player)) {
/* 117 */             Player heroes = (Player)t_entity;
/* 118 */             if ((!heroes.equals(player)) && (heroes.getLocation().distanceSquared(arrow.getLocation()) <= radius))
/*     */             {
/* 120 */               Skill.damageEntity(heroes, player, (int)damage, DamageCause.LIGHTNING);
/*     */             }
/* 122 */           } else if ((t_entity instanceof Creature)) {
/* 123 */             Creature mob = (Creature)t_entity;
/* 124 */             if (t_entity.getLocation().distanceSquared(arrow.getLocation()) <= radius) {
/* 125 */               Skill.damageEntity(mob, player, (int)damage, DamageCause.LIGHTNING);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 133 */       arrow.getWorld().strikeLightningEffect(arrow.getLocation());
/*     */ 
/* 135 */       Heroes.debug.stopTask("HeroesSkillListener");
/*     */     }
/*     */ 
/*     */     @EventHandler
/*     */     public void onEntityDamage(EntityDamageEvent event) {
/* 140 */       Heroes.debug.startTask("HeroesSkillListener");
/* 141 */       if ((event.isCancelled()) || (!(event instanceof EntityDamageByEntityEvent))) {
/* 142 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 143 */         return;
/*     */       }
/*     */ 
/* 146 */       Entity projectile = ((EntityDamageByEntityEvent)event).getDamager();
/* 147 */       if ((!(projectile instanceof Arrow)) || (!(((Projectile)projectile).getShooter() instanceof Player))) {
/* 148 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 149 */         return;
/*     */       }
/*     */ 
/* 152 */       Player player = (Player)((Projectile)projectile).getShooter();
/* 153 */       Hero hero = SkillLightningArrow.this.plugin.getCharacterManager().getHero(player);
/* 154 */       if (!hero.hasEffect("LightningArrowBuff")) {
/* 155 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 156 */         return;
/*     */       }
/*     */ 
/* 159 */       int radius = (int)Math.pow(SkillConfigManager.getUseSetting(hero, this.skill, "radius", 5, false), 2.0D);
/*     */ 
/* 161 */       float damage = SkillConfigManager.getUseSetting(hero, this.skill, "DAMAGE", 5, false);
/* 162 */       float blockdamage = damage;
/* 163 */       int block_dmg = SkillConfigManager.getUseSetting(hero, this.skill, "block-dmg", 0, false);
/*     */ 
/* 165 */       if (block_dmg == 0)
/*     */       {
/* 167 */         blockdamage = 0.0F;
/*     */ 
/* 169 */         for (Entity t_entity : player.getWorld().getEntities()) {
/* 170 */           if ((t_entity instanceof Player)) {
/* 171 */             Player heroes = (Player)t_entity;
/* 172 */             if (heroes.getLocation().distanceSquared(projectile.getLocation()) <= radius)
/* 173 */               SkillLightningArrow.damageEntity(heroes, player, (int)damage, DamageCause.LIGHTNING);
/*     */           }
/* 175 */           else if ((t_entity instanceof Creature)) {
/* 176 */             Creature mob = (Creature)t_entity;
/* 177 */             if (t_entity.getLocation().distanceSquared(projectile.getLocation()) <= radius) {
/* 178 */               SkillLightningArrow.damageEntity(mob, player, (int)damage, DamageCause.LIGHTNING);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 186 */       projectile.getWorld().strikeLightningEffect(projectile.getLocation());
/*     */ 
/* 188 */       Heroes.debug.stopTask("HeroesSkillListener");
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onEntityShootBow(EntityShootBowEvent event) {
/* 193 */       if ((event.isCancelled()) || (!(event.getEntity() instanceof Player)) || (!(event.getProjectile() instanceof Arrow))) {
/* 194 */         return;
/*     */       }
/* 196 */       Hero hero = SkillLightningArrow.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
/* 197 */       if (hero.hasEffect("LightningBuff")) {
/* 198 */         int mana = SkillConfigManager.getUseSetting(hero, this.skill, "mana-per-shot", 1, true);
/* 199 */         if (hero.getMana() < mana)
/* 200 */           hero.removeEffect(hero.getEffect("LightningArrowBuff"));
/*     */         else
/* 202 */           hero.setMana(hero.getMana() - mana);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public class LightningArrowBuff extends ImbueEffect
/*     */   {
/*     */     public LightningArrowBuff(Skill skill, long duration, int numAttacks)
/*     */     {
/*  69 */       super(skill,"LightningArrowBuff");
/*  70 */       this.types.add(EffectType.FIRE);
/*  71 */       setDescription("lightning");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillLightningArrow.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillLightningArrow
 * JD-Core Version:    0.6.2
 */
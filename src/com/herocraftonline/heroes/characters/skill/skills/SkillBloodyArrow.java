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
/*     */ import org.bukkit.Effect;
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
/*     */ public class SkillBloodyArrow extends ActiveSkill
/*     */ {
/*     */   public SkillBloodyArrow(Heroes plugin)
/*     */   {
/*  29 */     super(plugin, "BloodyArrow");
/*  30 */     setDescription("Your arrows hurl blood when you shoot!");
/*  31 */     setUsage("/skill bloodyarrow");
/*  32 */     setArgumentRange(0, 0);
/*  33 */     setIdentifiers(new String[] { "skill bloodyarrow", "skill barrow" });
/*  34 */     setTypes(new SkillType[] { SkillType.PHYSICAL, SkillType.BUFF });
/*     */ 
/*  36 */     Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  42 */     ConfigurationSection node = super.getDefaultConfig();
/*  43 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(5));
/*  44 */     node.set("block-dmg", Integer.valueOf(0));
/*  45 */     node.set("mana-per-shot", Integer.valueOf(1));
/*  46 */     node.set("radius", Integer.valueOf(5));
/*  47 */     return node;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/*  53 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 60000, false);
/*  54 */     int numAttacks = SkillConfigManager.getUseSetting(hero, this, "attacks", 1, false);
/*  55 */     hero.addEffect(new BloodyArrowBuff(this, duration, numAttacks));
/*  56 */     broadcastExecuteText(hero);
/*  57 */     return SkillResult.NORMAL;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  63 */     return getDescription();
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
/*     */     @EventHandler
/*     */     public void onProjectileHit(ProjectileHitEvent projectile) {
/*  85 */       Heroes.debug.startTask("HeroesSkillListener");
/*     */ 
/*  87 */       if (!(projectile.getEntity() instanceof Arrow)) {
/*  88 */         Heroes.debug.stopTask("HeroesSkillListener");
/*  89 */         return;
/*     */       }
/*     */ 
/*  92 */       Arrow arrow = (Arrow)projectile.getEntity();
/*     */ 
/*  94 */       if (!(arrow.getShooter() instanceof Player)) {
/*  95 */         Heroes.debug.stopTask("HeroesSkillListener");
/*  96 */         return;
/*     */       }
/*     */ 
/*  99 */       Player player = (Player)arrow.getShooter();
/* 100 */       Hero hero = SkillBloodyArrow.this.plugin.getCharacterManager().getHero(player);
/* 101 */       if (!hero.hasEffect("BloodyArrowBuff")) {
/* 102 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 103 */         return;
/*     */       }
/*     */ 
/* 106 */       int radius = (int)Math.pow(SkillConfigManager.getUseSetting(hero, this.skill, "radius", 5, false), 2.0D);
/*     */ 
/* 108 */       float damage = SkillConfigManager.getUseSetting(hero, this.skill, "DAMAGE", 5, false);
/* 109 */       float blockdamage = damage;
/* 110 */       int block_dmg = SkillConfigManager.getUseSetting(hero, this.skill, "block-dmg", 0, false);
/*     */ 
/* 112 */       if (block_dmg == 0)
/*     */       {
/* 114 */         blockdamage = 0.0F;
/*     */ 
/* 116 */         for (Entity t_entity : player.getWorld().getEntities()) {
/* 117 */           if ((t_entity instanceof Player)) {
/* 118 */             Player heroes = (Player)t_entity;
/* 119 */             if ((!heroes.equals(player)) && (heroes.getLocation().distanceSquared(arrow.getLocation()) <= radius))
/*     */             {
/* 121 */               Skill.damageEntity(heroes, player, (int)damage, EntityDamageEvent.DamageCause.POISON);
/*     */             }
/* 123 */           } else if ((t_entity instanceof Creature)) {
/* 124 */             Creature mob = (Creature)t_entity;
/* 125 */             if (t_entity.getLocation().distanceSquared(arrow.getLocation()) <= radius) {
/* 126 */               Skill.damageEntity(mob, player, (int)damage, EntityDamageEvent.DamageCause.POISON);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 134 */       arrow.getWorld().playEffect(arrow.getLocation(), Effect.POTION_BREAK, 16453);
/*     */ 
/* 136 */       Heroes.debug.stopTask("HeroesSkillListener");
/*     */     }
/*     */ 
/*     */     @EventHandler
/*     */     public void onEntityDamage(EntityDamageEvent event) {
/* 141 */       Heroes.debug.startTask("HeroesSkillListener");
/* 142 */       if ((event.isCancelled()) || (!(event instanceof EntityDamageByEntityEvent))) {
/* 143 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 144 */         return;
/*     */       }
/*     */ 
/* 147 */       Entity projectile = ((EntityDamageByEntityEvent)event).getDamager();
/* 148 */       if ((!(projectile instanceof Arrow)) || (!(((Projectile)projectile).getShooter() instanceof Player))) {
/* 149 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 150 */         return;
/*     */       }
/*     */ 
/* 153 */       Player player = (Player)((Projectile)projectile).getShooter();
/* 154 */       Hero hero = SkillBloodyArrow.this.plugin.getCharacterManager().getHero(player);
/* 155 */       if (!hero.hasEffect("BloodyArrowBuff")) {
/* 156 */         Heroes.debug.stopTask("HeroesSkillListener");
/* 157 */         return;
/*     */       }
/*     */ 
/* 160 */       int radius = (int)Math.pow(SkillConfigManager.getUseSetting(hero, this.skill, "radius", 5, false), 2.0D);
/*     */ 
/* 162 */       float damage = SkillConfigManager.getUseSetting(hero, this.skill, "DAMAGE", 5, false);
/* 163 */       float blockdamage = damage;
/* 164 */       int block_dmg = SkillConfigManager.getUseSetting(hero, this.skill, "block-dmg", 0, false);
/*     */ 
/* 166 */       if (block_dmg == 0)
/*     */       {
/* 168 */         blockdamage = 0.0F;
/*     */ 
/* 170 */         for (Entity t_entity : player.getWorld().getEntities()) {
/* 171 */           if ((t_entity instanceof Player)) {
/* 172 */             Player heroes = (Player)t_entity;
/* 173 */             if (heroes.getLocation().distanceSquared(projectile.getLocation()) <= radius)
/* 174 */               SkillBloodyArrow.damageEntity(heroes, player, (int)damage, EntityDamageEvent.DamageCause.POISON);
/*     */           }
/* 176 */           else if ((t_entity instanceof Creature)) {
/* 177 */             Creature mob = (Creature)t_entity;
/* 178 */             if (t_entity.getLocation().distanceSquared(projectile.getLocation()) <= radius) {
/* 179 */               SkillBloodyArrow.damageEntity(mob, player, (int)damage, EntityDamageEvent.DamageCause.POISON);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 187 */       projectile.getWorld().playEffect(projectile.getLocation(), Effect.POTION_BREAK, 16453);
/*     */ 
/* 189 */       Heroes.debug.stopTask("HeroesSkillListener");
/*     */     }
/*     */ 
/*     */     @EventHandler(priority=EventPriority.MONITOR)
/*     */     public void onEntityShootBow(EntityShootBowEvent event) {
/* 194 */       if ((event.isCancelled()) || (!(event.getEntity() instanceof Player)) || (!(event.getProjectile() instanceof Arrow))) {
/* 195 */         return;
/*     */       }
/* 197 */       Hero hero = SkillBloodyArrow.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
/* 198 */       if (hero.hasEffect("BloodyArrowBuff")) {
/* 199 */         int mana = SkillConfigManager.getUseSetting(hero, this.skill, "mana-per-shot", 1, true);
/* 200 */         if (hero.getMana() < mana)
/* 201 */           hero.removeEffect(hero.getEffect("BloodyArrowBuff"));
/*     */         else
/* 203 */           hero.setMana(hero.getMana() - mana);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public class BloodyArrowBuff extends ImbueEffect
/*     */   {
/*     */     public BloodyArrowBuff(Skill skill, long duration, int numAttacks)
/*     */     {
/*  70 */       super("BloodyArrowBuff");
/*  71 */       this.types.add(EffectType.PHYSICAL);
/*  72 */       setDescription("blood");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillBloodyArrow.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillBloodyArrow
 * JD-Core Version:    0.6.2
 */
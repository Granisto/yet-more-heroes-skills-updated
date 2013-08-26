/*    */ package com.herocraftonline.heroes.characters.skill.skills;
/*    */ 
/*    */ import com.herocraftonline.heroes.Heroes;
/*    */ import com.herocraftonline.heroes.api.SkillResult;
/*    */ import com.herocraftonline.heroes.characters.CharacterManager;
/*    */ import com.herocraftonline.heroes.characters.Hero;
/*    */ import com.herocraftonline.heroes.characters.effects.common.ImbueEffect;
/*    */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*    */ import com.herocraftonline.heroes.characters.skill.Skill;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.entity.Arrow;
/*    */ import org.bukkit.entity.Entity;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.EntityShootBowEvent;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ import org.bukkit.util.Vector;
/*    */ 
/*    */ public class SkillPreciseShot extends ActiveSkill
/*    */ {
/*    */   public SkillPreciseShot(Heroes plugin)
/*    */   {
/* 23 */     super(plugin, "PreciseShot");
/* 24 */     setDescription("Your precise instinct kicks in and your arrow will do 50% more damage!");
/* 25 */     setUsage("/skill PreciseShot");
/* 26 */     setArgumentRange(0, 0);
/* 27 */     setIdentifiers(new String[] { "skill PreciseShot" });
/* 28 */     setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.BUFF });
/*    */ 
/* 30 */     Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
/*    */   }
/*    */ 
/*    */   public ConfigurationSection getDefaultConfig()
/*    */   {
/* 36 */     ConfigurationSection node = super.getDefaultConfig();
/* 37 */     node.set("multiplier", Double.valueOf(1.5D));
/* 38 */     node.set("multiplier-increase", Double.valueOf(0.0D));
/* 39 */     return node;
/*    */   }
/*    */ 
/*    */   public SkillResult use(Hero hero, String[] args)
/*    */   {
/* 45 */     hero.addEffect(new SuperChargeBuff(this));
/* 46 */     broadcastExecuteText(hero);
/* 47 */     return SkillResult.NORMAL;
/*    */   }
/*    */ 
/*    */   public String getDescription(Hero hero)
/*    */   {
/* 53 */     return getDescription();
/*    */   }
/*    */ 
/*    */   public class SkillEntityListener
/*    */     implements Listener
/*    */   {
/*    */     private final Skill skill;
/*    */ 
/*    */     public SkillEntityListener(Skill skill)
/*    */     {
/* 68 */       this.skill = skill;
/*    */     }
/*    */ 
/*    */     @EventHandler(priority=EventPriority.MONITOR)
/*    */     public void onEntityShootBow(EntityShootBowEvent event) {
/* 73 */       if ((event.isCancelled()) || (!(event.getEntity() instanceof Player)) || (!(event.getProjectile() instanceof Arrow))) {
/* 74 */         return;
/*    */       }
/* 76 */       Hero hero = SkillPreciseShot.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
/* 77 */       if (hero.hasEffect("SuperChargeBuff")) {
/* 78 */         float multiplier = (float)(SkillConfigManager.getUseSetting(hero, this.skill, "multiplier", 1.5D, false) + SkillConfigManager.getUseSetting(hero, this.skill, "multiplier-increase", 0.0D, false) * hero.getSkillLevel(this.skill));
/*    */ 
/* 80 */         multiplier = multiplier > 0.0F ? multiplier : 0.0F;
/* 81 */         event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(multiplier));
/* 82 */         hero.removeEffect(hero.getEffect("SuperChargeBuff"));
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public class SuperChargeBuff extends ImbueEffect
/*    */   {
/*    */     public SuperChargeBuff(Skill skill)
/*    */     {
/* 60 */       super(skill,"SuperChargeBuff");
/* 61 */       setDescription("PreciseShot");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillPreciseShot.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillPreciseShot
 * JD-Core Version:    0.6.2
 */
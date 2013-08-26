/*    */ package com.herocraftonline.heroes.characters.skill.skills;
/*    */ 
/*    */ import com.herocraftonline.heroes.Heroes;
/*    */ import com.herocraftonline.heroes.characters.CharacterManager;
/*    */ import com.herocraftonline.heroes.characters.Hero;
/*    */ import com.herocraftonline.heroes.characters.skill.PassiveSkill;
/*    */ import com.herocraftonline.heroes.characters.skill.Skill;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.EntityDamageEvent;
/*    */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ 
/*    */ public class SkillConditioning extends PassiveSkill
/*    */ {
/*    */   private Skill conditioning;
/*    */ 
/*    */   public SkillConditioning(Heroes plugin)
/*    */   {
/* 24 */     super(plugin, "Conditioning");
/* 25 */     setDescription("Passive $1% reduction of all physical damage.");
/* 26 */     setTypes(new SkillType[] { SkillType.COUNTER, SkillType.BUFF });
/*    */ 
/* 28 */     Bukkit.getServer().getPluginManager().registerEvents(new SkillHeroListener(), plugin);
/*    */   }
/*    */ 
/*    */   public String getDescription(Hero hero)
/*    */   {
/* 34 */     int level = hero.getSkillLevel(this);
/* 35 */     double amount = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT.node(), 0.25D, false) + SkillConfigManager.getUseSetting(hero, this, "amount-increase", 0.0D, false) * level) * 100.0D;
/*    */ 
/* 37 */     amount = amount > 0.0D ? amount : 0.0D;
/* 38 */     String description = getDescription().replace("$1", amount + "");
/*    */ 
/* 40 */     return description;
/*    */   }
/*    */ 
/*    */   public ConfigurationSection getDefaultConfig()
/*    */   {
/* 45 */     ConfigurationSection node = super.getDefaultConfig();
/* 46 */     node.set(SkillSetting.AMOUNT.node(), Double.valueOf(0.2D));
/* 47 */     node.set("amount-increase", Double.valueOf(0.0D));
/* 48 */     return node;
/*    */   }
/*    */ 
/*    */   public void init()
/*    */   {
/* 53 */     super.init();
/* 54 */     this.conditioning = this;
/*    */   }
/*    */   public class SkillHeroListener implements Listener {
/*    */     public SkillHeroListener() {
/*    */     }
/* 60 */     @EventHandler
/*    */     public void onEntityDamage(EntityDamageEvent event) { if (((event.isCancelled()) || (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) || (!(event.getEntity() instanceof Player)) || (event.getDamage() < 1)) && 
/* 61 */         ((event.isCancelled()) || (event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) || (!(event.getEntity() instanceof Player)) || (event.getDamage() < 1)) && (
/* 62 */         (event.isCancelled()) || (event.getCause() != EntityDamageEvent.DamageCause.THORNS) || (!(event.getEntity() instanceof Player)) || (event.getDamage() < 1))) {
/* 63 */         return;
/*    */       }
/* 65 */       Player player = (Player)event.getEntity();
/* 66 */       Hero hero = SkillConditioning.this.plugin.getCharacterManager().getHero(player);
/* 67 */       if (!hero.hasEffect("Conditioning")) {
/* 68 */         return;
/*    */       }
/* 70 */       double amount = SkillConfigManager.getUseSetting(hero, SkillConditioning.this.conditioning, SkillSetting.AMOUNT.node(), 0.25D, false) + SkillConfigManager.getUseSetting(hero, SkillConditioning.this.conditioning, "amount-increase", 0.0D, false) * hero.getSkillLevel(SkillConditioning.this.conditioning);
/*    */ 
/* 72 */       amount = amount > 0.0D ? amount : 0.0D;
/* 73 */       event.setDamage((int)(event.getDamage() * (1.0D - amount)));
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillConditioning.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillConditioning
 * JD-Core Version:    0.6.2
 */
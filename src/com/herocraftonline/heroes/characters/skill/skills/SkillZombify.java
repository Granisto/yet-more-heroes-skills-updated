/*    */ package com.herocraftonline.heroes.characters.skill.skills;
/*    */ 
/*    */ import com.herocraftonline.heroes.Heroes;
/*    */ import com.herocraftonline.heroes.api.SkillResult;
/*    */ import com.herocraftonline.heroes.characters.CharacterManager;
/*    */ import com.herocraftonline.heroes.characters.Hero;
/*    */ import com.herocraftonline.heroes.characters.effects.common.HungerEffect;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*    */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*    */ import com.herocraftonline.heroes.characters.skill.TargettedSkill;
/*    */ import com.herocraftonline.heroes.util.Util;
/*    */ import org.bukkit.configuration.ConfigurationSection;
/*    */ import org.bukkit.entity.LivingEntity;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*    */ import org.bukkit.util.Vector;
/*    */ 
/*    */ public class SkillZombify extends TargettedSkill
/*    */ {
/*    */   private String useText;
/*    */ 
/*    */   public SkillZombify(Heroes plugin)
/*    */   {
/* 23 */     super(plugin, "Zombify");
/* 24 */     setDescription("You zombify the player, making their hunger to go down very quickly!");
/* 25 */     setUsage("/skill zombify <target>");
/* 26 */     setArgumentRange(0, 1);
/* 27 */     setIdentifiers(new String[] { "skill zombify" });
/* 28 */     setTypes(new SkillType[] { SkillType.HARMFUL, SkillType.DARK, SkillType.DAMAGING });
/*    */   }
/*    */ 
/*    */   public String getDescription(Hero hero) {
/* 32 */     StringBuilder descr = new StringBuilder(getDescription());
/*    */ 
/* 34 */     double silenceSec = SkillConfigManager.getUseSetting(hero, this, "duration", 3000, false) / 1000.0D;
/* 35 */     if (silenceSec > 0.0D) {
/* 36 */       descr.append(" Zombify Duration: ");
/* 37 */       descr.append(Util.formatDouble(silenceSec));
/* 38 */       descr.append("s");
/*    */     }
/*    */ 
/* 41 */     double cdSec = SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN, 15000, false) / 1000.0D;
/* 42 */     if (cdSec > 0.0D) {
/* 43 */       descr.append(" CD:");
/* 44 */       descr.append(Util.formatDouble(cdSec));
/* 45 */       descr.append("s");
/*    */     }
/*    */ 
/* 48 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA, 20, false);
/* 49 */     if (mana > 0) {
/* 50 */       descr.append(" M:");
/* 51 */       descr.append(mana);
/*    */     }
/*    */ 
/* 54 */     return descr.toString();
/*    */   }
/*    */ 
/*    */   public ConfigurationSection getDefaultConfig() {
/* 58 */     ConfigurationSection defaultConfig = super.getDefaultConfig();
/* 59 */     defaultConfig.set(SkillSetting.USE_TEXT.node(), "%player% turned %target% into an Undead!");
/* 60 */     defaultConfig.set(SkillSetting.COOLDOWN.node(), Integer.valueOf(15000));
/* 61 */     defaultConfig.set(SkillSetting.MANA.node(), Integer.valueOf(20));
/* 62 */     defaultConfig.set(SkillSetting.MAX_DISTANCE.node(), Integer.valueOf(5));
/* 63 */     defaultConfig.set(SkillSetting.DAMAGE.node(), Integer.valueOf(1));
/* 64 */     defaultConfig.set("duration", Integer.valueOf(3000));
/* 65 */     return defaultConfig;
/*    */   }
/*    */ 
/*    */   public void init()
/*    */   {
/* 70 */     super.init();
/* 71 */     this.useText = SkillConfigManager.getRaw(this, SkillSetting.USE_TEXT, "%player% turned %target% into an Undead!").replace("%player%", "$1").replace("%target%", "$2");
/*    */   }
/*    */ 
/*    */   public SkillResult use(Hero hero, LivingEntity target, String[] args)
/*    */   {
/* 76 */     target.setVelocity(new Vector(Math.random() * 0.4D - 0.2D, 0.8D, Math.random() * 0.4D - 0.2D));
/*    */ 
/* 78 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 1, false);
/* 79 */     damageEntity(target, hero.getPlayer(), damage, DamageCause.MAGIC, false);
/*    */ 
/* 81 */     if ((target instanceof Player)) {
/* 82 */       Player targetPlayer = (Player)target;
/* 83 */       Hero targetHero = this.plugin.getCharacterManager().getHero(targetPlayer);
/*    */ 
/* 85 */       int slowDuration = SkillConfigManager.getUseSetting(hero, this, "duration", 3000, false);
/* 86 */       if (slowDuration > 0) {
/* 87 */         targetHero.addEffect(new HungerEffect(this, slowDuration, "", ""));
/*    */       }
/*    */     }
/*    */ 
/* 91 */     broadcast(target.getLocation(), this.useText, new Object[] { hero.getName() });
/* 92 */     return SkillResult.NORMAL;
/*    */   }
/*    */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillZombify.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillZombify
 * JD-Core Version:    0.6.2
 */
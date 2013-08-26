/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.StunEffect;
/*     */ import com.herocraftonline.heroes.characters.party.HeroParty;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Creature;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ 
/*     */ public class SkillCracklingThunder extends ActiveSkill
/*     */ {
/*     */   public SkillCracklingThunder(Heroes plugin)
/*     */   {
/*  21 */     super(plugin, "CracklingThunder");
/*  22 */     setDescription("You shake the ground with thunder causing $3 magic damage + stuns everyone for $2s within $1 blocks of you.");
/*  23 */     setUsage("/skill cracklingthunder");
/*  24 */     setArgumentRange(0, 0);
/*  25 */     setIdentifiers(new String[] { "skill cracklingthunder" });
/*  26 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  31 */     ConfigurationSection node = super.getDefaultConfig();
/*  32 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  33 */     node.set("duration-increase", Integer.valueOf(0));
/*  34 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  35 */     node.set("radius-increase", Integer.valueOf(0));
/*  36 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  37 */     node.set("damage-increase", Integer.valueOf(0));
/*  38 */     return node;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  43 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  45 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  47 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  49 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  52 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  54 */     if (cooldown > 0) {
/*  55 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  59 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  61 */     if (mana > 0) {
/*  62 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  66 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  68 */     if (healthCost > 0) {
/*  69 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  73 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  75 */     if (staminaCost > 0) {
/*  76 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  80 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  81 */     if (delay > 0) {
/*  82 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  86 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  87 */     if (exp > 0) {
/*  88 */       description = description + " XP:" + exp;
/*     */     }
/*  90 */     return description;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/*  95 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  97 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  99 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 101 */     Player player = hero.getPlayer();
/* 102 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 103 */       if ((e instanceof Creature)) {
/* 104 */         Creature c = (Creature)e;
/* 105 */         damageEntity(c, player, damage, DamageCause.MAGIC);
/*     */       }
/* 107 */       else if ((e instanceof Player)) {
/* 108 */         Player p = (Player)e;
/* 109 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 112 */           if (damageCheck(player, p)) {
/* 113 */             damageEntity(p, player, damage, DamageCause.MAGIC);
/*     */ 
/* 115 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 116 */             tHero.addEffect(new StunEffect(this, duration));
/*     */           }
/*     */         }
/*     */       }
/* 120 */     player.getWorld().strikeLightningEffect(player.getLocation());
/* 121 */     broadcastExecuteText(hero);
/* 122 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillCracklingThunder.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillCracklingThunder
 * JD-Core Version:    0.6.2
 */
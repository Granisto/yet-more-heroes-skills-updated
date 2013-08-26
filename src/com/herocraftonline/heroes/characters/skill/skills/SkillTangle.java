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
/*     */ import org.bukkit.Effect;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Creature;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*     */ 
/*     */ public class SkillTangle extends ActiveSkill
/*     */ {
/*     */   public SkillTangle(Heroes plugin)
/*     */   {
/*  22 */     super(plugin, "Tangle");
/*  23 */     setDescription("You tangle your targets that are $1 blocks of you, dealing $3 physical damage + $2s stun.");
/*  24 */     setUsage("/skill tangle");
/*  25 */     setArgumentRange(0, 0);
/*  26 */     setIdentifiers(new String[] { "skill tangle" });
/*  27 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  32 */     ConfigurationSection node = super.getDefaultConfig();
/*  33 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  34 */     node.set("duration-increase", Integer.valueOf(0));
/*  35 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  36 */     node.set("radius-increase", Integer.valueOf(0));
/*  37 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  38 */     node.set("damage-increase", Integer.valueOf(0));
/*  39 */     return node;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  44 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  46 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  48 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  50 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  53 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  55 */     if (cooldown > 0) {
/*  56 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  60 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  62 */     if (mana > 0) {
/*  63 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  67 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  69 */     if (healthCost > 0) {
/*  70 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  74 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  76 */     if (staminaCost > 0) {
/*  77 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  81 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  82 */     if (delay > 0) {
/*  83 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  87 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  88 */     if (exp > 0) {
/*  89 */       description = description + " XP:" + exp;
/*     */     }
/*  91 */     return description;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/*  96 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  98 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 100 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 102 */     Player player = hero.getPlayer();
/* 103 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 104 */       if ((e instanceof Creature)) {
/* 105 */         Creature c = (Creature)e;
/* 106 */         damageEntity(c, player, damage, DamageCause.ENTITY_ATTACK);
/*     */       }
/* 108 */       else if ((e instanceof Player)) {
/* 109 */         Player p = (Player)e;
/* 110 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 113 */           if (damageCheck(player, p)) {
/* 114 */             damageEntity(p, player, damage, DamageCause.ENTITY_ATTACK);
/*     */ 
/* 116 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 117 */             tHero.addEffect(new StunEffect(this, duration));
/*     */           }
/*     */         }
/*     */       }
/* 121 */     player.getWorld().playSound(player.getLocation(), Sound.BURP, 1.0F, 0.0F);
/* 122 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 0.0F);
/* 123 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 1.0F);
/* 124 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
/* 125 */     broadcastExecuteText(hero);
/* 126 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillTangle.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillTangle
 * JD-Core Version:    0.6.2
 */
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
/*     */ public class SkillZephyr extends ActiveSkill
/*     */ {
/*     */   public SkillZephyr(Heroes plugin)
/*     */   {
/*  29 */     super(plugin, "Zephyr");
/*  30 */     setDescription("You put your entire soul into one single blow to strike down your enimes dealing $3 damage and chains everyone within $1 blocks of you for $2 seconds.");
/*  31 */     setUsage("/skill zephyr");
/*  32 */     setArgumentRange(0, 0);
/*  33 */     setIdentifiers(new String[] { "skill zephyr" });
/*  34 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL, SkillType.DEBUFF });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  39 */     ConfigurationSection node = super.getDefaultConfig();
/*  40 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  41 */     node.set("duration-increase", Integer.valueOf(0));
/*  42 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  43 */     node.set("radius-increase", Integer.valueOf(0));
/*  44 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  45 */     node.set("damage-increase", Integer.valueOf(0));
/*  46 */     return node;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  51 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  53 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  55 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  57 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  60 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  62 */     if (cooldown > 0) {
/*  63 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  67 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  69 */     if (mana > 0) {
/*  70 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  74 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  76 */     if (healthCost > 0) {
/*  77 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  81 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  83 */     if (staminaCost > 0) {
/*  84 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  88 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  89 */     if (delay > 0) {
/*  90 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  94 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  95 */     if (exp > 0) {
/*  96 */       description = description + " XP:" + exp;
/*     */     }
/*  98 */     return description;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 103 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 105 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 107 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 109 */     Player player = hero.getPlayer();
/* 110 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 111 */       if ((e instanceof Creature)) {
/* 112 */         Creature c = (Creature)e;
/* 113 */         damageEntity(c, player, damage, DamageCause.MAGIC);
/*     */       }
/* 115 */       else if ((e instanceof Player)) {
/* 116 */         Player p = (Player)e;
/* 117 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 120 */           if (damageCheck(player, p)) {
/* 121 */             damageEntity(p, player, damage, DamageCause.MAGIC);
/*     */ 
/* 123 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 124 */             tHero.addEffect(new StunEffect(this, duration));
/*     */           }
/*     */         }
/*     */       }
/* 128 */     player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1.0F, 0.0F);
/* 129 */     player.getWorld().playEffect(player.getLocation(), Effect.POTION_BREAK, 16386);
/* 130 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 155);
/* 131 */     broadcastExecuteText(hero);
/* 132 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillZephyr.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillZephyr
 * JD-Core Version:    0.6.2
 */
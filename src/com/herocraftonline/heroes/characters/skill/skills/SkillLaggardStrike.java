/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
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
/*     */ public class SkillLaggardStrike extends ActiveSkill
/*     */ {
/*     */   private SkillLaggardStrike blind;
/*     */ 
/*     */   public SkillLaggardStrike(Heroes plugin)
/*     */   {
/*  32 */     super(plugin, "LaggardStrike");
/*  33 */     setDescription("Your weapon has the power of slowness and people with a radius of $1 blocks gets damaged for $3 physical damage + players will get slowed for $2 seconds.");
/*  34 */     setUsage("/skill laggardstrike, or lstrike");
/*  35 */     setArgumentRange(0, 0);
/*  36 */     setIdentifiers(new String[] { "skill laggardstrike", "skill lstrike" });
/*  37 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL, SkillType.DEBUFF, SkillType.SILENCABLE });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  42 */     ConfigurationSection node = super.getDefaultConfig();
/*  43 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  44 */     node.set("duration-increase", Integer.valueOf(0));
/*  45 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  46 */     node.set("radius-increase", Integer.valueOf(0));
/*  47 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  48 */     node.set("damage-increase", Integer.valueOf(0));
/*  49 */     return node;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  54 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  56 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  58 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  60 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  63 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  65 */     if (cooldown > 0) {
/*  66 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  70 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  72 */     if (mana > 0) {
/*  73 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  77 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  79 */     if (healthCost > 0) {
/*  80 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  84 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  86 */     if (staminaCost > 0) {
/*  87 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  91 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  92 */     if (delay > 0) {
/*  93 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  97 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  98 */     if (exp > 0) {
/*  99 */       description = description + " XP:" + exp;
/*     */     }
/* 101 */     return description;
/*     */   }
/*     */ 
/*     */   public void init() {
/* 105 */     super.init();
/* 106 */     this.blind = this;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args) {
/* 110 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 112 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 114 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 116 */     Player player = hero.getPlayer();
/* 117 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 118 */       if ((e instanceof Creature)) {
/* 119 */         Creature c = (Creature)e;
/* 120 */         damageEntity(c, player, damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/*     */       }
/* 122 */       else if ((e instanceof Player)) {
/* 123 */         Player p = (Player)e;
/* 124 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 127 */           if (damageCheck(player, p)) {
/* 128 */             damageEntity(p, player, damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/*     */ 
/* 130 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 131 */             tHero.addEffect(new SlowEffect(this.blind, duration, 2, false, "", "", hero));
/*     */           }
/*     */         }
/*     */       }
/* 135 */     player.getWorld().playSound(player.getLocation(), Sound.BLAZE_DEATH, 1.0F, 0.0F);
/* 136 */     player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 1.0F, 0.0F);
/* 137 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 51);
/* 138 */     broadcastExecuteText(hero);
/* 139 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillLaggardStrike.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillLaggardStrike
 * JD-Core Version:    0.6.2
 */
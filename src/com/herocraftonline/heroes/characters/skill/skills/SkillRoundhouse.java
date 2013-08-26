/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.NauseaEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
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
/*     */ public class SkillRoundhouse extends ActiveSkill
/*     */ {
/*     */   private SkillRoundhouse slow;
/*     */ 
/*     */   public SkillRoundhouse(Heroes plugin)
/*     */   {
/*  25 */     super(plugin, "Roundhouse");
/*  26 */     setDescription("You Roundhouse kick enemies within $1 blocks of you, deals $3 damage + $2s blindness, nausea, and silence.");
/*  27 */     setUsage("/skill roundhouse");
/*  28 */     setArgumentRange(0, 0);
/*  29 */     setIdentifiers(new String[] { "skill roundhouse" });
/*  30 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  35 */     ConfigurationSection node = super.getDefaultConfig();
/*  36 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  37 */     node.set("duration-increase", Integer.valueOf(0));
/*  38 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  39 */     node.set("radius-increase", Integer.valueOf(0));
/*  40 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  41 */     node.set("damage-increase", Integer.valueOf(0));
/*  42 */     return node;
/*     */   }
/*     */ 
/*     */   public void init() {
/*  46 */     super.init();
/*  47 */     this.slow = this;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  52 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  54 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  56 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  58 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  61 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  63 */     if (cooldown > 0) {
/*  64 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  68 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  70 */     if (mana > 0) {
/*  71 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  75 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  77 */     if (healthCost > 0) {
/*  78 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  82 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  84 */     if (staminaCost > 0) {
/*  85 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  89 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  90 */     if (delay > 0) {
/*  91 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  95 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  96 */     if (exp > 0) {
/*  97 */       description = description + " XP:" + exp;
/*     */     }
/*  99 */     return description;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 104 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 106 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 108 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 110 */     Player player = hero.getPlayer();
/* 111 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 112 */       if ((e instanceof Creature)) {
/* 113 */         Creature c = (Creature)e;
/* 114 */         damageEntity(c, player, damage, DamageCause.ENTITY_ATTACK);
/*     */       }
/* 116 */       else if ((e instanceof Player)) {
/* 117 */         Player p = (Player)e;
/* 118 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 121 */           if (damageCheck(player, p)) {
/* 122 */             damageEntity(p, player, damage, DamageCause.ENTITY_ATTACK);
/*     */ 
/* 124 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 125 */             tHero.addEffect(new SilenceEffect(this, duration));
/* 126 */             tHero.addEffect(new NauseaEffect(this, duration, "", ""));
/* 127 */             tHero.addEffect(new SlowEffect(this.slow, duration, 6, false, "", "", hero));
/*     */           }
/*     */         }
/*     */       }
/* 131 */     player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0F, 1.0F);
/* 132 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 0.0F);
/* 133 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 1.0F);
/* 134 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
/* 135 */     broadcast(player.getLocation(), "$1: You think you are so tough eh? Well take this! ROUNDHOUSE KICK!", new Object[] { player.getDisplayName() });
/* 136 */     broadcastExecuteText(hero);
/* 137 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillRoundhouse.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillRoundhouse
 * JD-Core Version:    0.6.2
 */
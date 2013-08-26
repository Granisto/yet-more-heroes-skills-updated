/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.common.BlindEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.HungerEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.NauseaEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SilenceEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
/*     */ import com.herocraftonline.heroes.characters.effects.common.WeaknessEffect;
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
/*     */ public class SkillLiverShot extends ActiveSkill
/*     */ {
/*     */   private SkillLiverShot slow;
/*     */ 
/*     */   public SkillLiverShot(Heroes plugin)
/*     */   {
/*  28 */     super(plugin, "LiverShot");
/*  29 */     setDescription("Puncturing everyones liver with you feet within $1 blocks of you, deals $3 damage + $2s blindness, nausea, silence, weakness, and hunger.");
/*  30 */     setUsage("/skill livershot");
/*  31 */     setArgumentRange(0, 0);
/*  32 */     setIdentifiers(new String[] { "skill livershot" });
/*  33 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  38 */     ConfigurationSection node = super.getDefaultConfig();
/*  39 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(10000));
/*  40 */     node.set("duration-increase", Integer.valueOf(0));
/*  41 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  42 */     node.set("radius-increase", Integer.valueOf(0));
/*  43 */     node.set(SkillSetting.DAMAGE.node(), Integer.valueOf(0));
/*  44 */     node.set("damage-increase", Integer.valueOf(0));
/*  45 */     return node;
/*     */   }
/*     */ 
/*     */   public void init() {
/*  49 */     super.init();
/*  50 */     this.slow = this;
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  55 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  57 */     int duration = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  59 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  61 */     String description = getDescription().replace("$1", radius + "").replace("$2", duration + "").replace("$3", damage + "");
/*     */ 
/*  64 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  66 */     if (cooldown > 0) {
/*  67 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  71 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  73 */     if (mana > 0) {
/*  74 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  78 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  80 */     if (healthCost > 0) {
/*  81 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  85 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  87 */     if (staminaCost > 0) {
/*  88 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  92 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  93 */     if (delay > 0) {
/*  94 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  98 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  99 */     if (exp > 0) {
/* 100 */       description = description + " XP:" + exp;
/*     */     }
/* 102 */     return description;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 107 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 109 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 111 */     long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 10000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 113 */     Player player = hero.getPlayer();
/* 114 */     for (Entity e : player.getNearbyEntities(radius, radius, radius))
/* 115 */       if ((e instanceof Creature)) {
/* 116 */         Creature c = (Creature)e;
/* 117 */         damageEntity(c, player, damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/*     */       }
/* 119 */       else if ((e instanceof Player)) {
/* 120 */         Player p = (Player)e;
/* 121 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 124 */           if (damageCheck(player, p)) {
/* 125 */             damageEntity(p, player, damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
/*     */ 
/* 127 */             Hero tHero = this.plugin.getCharacterManager().getHero(p);
/* 128 */             tHero.addEffect(new SilenceEffect(this, duration));
/* 129 */             tHero.addEffect(new NauseaEffect(this, duration, "", ""));
/* 130 */             tHero.addEffect(new SlowEffect(this.slow, duration, 6, false, "", "", hero));
/* 131 */             tHero.addEffect(new HungerEffect(this, duration, "", ""));
/* 132 */             tHero.addEffect(new BlindEffect(this, duration, "", ""));
/* 133 */             tHero.addEffect(new WeaknessEffect(this, duration, "", ""));
/*     */           }
/*     */         }
/*     */       }
/* 137 */     player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
/* 138 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 0.0F);
/* 139 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 1.0F);
/* 140 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
/* 141 */     broadcast(player.getLocation(), "$1: I've had enough of your bullcrap! I WILL MAKE YOUR LIVER SHED BLOOD!", new Object[] { player.getDisplayName() });
/* 142 */     broadcastExecuteText(hero);
/* 143 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillLiverShot.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillLiverShot
 * JD-Core Version:    0.6.2
 */
/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.CharacterManager;
/*     */ import com.herocraftonline.heroes.characters.Hero;
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
/*     */ public class SkillHBomb extends ActiveSkill
/*     */ {
/*     */   private SkillHBomb slow;
/*     */ 
/*     */   public SkillHBomb(Heroes plugin)
/*     */   {
/*  27 */     super(plugin, "H-Bomb");
/*  28 */     setDescription("You have had enough of the fighting and decide to end it with your finisher... You raise your fist into the air dealing $3 damage to people within $1 blocks of you + Interupts a skill.");
/*  29 */     setUsage("/skill h-bomb or /skill hbomb");
/*  30 */     setArgumentRange(0, 0);
/*  31 */     setIdentifiers(new String[] { "skill h-bomb", "skill hbomb" });
/*  32 */     setTypes(new SkillType[] { SkillType.MOVEMENT, SkillType.PHYSICAL, SkillType.INTERRUPT });
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  37 */     ConfigurationSection node = super.getDefaultConfig();
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
/*  54 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  56 */     String description = getDescription().replace("$1", radius + "").replace("$3", damage + "");
/*     */ 
/*  59 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  61 */     if (cooldown > 0) {
/*  62 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  66 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  68 */     if (mana > 0) {
/*  69 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  73 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  75 */     if (healthCost > 0) {
/*  76 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  80 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  82 */     if (staminaCost > 0) {
/*  83 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  87 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  88 */     if (delay > 0) {
/*  89 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  93 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  94 */     if (exp > 0) {
/*  95 */       description = description + " XP:" + exp;
/*     */     }
/*  97 */     return description;
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 102 */     int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 30, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 104 */     int damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false) + SkillConfigManager.getUseSetting(hero, this, "damage-increase", 0, false) * hero.getSkillLevel(this);
/*     */ 
/* 106 */     Player player = hero.getPlayer();
/* 107 */     for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
/* 108 */       if ((e instanceof Creature)) {
/* 109 */         Creature c = (Creature)e;
/* 110 */         damageEntity(c, player, damage, DamageCause.ENTITY_ATTACK);
/*     */       }
/* 112 */       else if ((e instanceof Player)) {
/* 113 */         Player p = (Player)e;
/* 114 */         if ((!hero.hasParty()) || (!hero.getParty().isPartyMember(this.plugin.getCharacterManager().getHero(p))))
/*     */         {
/* 117 */           if (damageCheck(player, p))
/* 118 */             damageEntity(p, player, damage, DamageCause.ENTITY_ATTACK);
/*     */         }
/*     */       }
/*     */     }
/* 122 */     player.getWorld().playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 0.0F);
/* 123 */     player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 0.0F);
/* 124 */     player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0F, 1.0F);
/* 125 */     player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 0.0F);
/* 126 */     player.getWorld().createExplosion(player.getLocation(), 0.0F, false);
/* 127 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 0.0F);
/* 128 */     player.getWorld().playSound(player.getLocation(), Sound.HURT, 1.0F, 1.0F);
/* 129 */     player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
/* 130 */     broadcast(player.getLocation(), "$1: I have... had... ENOUGH!", new Object[] { player.getDisplayName() });
/* 131 */     broadcastExecuteText(hero);
/* 132 */     return SkillResult.NORMAL;
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillHBomb.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillHBomb
 * JD-Core Version:    0.6.2
 */
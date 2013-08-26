/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.effects.EffectType;
/*     */ import com.herocraftonline.heroes.characters.effects.PeriodicHealEffect;
/*     */ import com.herocraftonline.heroes.characters.party.HeroParty;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import com.herocraftonline.heroes.util.Messaging;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class SkillChakraCharge extends ActiveSkill
/*     */ {
/*     */   private String applyText;
/*     */   private String expireText;
/*     */ 
/*     */   public SkillChakraCharge(Heroes plugin)
/*     */   {
/*  23 */     super(plugin, "ChakraCharge");
/*  24 */     setDescription("You charge you and your party members Mana that is around $3 block and gain $1 mana $4 times.");
/*  25 */     setUsage("/skill chakracharge");
/*  26 */     setArgumentRange(0, 0);
/*  27 */     setIdentifiers(new String[] { "skill ChakraCharge" });
/*     */ 
/*  29 */     setTypes(new SkillType[] { SkillType.BUFF, SkillType.HEAL, SkillType.MANA });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  34 */     int manaTick = (int)(SkillConfigManager.getUseSetting(hero, this, "mana-tick", 4, false) + SkillConfigManager.getUseSetting(hero, this, "mana-tick-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  36 */     manaTick = manaTick > 0 ? manaTick : 0;
/*  37 */     int healthTick = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_TICK.node(), 2, false) + SkillConfigManager.getUseSetting(hero, this, "health-tick-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  39 */     healthTick = healthTick > 0 ? healthTick : 0;
/*  40 */     int radius = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 10, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  42 */     radius = radius > 0 ? radius : 0;
/*  43 */     long duration = ()(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 12000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/*  45 */     duration = duration > 0L ? duration : 0L;
/*  46 */     long period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD.node(), 3000, false);
/*  47 */     int ticks = (int)(duration / period);
/*  48 */     String description = getDescription().replace("$1", manaTick + "").replace("$2", healthTick + "").replace("$3", radius + "").replace("$4", ticks + "");
/*     */ 
/*  51 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  53 */     if (cooldown > 0) {
/*  54 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  58 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  60 */     if (mana > 0) {
/*  61 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  65 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  67 */     if (healthCost > 0) {
/*  68 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  72 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  74 */     if (staminaCost > 0) {
/*  75 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  79 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  80 */     if (delay > 0) {
/*  81 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  85 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  86 */     if (exp > 0) {
/*  87 */       description = description + " XP:" + exp;
/*     */     }
/*  89 */     return description;
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  94 */     ConfigurationSection node = super.getDefaultConfig();
/*  95 */     node.set("mana-tick", Integer.valueOf(4));
/*  96 */     node.set("mana-tick-increase", Integer.valueOf(0));
/*  97 */     node.set(SkillSetting.RADIUS.node(), Integer.valueOf(10));
/*  98 */     node.set("radius-increase", Integer.valueOf(0));
/*  99 */     node.set(SkillSetting.DURATION.node(), Integer.valueOf(12000));
/* 100 */     node.set("duration-increase", Integer.valueOf(0));
/* 101 */     node.set(SkillSetting.HEALTH_TICK.node(), Integer.valueOf(2));
/* 102 */     node.set("health-tick-increase", Integer.valueOf(0));
/* 103 */     node.set(SkillSetting.PERIOD.node(), Integer.valueOf(3000));
/* 104 */     return node;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 109 */     super.init();
/* 110 */     this.applyText = SkillConfigManager.getUseSetting(null, this, SkillSetting.APPLY_TEXT.node(), "Your mana increases from the Chakra!");
/* 111 */     this.expireText = SkillConfigManager.getUseSetting(null, this, SkillSetting.EXPIRE_TEXT.node(), "You no longer feel anymore Chakra!");
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args)
/*     */   {
/* 116 */     Player player = hero.getPlayer();
/* 117 */     long duration = ()(SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 12000, false) + SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 119 */     duration = duration > 0L ? duration : 0L;
/* 120 */     int manaTick = (int)(SkillConfigManager.getUseSetting(hero, this, "mana-tick", 4, false) + SkillConfigManager.getUseSetting(hero, this, "mana-tick-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 122 */     manaTick = manaTick > 0 ? manaTick : 0;
/* 123 */     int healthTick = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_TICK.node(), 2, false) + SkillConfigManager.getUseSetting(hero, this, "health-tick-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 125 */     healthTick = healthTick > 0 ? healthTick : 0;
/* 126 */     long period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD.node(), 3000, false);
/*     */ 
/* 128 */     WisdomEffect mEffect = new WisdomEffect(this, period, duration, healthTick, player, manaTick);
/*     */     int rangeSquared;
/* 129 */     if (!hero.hasParty()) {
/* 130 */       hero.addEffect(mEffect);
/*     */     } else {
/* 132 */       int radius = (int)(SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 10, false) + SkillConfigManager.getUseSetting(hero, this, "radius-increase", 0.0D, false) * hero.getSkillLevel(this));
/*     */ 
/* 134 */       radius = radius > 0 ? radius : 0;
/* 135 */       rangeSquared = (int)Math.pow(radius, 2.0D);
/* 136 */       for (Hero pHero : hero.getParty().getMembers()) {
/* 137 */         Player pPlayer = pHero.getPlayer();
/* 138 */         if ((pPlayer.getWorld().equals(player.getWorld())) && 
/* 141 */           (pPlayer.getLocation().distanceSquared(player.getLocation()) <= rangeSquared))
/*     */         {
/* 144 */           pHero.addEffect(mEffect);
/*     */         }
/*     */       }
/*     */     }
/* 148 */     broadcastExecuteText(hero);
/* 149 */     return SkillResult.NORMAL;
/*     */   }
/*     */   public class WisdomEffect extends PeriodicHealEffect {
/*     */     private final int amount;
/*     */     private final int manaMultiplier;
/*     */ 
/*     */     public WisdomEffect(Skill skill, long period, long duration, int amount, Player applier, int manaMultiplier) {
/* 157 */       super("Vitalize", period, duration, amount, applier);
/* 158 */       this.manaMultiplier = manaMultiplier;
/* 159 */       this.amount = amount;
/* 160 */       this.types.add(EffectType.DISPELLABLE);
/* 161 */       this.types.add(EffectType.BENEFICIAL);
/* 162 */       this.types.add(EffectType.HEAL);
/*     */     }
/*     */ 
/*     */     public void applyToHero(Hero hero)
/*     */     {
/* 167 */       super.applyToHero(hero);
/* 168 */       Player player = hero.getPlayer();
/* 169 */       Messaging.send(player, SkillChakraCharge.this.applyText, new Object[0]);
/*     */     }
/*     */ 
/*     */     public void tickHero(Hero hero)
/*     */     {
/* 174 */       super.tickHero(hero);
/* 175 */       HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(hero, this.amount, this.skill);
/* 176 */       this.plugin.getServer().getPluginManager().callEvent(hrhEvent);
/* 177 */       int addMana = hero.getMana() + this.manaMultiplier > hero.getMaxMana() ? hero.getMaxMana() - hero.getMana() : this.manaMultiplier;
/* 178 */       hero.setMana(addMana + hero.getMana());
/*     */     }
/*     */ 
/*     */     public void removeFromHero(Hero hero)
/*     */     {
/* 183 */       super.removeFromHero(hero);
/* 184 */       Player player = hero.getPlayer();
/* 185 */       Messaging.send(player, SkillChakraCharge.this.expireText, new Object[0]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillChakraCharge.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillChakraCharge
 * JD-Core Version:    0.6.2
 */
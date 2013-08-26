/*     */ package com.herocraftonline.heroes.characters.skill.skills;
/*     */ 
/*     */ import com.herocraftonline.heroes.Heroes;
/*     */ import com.herocraftonline.heroes.api.SkillResult;
/*     */ import com.herocraftonline.heroes.characters.Hero;
/*     */ import com.herocraftonline.heroes.characters.Monster;
/*     */ import com.herocraftonline.heroes.characters.effects.EffectType;
/*     */ import com.herocraftonline.heroes.characters.effects.PeriodicExpirableEffect;
/*     */ import com.herocraftonline.heroes.characters.skill.ActiveSkill;
/*     */ import com.herocraftonline.heroes.characters.skill.Skill;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillSetting;
/*     */ import com.herocraftonline.heroes.characters.skill.SkillType;
/*     */ import com.herocraftonline.heroes.util.Messaging;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockFace;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class SkillWebbingBarrier extends ActiveSkill
/*     */ {
/*     */   public String applyText;
/*     */   public String expireText;
/*     */ 
/*     */   public SkillWebbingBarrier(Heroes plugin)
/*     */   {
/*  29 */     super(plugin, "WebbingBarrier");
/*  30 */     setDescription("You have a barrier of webs around you to trap your enemies and protect yourself from harm! Duration $1-$2s");
/*  31 */     setUsage("/skill WebbingBarrier");
/*  32 */     setArgumentRange(0, 0);
/*  33 */     setIdentifiers(new String[] { "skill WebbingBarrier", "skill wbarrier" });
/*     */ 
/*  35 */     setTypes(new SkillType[] { SkillType.BUFF, SkillType.COUNTER, SkillType.SILENCABLE });
/*     */   }
/*     */ 
/*     */   public String getDescription(Hero hero)
/*     */   {
/*  40 */     long addDur = SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/*  41 */     long minDur = SkillConfigManager.getUseSetting(hero, this, "min_duration", 20000, false) + addDur;
/*  42 */     long maxDur = SkillConfigManager.getUseSetting(hero, this, "max_duration", 40000, false) + addDur;
/*  43 */     String description = getDescription().replace("$1", minDur + "").replace("$2", maxDur + "");
/*     */ 
/*  46 */     int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;
/*     */ 
/*  48 */     if (cooldown > 0) {
/*  49 */       description = description + " CD:" + cooldown + "s";
/*     */     }
/*     */ 
/*  53 */     int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  55 */     if (mana > 0) {
/*  56 */       description = description + " M:" + mana;
/*     */     }
/*     */ 
/*  60 */     int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);
/*     */ 
/*  62 */     if (healthCost > 0) {
/*  63 */       description = description + " HP:" + healthCost;
/*     */     }
/*     */ 
/*  67 */     int staminaCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, SkillSetting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);
/*     */ 
/*  69 */     if (staminaCost > 0) {
/*  70 */       description = description + " FP:" + staminaCost;
/*     */     }
/*     */ 
/*  74 */     int delay = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DELAY.node(), 0, false) / 1000;
/*  75 */     if (delay > 0) {
/*  76 */       description = description + " W:" + delay + "s";
/*     */     }
/*     */ 
/*  80 */     int exp = SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXP.node(), 0, false);
/*  81 */     if (exp > 0) {
/*  82 */       description = description + " XP:" + exp;
/*     */     }
/*  84 */     return description;
/*     */   }
/*     */ 
/*     */   public ConfigurationSection getDefaultConfig()
/*     */   {
/*  89 */     ConfigurationSection node = super.getDefaultConfig();
/*  90 */     node.set("min_duration", Integer.valueOf(20000));
/*  91 */     node.set("max_duration", Integer.valueOf(40000));
/*  92 */     node.set("duration-increase", Integer.valueOf(0));
/*  93 */     node.set("apply-text", "%hero% has used the shadows to create a %skill%!");
/*  94 */     node.set("expire-text", "%hero%s %skill% fades as the shadows dies down!");
/*  95 */     return node;
/*     */   }
/*     */ 
/*     */   public void init()
/*     */   {
/* 100 */     super.init();
/* 101 */     this.applyText = SkillConfigManager.getUseSetting(null, this, "apply-text", "%hero% has used the shadows to create a %skill%!").replace("%hero%", "$1").replace("%skill%", "$2");
/* 102 */     this.expireText = SkillConfigManager.getUseSetting(null, this, "expire-text", "%hero%s %skill% fades as the shadows dies down!").replace("%hero%", "$1").replace("%skill%", "$2");
/*     */   }
/*     */ 
/*     */   public SkillResult use(Hero hero, String[] args) {
/* 106 */     Player player = hero.getPlayer();
/* 107 */     long addDur = SkillConfigManager.getUseSetting(hero, this, "duration-increase", 0, false) * hero.getSkillLevel(this);
/* 108 */     long minDuration = SkillConfigManager.getUseSetting(hero, this, "min_duration", 20000, false) + addDur;
/* 109 */     if ((minDuration < 1000L) || (minDuration > 60000L)) {
/* 110 */       minDuration = 20000L;
/*     */     }
/* 112 */     long maxDuration = SkillConfigManager.getUseSetting(hero, this, "max_duration", 40000, false) + addDur;
/* 113 */     if (maxDuration > 60000L) {
/* 114 */       maxDuration = 40000L;
/*     */     }
/* 116 */     if (maxDuration < minDuration) {
/* 117 */       maxDuration = minDuration;
/*     */     }
/* 119 */     long randDuration = maxDuration - minDuration;
/* 120 */     long duration = (long)(Math.random() * randDuration + minDuration);
/* 121 */     broadcastExecuteText(hero);
/* 122 */     Messaging.send(player, "Duration " + duration / 1000L + "s", new Object[0]);
/* 123 */     WebbingBarrierEffect gEffect = new WebbingBarrierEffect(this, "WebbingBarrier", duration);
/* 124 */     hero.addEffect(gEffect);
/* 125 */     return SkillResult.NORMAL;
/*     */   }
/*     */ 
/*     */   public class WebbingBarrierEffect extends PeriodicExpirableEffect
/*     */   {
/*     */     private boolean seed;
/*     */     private Block prevRefBlock;
/* 131 */     private ArrayList<Block> glassList = new ArrayList();
/*     */ 
/* 133 */     public WebbingBarrierEffect(Skill skill, String name, long duration) { super(skill, name, 200L, duration);
/* 134 */       this.types.add(EffectType.BENEFICIAL);
/* 135 */       this.types.add(EffectType.DISPELLABLE);
/* 136 */       this.types.add(EffectType.ICE);
/* 137 */       this.types.add(EffectType.LIGHT);
/*     */ 
/* 139 */       this.seed = true;
/*     */     }
/*     */ 
/*     */     public void applyToHero(Hero hero)
/*     */     {
/* 144 */       super.applyToHero(hero);
/* 145 */       Player player = hero.getPlayer();
/* 146 */       broadcast(player.getLocation(), SkillWebbingBarrier.this.applyText, new Object[] { player.getDisplayName(), "WebbingBarrier" });
/*     */     }
/*     */ 
/*     */     public void removeFromHero(Hero hero)
/*     */     {
/* 151 */       super.removeFromHero(hero);
/* 152 */       Player player = hero.getPlayer();
/* 153 */       broadcast(player.getLocation(), SkillWebbingBarrier.this.expireText, new Object[] { player.getDisplayName(), "WebbingBarrier" });
/* 154 */       updateShield(null, this.seed);
/*     */     }
/*     */ 
/*     */     public void tickHero(Hero hero)
/*     */     {
/* 159 */       if (!hero.hasEffect("WebbingBarrier")) {
/* 160 */         return;
/*     */       }
/* 162 */       updateShield(hero.getPlayer().getLocation().getBlock(), this.seed);
/* 163 */       this.seed = (!this.seed);
/*     */     }
/*     */ 
/*     */     public void tickMonster(Monster mnstr)
/*     */     {
/*     */     }
/*     */ 
/*     */     private void updateShield(Block refBlock, boolean seed)
/*     */     {
/* 172 */       if (this.prevRefBlock != null)
/*     */       {
/* 174 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 0, -1));
/* 175 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 0, 0));
/* 176 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 0, 1));
/* 177 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 0, 2));
/* 178 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 0, 3));
/*     */ 
/* 180 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 1, -1));
/* 181 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 1, 0));
/* 182 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 1, 1));
/* 183 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 1, 2));
/* 184 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 1, 3));
/*     */ 
/* 186 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 2, -1));
/* 187 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 2, 0));
/* 188 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 2, 1));
/* 189 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 2, 2));
/* 190 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, 2, 3));
/*     */ 
/* 192 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -1, -1));
/* 193 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -1, 0));
/* 194 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -1, 1));
/* 195 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -1, 2));
/* 196 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -1, 3));
/*     */ 
/* 198 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -2, -1));
/* 199 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -2, 0));
/* 200 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -2, 1));
/* 201 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -2, 2));
/* 202 */         replaceGlass(retrieveBlock(this.prevRefBlock, 3, -2, 3));
/*     */ 
/* 205 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 3, -1));
/* 206 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 3, 0));
/* 207 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 3, 1));
/* 208 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 3, 2));
/* 209 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 3, 3));
/*     */ 
/* 211 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 3, -1));
/* 212 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 3, 0));
/* 213 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 3, 1));
/* 214 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 3, 2));
/* 215 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 3, 3));
/*     */ 
/* 217 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 3, -1));
/* 218 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 3, 0));
/* 219 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 3, 1));
/* 220 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 3, 2));
/* 221 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 3, 3));
/*     */ 
/* 223 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 3, -1));
/* 224 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 3, 0));
/* 225 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 3, 1));
/* 226 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 3, 2));
/* 227 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 3, 3));
/*     */ 
/* 229 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 3, -1));
/* 230 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 3, 0));
/* 231 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 3, 1));
/* 232 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 3, 2));
/* 233 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 3, 3));
/*     */ 
/* 236 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -3, -1));
/* 237 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -3, 0));
/* 238 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -3, 1));
/* 239 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -3, 2));
/* 240 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -3, 3));
/*     */ 
/* 242 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -3, -1));
/* 243 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -3, 0));
/* 244 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -3, 1));
/* 245 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -3, 2));
/* 246 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -3, 3));
/*     */ 
/* 248 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -3, -1));
/* 249 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -3, 0));
/* 250 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -3, 1));
/* 251 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -3, 2));
/* 252 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -3, 3));
/*     */ 
/* 254 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -3, -1));
/* 255 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -3, 0));
/* 256 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -3, 1));
/* 257 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -3, 2));
/* 258 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -3, 3));
/*     */ 
/* 260 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -3, -1));
/* 261 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -3, 0));
/* 262 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -3, 1));
/* 263 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -3, 2));
/* 264 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -3, 3));
/*     */ 
/* 267 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 0, -1));
/* 268 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 0, 0));
/* 269 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 0, 1));
/* 270 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 0, 2));
/* 271 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 0, 3));
/*     */ 
/* 273 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 1, -1));
/* 274 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 1, 0));
/* 275 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 1, 1));
/* 276 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 1, 2));
/* 277 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 1, 3));
/*     */ 
/* 279 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 2, -1));
/* 280 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 2, 0));
/* 281 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 2, 1));
/* 282 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 2, 2));
/* 283 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, 2, 3));
/*     */ 
/* 285 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -1, -1));
/* 286 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -1, 0));
/* 287 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -1, 1));
/* 288 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -1, 2));
/* 289 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -1, 3));
/*     */ 
/* 291 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -2, -1));
/* 292 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -2, 0));
/* 293 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -2, 1));
/* 294 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -2, 2));
/* 295 */         replaceGlass(retrieveBlock(this.prevRefBlock, -3, -2, 3));
/*     */ 
/* 298 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 0, 4));
/* 299 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 0, 4));
/* 300 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 0, 4));
/* 301 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 0, 4));
/* 302 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 0, 4));
/*     */ 
/* 304 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 1, 4));
/* 305 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 1, 4));
/* 306 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 1, 4));
/* 307 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 1, 4));
/* 308 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 1, 4));
/*     */ 
/* 310 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 2, 4));
/* 311 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 2, 4));
/* 312 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 2, 4));
/* 313 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 2, 4));
/* 314 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 2, 4));
/*     */ 
/* 316 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -1, 4));
/* 317 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -1, 4));
/* 318 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -1, 4));
/* 319 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -1, 4));
/* 320 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -1, 4));
/*     */ 
/* 322 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -2, 4));
/* 323 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -2, 4));
/* 324 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -2, 4));
/* 325 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -2, 4));
/* 326 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -2, 4));
/*     */ 
/* 329 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 0, -2));
/* 330 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 0, -2));
/* 331 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 0, -2));
/* 332 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 0, -2));
/* 333 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 0, -2));
/*     */ 
/* 335 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 1, -2));
/* 336 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 1, -2));
/* 337 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 1, -2));
/* 338 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 1, -2));
/* 339 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 1, -2));
/*     */ 
/* 341 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, 2, -2));
/* 342 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, 2, -2));
/* 343 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, 2, -2));
/* 344 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, 2, -2));
/* 345 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, 2, -2));
/*     */ 
/* 347 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -1, -2));
/* 348 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -1, -2));
/* 349 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -1, -2));
/* 350 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -1, -2));
/* 351 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -1, -2));
/*     */ 
/* 353 */         replaceGlass(retrieveBlock(this.prevRefBlock, 0, -2, -2));
/* 354 */         replaceGlass(retrieveBlock(this.prevRefBlock, -2, -2, -2));
/* 355 */         replaceGlass(retrieveBlock(this.prevRefBlock, -1, -2, -2));
/* 356 */         replaceGlass(retrieveBlock(this.prevRefBlock, 1, -2, -2));
/* 357 */         replaceGlass(retrieveBlock(this.prevRefBlock, 2, -2, -2));
/* 358 */         fixGlass();
/*     */       }
/* 360 */       if (refBlock != null) {
/* 361 */         if (seed)
/*     */         {
/* 363 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, -1), Material.WEB);
/* 364 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 0), Material.WEB);
/* 365 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 1), Material.WEB);
/* 366 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 2), Material.WEB);
/* 367 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 3), Material.WEB);
/*     */ 
/* 369 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, -1), Material.WEB);
/* 370 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 0), Material.WEB);
/* 371 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 1), Material.WEB);
/* 372 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 2), Material.WEB);
/* 373 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 3), Material.WEB);
/*     */ 
/* 375 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, -1), Material.WEB);
/* 376 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 0), Material.WEB);
/* 377 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 1), Material.WEB);
/* 378 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 2), Material.WEB);
/* 379 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 3), Material.WEB);
/*     */ 
/* 381 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, -1), Material.WEB);
/* 382 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 0), Material.WEB);
/* 383 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 1), Material.WEB);
/* 384 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 2), Material.WEB);
/* 385 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 3), Material.WEB);
/*     */ 
/* 387 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, -1), Material.WEB);
/* 388 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 0), Material.WEB);
/* 389 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 1), Material.WEB);
/* 390 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 2), Material.WEB);
/* 391 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 3), Material.WEB);
/*     */ 
/* 394 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, -1), Material.WEB);
/* 395 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 0), Material.WEB);
/* 396 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 1), Material.WEB);
/* 397 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 2), Material.WEB);
/* 398 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 3), Material.WEB);
/*     */ 
/* 400 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, -1), Material.WEB);
/* 401 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 0), Material.WEB);
/* 402 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 1), Material.WEB);
/* 403 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 2), Material.WEB);
/* 404 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 3), Material.WEB);
/*     */ 
/* 406 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, -1), Material.WEB);
/* 407 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 0), Material.WEB);
/* 408 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 1), Material.WEB);
/* 409 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 2), Material.WEB);
/* 410 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 3), Material.WEB);
/*     */ 
/* 412 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, -1), Material.WEB);
/* 413 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 0), Material.WEB);
/* 414 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 1), Material.WEB);
/* 415 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 2), Material.WEB);
/* 416 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 3), Material.WEB);
/*     */ 
/* 418 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, -1), Material.WEB);
/* 419 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 0), Material.WEB);
/* 420 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 1), Material.WEB);
/* 421 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 2), Material.WEB);
/* 422 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 3), Material.WEB);
/*     */ 
/* 425 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, -1), Material.WEB);
/* 426 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 0), Material.WEB);
/* 427 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 1), Material.WEB);
/* 428 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 2), Material.WEB);
/* 429 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 3), Material.WEB);
/*     */ 
/* 431 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, -1), Material.WEB);
/* 432 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 0), Material.WEB);
/* 433 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 1), Material.WEB);
/* 434 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 2), Material.WEB);
/* 435 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 3), Material.WEB);
/*     */ 
/* 437 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, -1), Material.WEB);
/* 438 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 0), Material.WEB);
/* 439 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 1), Material.WEB);
/* 440 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 2), Material.WEB);
/* 441 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 3), Material.WEB);
/*     */ 
/* 443 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, -1), Material.WEB);
/* 444 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 0), Material.WEB);
/* 445 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 1), Material.WEB);
/* 446 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 2), Material.WEB);
/* 447 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 3), Material.WEB);
/*     */ 
/* 449 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, -1), Material.WEB);
/* 450 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 0), Material.WEB);
/* 451 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 1), Material.WEB);
/* 452 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 2), Material.WEB);
/* 453 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 3), Material.WEB);
/*     */ 
/* 456 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, -1), Material.WEB);
/* 457 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 0), Material.WEB);
/* 458 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 1), Material.WEB);
/* 459 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 2), Material.WEB);
/* 460 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 3), Material.WEB);
/*     */ 
/* 462 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, -1), Material.WEB);
/* 463 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 0), Material.WEB);
/* 464 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 1), Material.WEB);
/* 465 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 2), Material.WEB);
/* 466 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 3), Material.WEB);
/*     */ 
/* 468 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, -1), Material.WEB);
/* 469 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 0), Material.WEB);
/* 470 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 1), Material.WEB);
/* 471 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 2), Material.WEB);
/* 472 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 3), Material.WEB);
/*     */ 
/* 474 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, -1), Material.WEB);
/* 475 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 0), Material.WEB);
/* 476 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 1), Material.WEB);
/* 477 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 2), Material.WEB);
/* 478 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 3), Material.WEB);
/*     */ 
/* 480 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, -1), Material.WEB);
/* 481 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 0), Material.WEB);
/* 482 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 1), Material.WEB);
/* 483 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 2), Material.WEB);
/* 484 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 3), Material.WEB);
/*     */ 
/* 487 */           replaceIfAir(retrieveBlock(refBlock, 0, 0, 4), Material.WEB);
/* 488 */           replaceIfAir(retrieveBlock(refBlock, -1, 0, 4), Material.WEB);
/* 489 */           replaceIfAir(retrieveBlock(refBlock, -2, 0, 4), Material.WEB);
/* 490 */           replaceIfAir(retrieveBlock(refBlock, 1, 0, 4), Material.WEB);
/* 491 */           replaceIfAir(retrieveBlock(refBlock, 2, 0, 4), Material.WEB);
/*     */ 
/* 493 */           replaceIfAir(retrieveBlock(refBlock, 0, 1, 4), Material.WEB);
/* 494 */           replaceIfAir(retrieveBlock(refBlock, 1, 1, 4), Material.WEB);
/* 495 */           replaceIfAir(retrieveBlock(refBlock, 2, 1, 4), Material.WEB);
/* 496 */           replaceIfAir(retrieveBlock(refBlock, -1, 1, 4), Material.WEB);
/* 497 */           replaceIfAir(retrieveBlock(refBlock, -2, 1, 4), Material.WEB);
/*     */ 
/* 500 */           replaceIfAir(retrieveBlock(refBlock, 0, 2, 4), Material.WEB);
/* 501 */           replaceIfAir(retrieveBlock(refBlock, -1, 2, 4), Material.WEB);
/* 502 */           replaceIfAir(retrieveBlock(refBlock, -2, 2, 4), Material.WEB);
/* 503 */           replaceIfAir(retrieveBlock(refBlock, 1, 2, 4), Material.WEB);
/* 504 */           replaceIfAir(retrieveBlock(refBlock, 2, 2, 4), Material.WEB);
/*     */ 
/* 506 */           replaceIfAir(retrieveBlock(refBlock, 0, -1, 4), Material.WEB);
/* 507 */           replaceIfAir(retrieveBlock(refBlock, 1, -1, 4), Material.WEB);
/* 508 */           replaceIfAir(retrieveBlock(refBlock, 2, -1, 4), Material.WEB);
/* 509 */           replaceIfAir(retrieveBlock(refBlock, -1, -1, 4), Material.WEB);
/* 510 */           replaceIfAir(retrieveBlock(refBlock, -2, -1, 4), Material.WEB);
/*     */ 
/* 512 */           replaceIfAir(retrieveBlock(refBlock, 0, -2, 4), Material.WEB);
/* 513 */           replaceIfAir(retrieveBlock(refBlock, -1, -2, 4), Material.WEB);
/* 514 */           replaceIfAir(retrieveBlock(refBlock, -2, -2, 4), Material.WEB);
/* 515 */           replaceIfAir(retrieveBlock(refBlock, 1, -2, 4), Material.WEB);
/* 516 */           replaceIfAir(retrieveBlock(refBlock, 2, -2, 4), Material.WEB);
/*     */ 
/* 519 */           replaceIfAir(retrieveBlock(refBlock, 0, 0, -2), Material.WEB);
/* 520 */           replaceIfAir(retrieveBlock(refBlock, -1, 0, -2), Material.WEB);
/* 521 */           replaceIfAir(retrieveBlock(refBlock, -2, 0, -2), Material.WEB);
/* 522 */           replaceIfAir(retrieveBlock(refBlock, 1, 0, -2), Material.WEB);
/* 523 */           replaceIfAir(retrieveBlock(refBlock, 2, 0, -2), Material.WEB);
/*     */ 
/* 525 */           replaceIfAir(retrieveBlock(refBlock, 0, 1, -2), Material.WEB);
/* 526 */           replaceIfAir(retrieveBlock(refBlock, 1, 1, -2), Material.WEB);
/* 527 */           replaceIfAir(retrieveBlock(refBlock, 2, 1, -2), Material.WEB);
/* 528 */           replaceIfAir(retrieveBlock(refBlock, -1, 1, -2), Material.WEB);
/* 529 */           replaceIfAir(retrieveBlock(refBlock, -2, 1, -2), Material.WEB);
/*     */ 
/* 531 */           replaceIfAir(retrieveBlock(refBlock, 0, 2, -2), Material.WEB);
/* 532 */           replaceIfAir(retrieveBlock(refBlock, -1, 2, -2), Material.WEB);
/* 533 */           replaceIfAir(retrieveBlock(refBlock, -2, 2, -2), Material.WEB);
/* 534 */           replaceIfAir(retrieveBlock(refBlock, 1, 2, -2), Material.WEB);
/* 535 */           replaceIfAir(retrieveBlock(refBlock, 2, 2, -2), Material.WEB);
/*     */ 
/* 537 */           replaceIfAir(retrieveBlock(refBlock, 0, -1, -2), Material.WEB);
/* 538 */           replaceIfAir(retrieveBlock(refBlock, 1, -1, -2), Material.WEB);
/* 539 */           replaceIfAir(retrieveBlock(refBlock, 2, -1, -2), Material.WEB);
/* 540 */           replaceIfAir(retrieveBlock(refBlock, -1, -1, -2), Material.WEB);
/* 541 */           replaceIfAir(retrieveBlock(refBlock, -2, -1, -2), Material.WEB);
/*     */ 
/* 543 */           replaceIfAir(retrieveBlock(refBlock, 0, -2, -2), Material.WEB);
/* 544 */           replaceIfAir(retrieveBlock(refBlock, -1, -2, -2), Material.WEB);
/* 545 */           replaceIfAir(retrieveBlock(refBlock, -2, -2, -2), Material.WEB);
/* 546 */           replaceIfAir(retrieveBlock(refBlock, 1, -2, -2), Material.WEB);
/* 547 */           replaceIfAir(retrieveBlock(refBlock, 2, -2, -2), Material.WEB);
/*     */         }
/*     */         else {
/* 550 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, -1), Material.WEB);
/* 551 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 0), Material.WEB);
/* 552 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 1), Material.WEB);
/* 553 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 2), Material.WEB);
/* 554 */           replaceIfAir(retrieveBlock(refBlock, 3, 0, 3), Material.WEB);
/*     */ 
/* 556 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, -1), Material.WEB);
/* 557 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 0), Material.WEB);
/* 558 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 1), Material.WEB);
/* 559 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 2), Material.WEB);
/* 560 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 3), Material.WEB);
/*     */ 
/* 562 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, -1), Material.WEB);
/* 563 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 0), Material.WEB);
/* 564 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 1), Material.WEB);
/* 565 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 2), Material.WEB);
/* 566 */           replaceIfAir(retrieveBlock(refBlock, 3, 2, 3), Material.WEB);
/*     */ 
/* 568 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, -1), Material.WEB);
/* 569 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 0), Material.WEB);
/* 570 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 1), Material.WEB);
/* 571 */           replaceIfAir(retrieveBlock(refBlock, 3, -1, 2), Material.WEB);
/* 572 */           replaceIfAir(retrieveBlock(refBlock, 3, 1, 3), Material.WEB);
/*     */ 
/* 574 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, -1), Material.WEB);
/* 575 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 0), Material.WEB);
/* 576 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 1), Material.WEB);
/* 577 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 2), Material.WEB);
/* 578 */           replaceIfAir(retrieveBlock(refBlock, 3, -2, 3), Material.WEB);
/*     */ 
/* 581 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, -1), Material.WEB);
/* 582 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 0), Material.WEB);
/* 583 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 1), Material.WEB);
/* 584 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 2), Material.WEB);
/* 585 */           replaceIfAir(retrieveBlock(refBlock, 0, 3, 3), Material.WEB);
/*     */ 
/* 587 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, -1), Material.WEB);
/* 588 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 0), Material.WEB);
/* 589 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 1), Material.WEB);
/* 590 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 2), Material.WEB);
/* 591 */           replaceIfAir(retrieveBlock(refBlock, 1, 3, 3), Material.WEB);
/*     */ 
/* 593 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, -1), Material.WEB);
/* 594 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 0), Material.WEB);
/* 595 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 1), Material.WEB);
/* 596 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 2), Material.WEB);
/* 597 */           replaceIfAir(retrieveBlock(refBlock, 2, 3, 3), Material.WEB);
/*     */ 
/* 599 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, -1), Material.WEB);
/* 600 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 0), Material.WEB);
/* 601 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 1), Material.WEB);
/* 602 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 2), Material.WEB);
/* 603 */           replaceIfAir(retrieveBlock(refBlock, -1, 3, 3), Material.WEB);
/*     */ 
/* 605 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, -1), Material.WEB);
/* 606 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 0), Material.WEB);
/* 607 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 1), Material.WEB);
/* 608 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 2), Material.WEB);
/* 609 */           replaceIfAir(retrieveBlock(refBlock, -2, 3, 3), Material.WEB);
/*     */ 
/* 612 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, -1), Material.WEB);
/* 613 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 0), Material.WEB);
/* 614 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 1), Material.WEB);
/* 615 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 2), Material.WEB);
/* 616 */           replaceIfAir(retrieveBlock(refBlock, 0, -3, 3), Material.WEB);
/*     */ 
/* 618 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, -1), Material.WEB);
/* 619 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 0), Material.WEB);
/* 620 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 1), Material.WEB);
/* 621 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 2), Material.WEB);
/* 622 */           replaceIfAir(retrieveBlock(refBlock, 1, -3, 3), Material.WEB);
/*     */ 
/* 624 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, -1), Material.WEB);
/* 625 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 0), Material.WEB);
/* 626 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 1), Material.WEB);
/* 627 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 2), Material.WEB);
/* 628 */           replaceIfAir(retrieveBlock(refBlock, 2, -3, 3), Material.WEB);
/*     */ 
/* 630 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, -1), Material.WEB);
/* 631 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 0), Material.WEB);
/* 632 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 1), Material.WEB);
/* 633 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 2), Material.WEB);
/* 634 */           replaceIfAir(retrieveBlock(refBlock, -1, -3, 3), Material.WEB);
/*     */ 
/* 636 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, -1), Material.WEB);
/* 637 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 0), Material.WEB);
/* 638 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 1), Material.WEB);
/* 639 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 2), Material.WEB);
/* 640 */           replaceIfAir(retrieveBlock(refBlock, -2, -3, 3), Material.WEB);
/*     */ 
/* 643 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, -1), Material.WEB);
/* 644 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 0), Material.WEB);
/* 645 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 1), Material.WEB);
/* 646 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 2), Material.WEB);
/* 647 */           replaceIfAir(retrieveBlock(refBlock, -3, 0, 3), Material.WEB);
/*     */ 
/* 649 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, -1), Material.WEB);
/* 650 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 0), Material.WEB);
/* 651 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 1), Material.WEB);
/* 652 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 2), Material.WEB);
/* 653 */           replaceIfAir(retrieveBlock(refBlock, -3, 1, 3), Material.WEB);
/*     */ 
/* 655 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, -1), Material.WEB);
/* 656 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 0), Material.WEB);
/* 657 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 1), Material.WEB);
/* 658 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 2), Material.WEB);
/* 659 */           replaceIfAir(retrieveBlock(refBlock, -3, 2, 3), Material.WEB);
/*     */ 
/* 661 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, -1), Material.WEB);
/* 662 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 0), Material.WEB);
/* 663 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 1), Material.WEB);
/* 664 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 2), Material.WEB);
/* 665 */           replaceIfAir(retrieveBlock(refBlock, -3, -1, 3), Material.WEB);
/*     */ 
/* 667 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, -1), Material.WEB);
/* 668 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 0), Material.WEB);
/* 669 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 1), Material.WEB);
/* 670 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 2), Material.WEB);
/* 671 */           replaceIfAir(retrieveBlock(refBlock, -3, -2, 3), Material.WEB);
/*     */ 
/* 674 */           replaceIfAir(retrieveBlock(refBlock, 0, 0, 4), Material.WEB);
/* 675 */           replaceIfAir(retrieveBlock(refBlock, -1, 0, 4), Material.WEB);
/* 676 */           replaceIfAir(retrieveBlock(refBlock, -2, 0, 4), Material.WEB);
/* 677 */           replaceIfAir(retrieveBlock(refBlock, 1, 0, 4), Material.WEB);
/* 678 */           replaceIfAir(retrieveBlock(refBlock, 2, 0, 4), Material.WEB);
/*     */ 
/* 680 */           replaceIfAir(retrieveBlock(refBlock, 0, 1, 4), Material.WEB);
/* 681 */           replaceIfAir(retrieveBlock(refBlock, 1, 1, 4), Material.WEB);
/* 682 */           replaceIfAir(retrieveBlock(refBlock, 2, 1, 4), Material.WEB);
/* 683 */           replaceIfAir(retrieveBlock(refBlock, -1, 1, 4), Material.WEB);
/* 684 */           replaceIfAir(retrieveBlock(refBlock, -2, 1, 4), Material.WEB);
/*     */ 
/* 686 */           replaceIfAir(retrieveBlock(refBlock, 0, 2, 4), Material.WEB);
/* 687 */           replaceIfAir(retrieveBlock(refBlock, -1, 2, 4), Material.WEB);
/* 688 */           replaceIfAir(retrieveBlock(refBlock, -2, 2, 4), Material.WEB);
/* 689 */           replaceIfAir(retrieveBlock(refBlock, 1, 2, 4), Material.WEB);
/* 690 */           replaceIfAir(retrieveBlock(refBlock, 2, 2, 4), Material.WEB);
/*     */ 
/* 692 */           replaceIfAir(retrieveBlock(refBlock, 0, -1, 4), Material.WEB);
/* 693 */           replaceIfAir(retrieveBlock(refBlock, 1, -1, 4), Material.WEB);
/* 694 */           replaceIfAir(retrieveBlock(refBlock, 2, -1, 4), Material.WEB);
/* 695 */           replaceIfAir(retrieveBlock(refBlock, -1, -1, 4), Material.WEB);
/* 696 */           replaceIfAir(retrieveBlock(refBlock, -2, -1, 4), Material.WEB);
/*     */ 
/* 698 */           replaceIfAir(retrieveBlock(refBlock, 0, -2, 4), Material.WEB);
/* 699 */           replaceIfAir(retrieveBlock(refBlock, -1, -2, 4), Material.WEB);
/* 700 */           replaceIfAir(retrieveBlock(refBlock, -2, -2, 4), Material.WEB);
/* 701 */           replaceIfAir(retrieveBlock(refBlock, 1, -2, 4), Material.WEB);
/* 702 */           replaceIfAir(retrieveBlock(refBlock, 2, -2, 4), Material.WEB);
/*     */ 
/* 705 */           replaceIfAir(retrieveBlock(refBlock, 0, 0, -2), Material.WEB);
/* 706 */           replaceIfAir(retrieveBlock(refBlock, -1, 0, -2), Material.WEB);
/* 707 */           replaceIfAir(retrieveBlock(refBlock, -2, 0, -2), Material.WEB);
/* 708 */           replaceIfAir(retrieveBlock(refBlock, 1, 0, -2), Material.WEB);
/* 709 */           replaceIfAir(retrieveBlock(refBlock, 2, 0, -2), Material.WEB);
/*     */ 
/* 711 */           replaceIfAir(retrieveBlock(refBlock, 0, 1, -2), Material.WEB);
/* 712 */           replaceIfAir(retrieveBlock(refBlock, 1, 1, -2), Material.WEB);
/* 713 */           replaceIfAir(retrieveBlock(refBlock, 2, 1, -2), Material.WEB);
/* 714 */           replaceIfAir(retrieveBlock(refBlock, -1, 1, -2), Material.WEB);
/* 715 */           replaceIfAir(retrieveBlock(refBlock, -2, 1, -2), Material.WEB);
/*     */ 
/* 717 */           replaceIfAir(retrieveBlock(refBlock, 0, 2, -2), Material.WEB);
/* 718 */           replaceIfAir(retrieveBlock(refBlock, -1, 2, -2), Material.WEB);
/* 719 */           replaceIfAir(retrieveBlock(refBlock, -2, 2, -2), Material.WEB);
/* 720 */           replaceIfAir(retrieveBlock(refBlock, 1, 2, -2), Material.WEB);
/* 721 */           replaceIfAir(retrieveBlock(refBlock, 2, 2, -2), Material.WEB);
/*     */ 
/* 723 */           replaceIfAir(retrieveBlock(refBlock, 0, -1, -2), Material.WEB);
/* 724 */           replaceIfAir(retrieveBlock(refBlock, 1, -1, -2), Material.WEB);
/* 725 */           replaceIfAir(retrieveBlock(refBlock, 2, -1, -2), Material.WEB);
/* 726 */           replaceIfAir(retrieveBlock(refBlock, -1, -1, -2), Material.WEB);
/* 727 */           replaceIfAir(retrieveBlock(refBlock, -2, -1, -2), Material.WEB);
/*     */ 
/* 729 */           replaceIfAir(retrieveBlock(refBlock, 0, -2, -2), Material.WEB);
/* 730 */           replaceIfAir(retrieveBlock(refBlock, -1, -2, -2), Material.WEB);
/* 731 */           replaceIfAir(retrieveBlock(refBlock, -2, -2, -2), Material.WEB);
/* 732 */           replaceIfAir(retrieveBlock(refBlock, 1, -2, -2), Material.WEB);
/* 733 */           replaceIfAir(retrieveBlock(refBlock, 2, -2, -2), Material.WEB);
/*     */         }
/*     */       }
/* 736 */       this.prevRefBlock = refBlock;
/*     */     }
/*     */ 
/*     */     private void replaceIfAir(Block checkBlock, Material replaceWith)
/*     */     {
/* 746 */       if (checkBlock.getType() == Material.WEB) {
/* 747 */         this.glassList.add(checkBlock);
/*     */       }
/* 749 */       if (checkBlock.getType() == Material.AIR)
/* 750 */         checkBlock.setType(replaceWith);
/*     */     }
/*     */ 
/*     */     private void replaceGlass(Block checkBlock)
/*     */     {
/* 761 */       if (checkBlock.getType() == Material.WEB)
/* 762 */         checkBlock.setType(Material.AIR);
/*     */     }
/*     */ 
/*     */     private void fixGlass()
/*     */     {
/* 773 */       for (int i = 0; i < this.glassList.size(); i++) {
/* 774 */         ((Block)this.glassList.get(i)).setType(Material.AIR);
/*     */       }
/* 776 */       this.glassList.clear();
/*     */     }
/*     */ 
/*     */     private Block retrieveBlock(Block refBlock, int relX, int relY, int relZ) {
/* 780 */       Block returnBlock = refBlock;
/* 781 */       while (0 < Math.abs(relX)) {
/* 782 */         if (relX > 0) {
/* 783 */           returnBlock = returnBlock.getRelative(BlockFace.NORTH);
/* 784 */           relX--;
/*     */         } else {
/* 786 */           returnBlock = returnBlock.getRelative(BlockFace.SOUTH);
/* 787 */           relX++;
/*     */         }
/*     */       }
/* 790 */       while (0 < Math.abs(relY)) {
/* 791 */         if (relY > 0) {
/* 792 */           returnBlock = returnBlock.getRelative(BlockFace.EAST);
/* 793 */           relY--;
/*     */         } else {
/* 795 */           returnBlock = returnBlock.getRelative(BlockFace.WEST);
/* 796 */           relY++;
/*     */         }
/*     */       }
/* 799 */       while (0 < Math.abs(relZ)) {
/* 800 */         if (relZ > 0) {
/* 801 */           returnBlock = returnBlock.getRelative(BlockFace.UP);
/* 802 */           relZ--;
/*     */         } else {
/* 804 */           returnBlock = returnBlock.getRelative(BlockFace.DOWN);
/* 805 */           relZ++;
/*     */         }
/*     */       }
/* 808 */       return returnBlock;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Awesome\Desktop\CODING\Heroes\YMH Skills\SkillWebbingBarrier.jar
 * Qualified Name:     com.herocraftonline.heroes.characters.skill.skills.SkillWebbingBarrier
 * JD-Core Version:    0.6.2
 */
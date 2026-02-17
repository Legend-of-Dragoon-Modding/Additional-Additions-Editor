package additionaladditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import legend.game.additions.Addition;
import legend.game.additions.AdditionHitProperties10;
import legend.game.additions.SimpleAddition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Additional {
  private static final Logger LOGGER = LogManager.getFormatterLogger(Additional.class);

  public final int charId;
  public final RegistryId charRegId;

  public String filename;

  public String name;
  public int baseDamage = 100;
  /** {@link AdditionHitProperties10#overlayStartingFrameOffset_0f} */
  public int overlayFrame;
  public final List<SimpleAddition.LevelMultipliers> levelMultipliers = new ArrayList<>();
  public final List<Hit> hits = new ArrayList<>();

  public Additional(final int charId, final RegistryId charRegId) {
    this.charId = charId;
    this.charRegId = charRegId;
  }

  public void set(final Addition addition) {
    try {
      final Class<SimpleAddition> cls = SimpleAddition.class;
      final Field baseDamageField = cls.getDeclaredField("baseDamage");
      final Field levelMultipliersField = cls.getDeclaredField("levelMultipliers");
      final Field hitsField = cls.getDeclaredField("hits");

      baseDamageField.setAccessible(true);
      levelMultipliersField.setAccessible(true);
      hitsField.setAccessible(true);

      final SimpleAddition.LevelMultipliers[] levelMultipliers = (SimpleAddition.LevelMultipliers[])levelMultipliersField.get(addition);
      final AdditionHitProperties10[] hits = (AdditionHitProperties10[])hitsField.get(addition);

      this.baseDamage = baseDamageField.getInt(addition);
      this.overlayFrame = hits[0].overlayStartingFrameOffset_0f;

      this.levelMultipliers.clear();

      for(final SimpleAddition.LevelMultipliers multipliers : levelMultipliers) {
        this.levelMultipliers.add(new SimpleAddition.LevelMultipliers(multipliers.damage, multipliers.sp));
      }

      this.hits.clear();

      for(final AdditionHitProperties10 properties : hits) {
        final Hit hit = new Hit(this.charId);
        hit.set(properties);
        this.hits.add(hit);
      }
    } catch(final Throwable t) {
      LOGGER.error("Failed to clone %s", addition.getRegistryId());
    }
  }

  public void set(final Additional other) {
    this.name = other.name;
    this.baseDamage = other.baseDamage;
    this.overlayFrame = other.overlayFrame;

    this.levelMultipliers.clear();

    for(final SimpleAddition.LevelMultipliers multipliers : other.levelMultipliers) {
      this.levelMultipliers.add(new SimpleAddition.LevelMultipliers(multipliers.damage, multipliers.sp));
    }

    this.hits.clear();

    for(final Hit otherHit : other.hits) {
      final Hit hit = new Hit(this.charId);
      hit.set(otherHit);
      this.hits.add(hit);
    }
  }

  public JsonObject toJson(final ModelManager modelManager) {
    final JsonArray levelMultipliers = new JsonArray();

    for(final SimpleAddition.LevelMultipliers multipliers : this.levelMultipliers) {
      final JsonObject obj = new JsonObject();
      obj.addProperty("damage", multipliers.damage);
      obj.addProperty("sp", multipliers.sp);
      levelMultipliers.add(obj);
    }

    final JsonArray hits = new JsonArray();

    for(final Hit hit : this.hits) {
      hits.add(hit.toJson(modelManager));
    }

    final JsonObject obj = new JsonObject();
    obj.addProperty("char_id", this.charRegId.toString());
    obj.addProperty("name", this.name);
    obj.addProperty("base_damage", this.baseDamage);
    obj.addProperty("overlay_frame", this.overlayFrame);
    obj.add("level_multipliers", levelMultipliers);
    obj.add("hits", hits);
    return obj;
  }

  public void fromJson(final ModelManager modelManager, final JsonObject obj) {
    this.name = obj.getAsJsonPrimitive("name").getAsString();
    this.baseDamage = obj.getAsJsonPrimitive("base_damage").getAsInt();
    this.overlayFrame = obj.getAsJsonPrimitive("overlay_frame").getAsInt();

    final JsonArray levelMultipliers = obj.getAsJsonArray("level_multipliers");
    this.levelMultipliers.clear();

    for(int i = 0; i < levelMultipliers.size(); i++) {
      final JsonObject multipliersObj = levelMultipliers.get(i).getAsJsonObject();
      this.levelMultipliers.add(new SimpleAddition.LevelMultipliers(multipliersObj.getAsJsonPrimitive("damage").getAsFloat(), multipliersObj.getAsJsonPrimitive("sp").getAsFloat()));
    }

    final JsonArray hits = obj.getAsJsonArray("hits");
    this.hits.clear();

    for(int i = 0; i < hits.size(); i++) {
      final Hit hit = new Hit(this.charId);
      hit.fromJson(modelManager, hits.get(i).getAsJsonObject());
      this.hits.add(hit);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if(!(obj instanceof final Additional other)) {
      return false;
    }

    return
      this.baseDamage == other.baseDamage &&
        this.overlayFrame == other.overlayFrame &&
        Objects.equals(this.name, other.name) &&
        Objects.equals(this.levelMultipliers, other.levelMultipliers) &&
        Objects.equals(this.hits, other.hits)
      ;
  }

  public static class Hit {
    public final int charId;

    public int animationIndex;

    /** {@link AdditionHitProperties10#flags_00} */
    public int flags = 0xc0;
    /** {@link AdditionHitProperties10#audioFile_06} */
    public int audioFile;
    /** {@link AdditionHitProperties10#additionFailAnimationIndex_0e} */
    public int failAnimation;

    /** {@link AdditionHitProperties10#totalFrames_01} */
    public int totalFrames;
    /** {@link AdditionHitProperties10#framesToHitPosition_0c} */
    public int moveFrames;
    /** {@link AdditionHitProperties10#totalSuccessFrames_03} */
    public int successFrames;
    /** {@link AdditionHitProperties10#overlayHitFrameOffset_02} */
    public int hitFrame;

    /** {@link AdditionHitProperties10#hitDistanceFromTarget_0b} */
    public int distanceFromTarget;

    /** {@link AdditionHitProperties10#cameraMovementX_08} */
    public int cameraMovementX;
    /** {@link AdditionHitProperties10#cameraMovementZ_09} */
    public int cameraMovementZ;
    /** {@link AdditionHitProperties10#cameraMovementTicks_0a} */
    public int cameraMovementTicks;

    /** {@link AdditionHitProperties10#damageMultiplier_04} */
    public int damageMultiplier;
    /** {@link AdditionHitProperties10#sp_05} */
    public int sp;

    public Hit(final int charId) {
      this.charId = charId;
    }

    public void set(final AdditionHitProperties10 properties) {
      this.flags = properties.flags_00;
      this.audioFile = properties.audioFile_06;
      this.failAnimation = properties.additionFailAnimationIndex_0e;

      this.totalFrames = properties.totalFrames_01;
      this.moveFrames = properties.framesToHitPosition_0c;
      this.successFrames = properties.totalSuccessFrames_03;
      this.hitFrame = properties.overlayHitFrameOffset_02;

      this.distanceFromTarget = properties.hitDistanceFromTarget_0b;

      this.cameraMovementX = properties.cameraMovementX_08;
      this.cameraMovementZ = properties.cameraMovementZ_09;
      this.cameraMovementTicks = properties.cameraMovementTicks_0a;

      this.damageMultiplier = properties.damageMultiplier_04;
      this.sp = properties.sp_05;
    }

    public void set(final Hit other) {
      this.animationIndex = other.animationIndex;

      this.flags = other.flags;
      this.audioFile = other.audioFile;
      this.failAnimation = other.failAnimation;

      this.totalFrames = other.totalFrames;
      this.moveFrames = other.moveFrames;
      this.successFrames = other.successFrames;
      this.hitFrame = other.hitFrame;

      this.distanceFromTarget = other.distanceFromTarget;

      this.cameraMovementX = other.cameraMovementX;
      this.cameraMovementZ = other.cameraMovementZ;
      this.cameraMovementTicks = other.cameraMovementTicks;

      this.damageMultiplier = other.damageMultiplier;
      this.sp = other.sp;
    }

    public JsonObject toJson(final ModelManager modelManager) {
      final JsonObject obj = new JsonObject();
      obj.addProperty("animation", modelManager.getFilenameForCacheIndex(this.charId, this.animationIndex));
      obj.addProperty("flags", this.flags);
      obj.addProperty("audio_file", this.audioFile);
      obj.addProperty("fail_animation", this.failAnimation);
      obj.addProperty("total_frames", this.totalFrames);
      obj.addProperty("move_frames", this.moveFrames);
      obj.addProperty("success_frames", this.successFrames);
      obj.addProperty("hit_frame", this.hitFrame);
      obj.addProperty("distance_from_target", this.distanceFromTarget);
      obj.addProperty("camera_movement_x", this.cameraMovementX);
      obj.addProperty("camera_movement_z", this.cameraMovementZ);
      obj.addProperty("camera_movement_ticks", this.cameraMovementTicks);
      obj.addProperty("damage_multiplier", this.damageMultiplier);
      obj.addProperty("sp", this.sp);
      return obj;
    }

    public void fromJson(final ModelManager modelManager, final JsonObject obj) {
      this.animationIndex = modelManager.getCacheIndexForFilename(this.charId, obj.getAsJsonPrimitive("animation").getAsString());
      this.flags = obj.getAsJsonPrimitive("flags").getAsInt();
      this.audioFile = obj.getAsJsonPrimitive("audio_file").getAsInt();
      this.failAnimation = obj.getAsJsonPrimitive("fail_animation").getAsInt();
      this.totalFrames = obj.getAsJsonPrimitive("total_frames").getAsInt();
      this.moveFrames = obj.getAsJsonPrimitive("move_frames").getAsInt();
      this.successFrames = obj.getAsJsonPrimitive("success_frames").getAsInt();
      this.hitFrame = obj.getAsJsonPrimitive("hit_frame").getAsInt();
      this.distanceFromTarget = obj.getAsJsonPrimitive("distance_from_target").getAsInt();
      this.cameraMovementX = obj.getAsJsonPrimitive("camera_movement_x").getAsInt();
      this.cameraMovementZ = obj.getAsJsonPrimitive("camera_movement_z").getAsInt();
      this.cameraMovementTicks = obj.getAsJsonPrimitive("camera_movement_ticks").getAsInt();
      this.damageMultiplier = obj.getAsJsonPrimitive("damage_multiplier").getAsInt();
      this.sp = obj.getAsJsonPrimitive("sp").getAsInt();
    }

    @Override
    public boolean equals(final Object obj) {
      if(!(obj instanceof final Hit other)) {
        return false;
      }

      return
        this.animationIndex == other.animationIndex &&
        this.flags == other.flags &&
        this.audioFile == other.audioFile &&
        this.failAnimation == other.failAnimation &&
        this.totalFrames == other.totalFrames &&
        this.moveFrames == other.moveFrames &&
        this.successFrames == other.successFrames &&
        this.hitFrame == other.hitFrame &&
        this.distanceFromTarget == other.distanceFromTarget &&
        this.cameraMovementX == other.cameraMovementX &&
        this.cameraMovementZ == other.cameraMovementZ &&
        this.cameraMovementTicks == other.cameraMovementTicks &&
        this.damageMultiplier == other.damageMultiplier &&
        this.sp == other.sp
      ;
    }
  }
}

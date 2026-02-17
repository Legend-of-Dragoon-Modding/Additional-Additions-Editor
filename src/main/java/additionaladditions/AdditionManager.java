package additionaladditions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.ReflectionAccessFilter;
import legend.lodmod.LodMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdditionManager {
  private static final Logger LOGGER = LogManager.getFormatterLogger(AdditionManager.class);

  private static final Gson SERIALIZER = new GsonBuilder().setPrettyPrinting().addReflectionAccessFilter(rawClass -> ReflectionAccessFilter.FilterResult.BLOCK_ALL).create();

  private static final Path ADDITION_DIR = Path.of("mods", "additional_additions");

  public static final List<RegistryId> CHAR_IDS = List.of(
    LodMod.id("dart"),
    LodMod.id("lavitz"),
    LodMod.id("shana"),
    LodMod.id("rose"),
    LodMod.id("haschel"),
    LodMod.id("albert"),
    LodMod.id("meru"),
    LodMod.id("kongol"),
    LodMod.id("miranda")
  );

  private final Map<RegistryId, List<Additional>> additionals = new HashMap<>();

  public List<Additional> getAdditionals(final RegistryId charId) {
    return this.additionals.computeIfAbsent(charId, key -> new ArrayList<>());
  }

  public void loadAll(final ModelManager modelManager) {
    this.additionals.clear();

    try {
      Files.createDirectories(ADDITION_DIR);

      try(final DirectoryStream<Path> stream = Files.newDirectoryStream(ADDITION_DIR, "*.json")) {
        for(final Path path : stream) {
          try {
            final String contents = Files.readString(path, StandardCharsets.UTF_8);
            final JsonObject json = SERIALIZER.fromJson(contents, JsonObject.class);

            final RegistryId charRegId = new RegistryId(json.getAsJsonPrimitive("char_id").getAsString());
            final int charId = CHAR_IDS.indexOf(charRegId);
            final Additional additional = new Additional(charId, charRegId);
            additional.fromJson(modelManager, json);
            additional.filename = path.getFileName().toString();
            additional.filename = additional.filename.substring(0, additional.filename.length() - 5);
            this.getAdditionals(additional.charRegId).add(additional);
          } catch(final Throwable t) {
            LOGGER.error("Failed to load addition " + path, t);
          }
        }
      }
    } catch(final Throwable t) {
      LOGGER.error("Failed to load additions", t);
    }
  }

  public void save(final ModelManager modelManager, final Additional additional) throws IOException {
    final String output = SERIALIZER.toJson(additional.toJson(modelManager));
    final Path path = ADDITION_DIR.resolve(additional.filename + ".json");
    Files.createDirectories(path.getParent());
    Files.writeString(path, output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }
}

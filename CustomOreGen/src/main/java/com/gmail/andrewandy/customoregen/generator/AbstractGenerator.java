package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public abstract class AbstractGenerator implements BlockGenerator {

    private static final YamlConfiguration data = new YamlConfiguration();
    private static File saveFile;
    private final UUID generatorID;
    private final int maxLevel;
    private int level;
    private Priority priority = Priority.NORMAL;

    protected AbstractGenerator(int maxLevel, int level) {
        if (level < 1 || level > maxLevel) {
            throw new IllegalArgumentException("Invalid MaxLevel or Level!");
        }
        this.maxLevel = maxLevel;
        this.level = level;
        this.generatorID = UUID.randomUUID();
    }

    protected AbstractGenerator(int maxLevel, int level, Priority priority) {
        this(maxLevel, level);
        this.priority = Objects.requireNonNull(priority);
    }

    protected AbstractGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack.getItemMeta()));
    }

    protected AbstractGenerator(ItemMeta meta) {
        Objects.requireNonNull(meta);
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        try {
            Integer level, maxLevel;
            level = wrapper.get("Level", Integer.class);
            maxLevel = wrapper.get("MaxLevel", Integer.class);
            if (level == null || maxLevel == null) {
                throw new IllegalArgumentException("Level and MaxLevel not found!");
            }
            this.level = level;
            this.maxLevel = maxLevel;
            this.priority = Priority.valueOf(wrapper.getString("Priority"));
            if (level > maxLevel || level < 1) {
                throw new IllegalStateException("Invalid Generator params detected when deserialising from ItemMeta!");
            }
            generatorID = UUID.fromString(wrapper.getString("UUID"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid item!");
        }
    }

    /**
     * All subclass MUST include a constructor to recreate the generator instance from the {@link #getDataSection()}
     * with the param of the generatorID {@link #getGeneratorID()}
     *
     * @param fromID The ID of the spawner.
     * @throws IllegalArgumentException Thrown if the ID is null or no data section with the given ID was found.
     */
    public AbstractGenerator(UUID fromID) throws IllegalArgumentException {
        Objects.requireNonNull(fromID);
        ConfigurationSection section = data.getConfigurationSection(fromID.toString());
        if (section == null) {
            throw new IllegalArgumentException("No Generator with ID: " + fromID.toString() + " in data file!");
        }
        this.maxLevel = section.getInt("MaxLevel");
        this.level = section.getInt("Level");
        if (level < 0 || level > maxLevel) {
            throw new IllegalArgumentException("Invalid Generator! Level must be positive, AND MaxLevel must be greater or equal to the level!");
        }
        this.generatorID = fromID;
    }

    public static void setDataFile(File file) throws IOException {
        saveFile = Objects.requireNonNull(file);
        if (!saveFile.isFile()) {
            saveFile.createNewFile();
        }
    }

    /**
     * Attempts to reconstruct a generator instance from the data file.
     *
     * @param generatorID The id of the instance to reconstruct.
     * @return Returns a populated optional if the generator exists.
     * @throws IllegalArgumentException Thrown if the generatorID is null.
     * @throws IllegalStateException    Thrown if there was an error reconstructing a generator.
     *                                  This usually occurs if a subclass does not have a constructor which accepts the {@link UUID} class,
     *                                  such as anonymous classes.
     */
    public static Optional<AbstractGenerator> fromID(UUID generatorID) throws IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(generatorID);
        ConfigurationSection section = data.getConfigurationSection("DefaultData");
        section = section == null ? data.createSection("DefaultData") : section;
        ConfigurationSection generatorSection = section.getConfigurationSection(generatorID.toString());
        if (generatorSection == null) {
            return Optional.empty();
        }
        String rawClass = generatorSection.getString("Class");
        if (rawClass == null) {
            return Optional.empty();
        }
        try {
            Class<?> unknown = Class.forName(rawClass);
            if (!AbstractGenerator.class.isAssignableFrom(unknown)) {
                Common.log(Level.WARNING, "&e[Data] Invalid Generator Detected! Skipping...");
                return Optional.empty();
            }
            Class<? extends AbstractGenerator> clazz = unknown.asSubclass(AbstractGenerator.class);
            return Optional.of(clazz.getConstructor(UUID.class).newInstance(generatorID));
        } catch (ReflectiveOperationException ex) {
            Common.log(Level.SEVERE, "&c[Data] Error occurred when trying to reconstruct a generator!");
            throw new IllegalStateException(ex);
        }
    }

    public static void globalUpdateFile() {
        try {
            if (saveFile == null) {
                saveFile = new File(CustomOreGen.getInstance().getDataFolder().getAbsolutePath(), "GeneratorData.yml");
            }
            data.save(saveFile);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save to file.");
        }
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Priority priority) {
        this.priority = Objects.requireNonNull(priority);
    }

    public UUID getGeneratorID() {
        return generatorID;
    }

    /**
     * Attempts to reconstruct a generator instance with the given class and the generatorID.
     *
     * @see #fromID(UUID)
     */
    public <T extends AbstractGenerator> Optional<T> fromID(UUID generatorID, Class<T> clazzObj) throws IllegalArgumentException, IllegalStateException {
        Optional<AbstractGenerator> generator = fromID(generatorID);
        if (!generator.isPresent()) {
            return Optional.empty();
        }
        if (clazzObj.isAssignableFrom(generator.get().getClass())) {
            return Optional.of(clazzObj.cast(generator.get()));
        }
        return Optional.empty();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        if (level > maxLevel || level < 1) {
            throw new IllegalArgumentException("Invalid Level!");
        }
        this.level = level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int maxLevel() {
        return maxLevel;
    }

    protected final ConfigurationSection getDataSection() {
        ConfigurationSection section = data.getConfigurationSection("DefaultData");
        section = section == null ? data.createSection("DefaultData") : section;
        ConfigurationSection ret = section.getConfigurationSection(getGeneratorID().toString());
        return ret == null ? section.createSection(getGeneratorID().toString()) : ret;
    }

    public void save() {
        ConfigurationSection section = getDataSection();
        section.set("Level", level);
        section.set("MaxLevel", maxLevel);
        section.set("Class", this.getClass().getName());
        section.set("Priority", priority.name());
    }

    public ItemStack toBaseItem(Material material) {
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        writeToMeta(meta);
        i.setItemMeta(meta);
        return i;
    }

    public void writeToMeta(ItemMeta original) {
        ItemMeta meta = Objects.requireNonNull(original);
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        //Mutate the tag container
        wrapper.setString("Class", this.getClass().getCanonicalName())
                .setString("UUID", generatorID.toString())
                .setString("Priority", priority.name())
                .setInt("MaxLevel", maxLevel)
                .setInt("Level", level);
    }

    public final void updateFile() {
        save();
        globalUpdateFile();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractGenerator generator = (AbstractGenerator) o;
        return level == generator.level &&
                maxLevel == generator.maxLevel &&
                priority == generator.priority &&
                Objects.equals(generatorID, generator.generatorID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generatorID, level, maxLevel, priority);
    }

}

package fr.rodofire.ewc.config;


import fr.rodofire.ewc.config.objects.AbstractConfigObject;
import fr.rodofire.ewc.config.objects.BooleanConfigObject;
import fr.rodofire.ewc.config.objects.EnumConfigObject;
import fr.rodofire.ewc.config.objects.IntegerConfigObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WritableConfig {
    ConfigCategory category;
    String modId;

    public WritableConfig(String modId, ConfigCategory category) {
        this.category = category;
        this.modId = modId;
    }

    public void write(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("# ----- " + category.getName() + " -----");

            // Boucles pour écrire chaque type d'objets
            writeEntries(writer, category.bools);
            if (!category.bools.isEmpty() && !category.ints.isEmpty()) {
                writer.write("# ----------------------------");
            }
            writeEntries(writer, category.ints);
            if (!category.ints.isEmpty() && !category.bools.isEmpty()) {
                writer.write("# ----------------------------");
            }
            writeEntries(writer, category.enums);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config file", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractConfigObject<U>, U> void writeEntries(BufferedWriter writer, Map<String, T> configObject) throws IOException {
        Optional<T> opt = configObject.values().stream().findFirst();
        if (opt.isPresent()) {
            writer.newLine();
            writer.newLine();
            writer.write("# " + opt.get().getObjectCategory());
            writer.newLine();
            writer.newLine();
            for (Map.Entry<String, T> obj : configObject.entrySet()) {
                String key = obj.getKey();
                U value = obj.getValue().getActualValue();
                if (value instanceof String str) {
                    value = (U) ("\"" + str + "\"");
                }

                writer.write(obj.getValue().getDefaultDescription(this.modId));
                writer.newLine();
                writer.write(key + " = " + value);
                writer.newLine();
                writer.newLine();
            }
        }
    }

    public void repairConfig(Path path) {
        Map<String, String> existingComments = new HashMap<>();
        Map<String, Boolean> existingBools = new HashMap<>();
        Map<String, Integer> existingInts = new HashMap<>();
        Map<String, String> existingStrings = new HashMap<>();

        // Lire les commentaires et les valeurs actuels du fichier existant
        if (!Files.exists(path)) {
            throw new RuntimeException("Config file does not exist");
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            String lastComment = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    lastComment = line.substring(1).trim();
                } else if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    existingComments.put(key, lastComment);

                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                        existingBools.put(key, Boolean.parseBoolean(value));
                    } else if (value.matches("-?\\d+")) {
                        existingInts.put(key, Integer.parseInt(value));
                    } else if (value.startsWith("\"") && value.endsWith("\"")) {
                        existingStrings.put(key, value.substring(1, value.length() - 1));
                    }
                    lastComment = "";
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file", e);
        }

        // Réparer ou compléter le fichier
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("# ----- " + category.getName() + " -----");
            writer.newLine();
            writer.newLine();

            repairEntries(writer, category.bools, existingComments, existingBools);
            if (!category.bools.isEmpty() && !category.ints.isEmpty()) {
                writer.write("# ----------------------------");
                writer.newLine();
            }
            repairEntries(writer, category.ints, existingComments, existingInts);
            if (!category.ints.isEmpty() && !category.enums.isEmpty()) {
                writer.write("# ----------------------------");
                writer.newLine();
            }
            repairEntries(writer, category.enums, existingComments, existingStrings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to repair config file", e);
        }
    }

    private <T extends AbstractConfigObject<U>, U> void repairEntries(
            BufferedWriter writer,
            Map<String, T> configObject,
            Map<String, String> existingComments,
            Map<String, ?> existingValues
    ) throws IOException {
        Optional<T> opt = configObject.values().stream().findFirst();
        if (opt.isPresent()) {
            writer.newLine();
            writer.write("# " + opt.get().getObjectCategory());
            writer.newLine();
            writer.newLine();

            for (Map.Entry<String, T> entry : configObject.entrySet()) {
                String key = entry.getKey();
                T configValue = entry.getValue();

                // Vérifier ou écrire le commentaire
                String defaultComment = configValue.getDefaultDescription(this.modId);
                if (!defaultComment.equals(existingComments.getOrDefault(key, ""))) {
                    writer.write("# " + defaultComment);
                }

                writer.newLine();

                // Vérifier ou réparer la valeur
                U defaultValue = configValue.getDefaultValue();
                Object existingValue = existingValues.get(key);

                if (!isValueValid(existingValue, configValue)) {
                    existingValue = defaultValue;
                }

                writer.write(key + " = " + formatValue(existingValue));
                writer.newLine();
                writer.newLine();
            }
        }
    }

    private boolean isValueValid(Object value, AbstractConfigObject<?> configValue) {
        if (value == null) return false;

        if (configValue instanceof BooleanConfigObject && value instanceof Boolean) {
            return true;
        } else if (configValue instanceof IntegerConfigObject intConfig && value instanceof Integer) {
            int intValue = (Integer) value;
            return intValue >= intConfig.getMinValue() && intValue <= intConfig.getMaxValue();
        } else if (configValue instanceof EnumConfigObject enumConfig && value instanceof String) {
            return enumConfig.getPossibleValues().contains(value.toString());
        }

        return false;
    }

    private String formatValue(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\""; // Ajouter des guillemets pour les chaînes
        }
        return value.toString(); // Nombres et booléens sans guillemets
    }

}
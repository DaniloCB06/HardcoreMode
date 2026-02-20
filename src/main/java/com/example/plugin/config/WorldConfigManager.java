package com.example.plugin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia as configurações do HardcoreMode para cada mundo.
 * Cada mundo tem suas próprias configurações salvas em arquivos separados.
 */
public class WorldConfigManager {
    private static final String CONFIG_DIR = "config/hardcoremode/worlds";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private final Map<String, WorldHardcoreConfig> worldConfigs = new ConcurrentHashMap<>();
    private final Path configDir;
    private final java.util.function.Supplier<HardcoreModeConfig> globalConfigSupplier;
    
    public WorldConfigManager(Path dataDirectory, java.util.function.Supplier<HardcoreModeConfig> globalConfigSupplier) {
        this.configDir = dataDirectory.resolve("worlds");
        this.globalConfigSupplier = globalConfigSupplier;
        ensureConfigDirectory();
    }
    
    private void ensureConfigDirectory() {
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            // Ignorar erros ao criar diretório
        }
    }
    
    /**
     * Obtém a configuração para um mundo específico.
     * Se não existir, cria uma nova com valores padrão.
     */
    public WorldHardcoreConfig getWorldConfig(String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            return new WorldHardcoreConfig();
        }
        
        String key = worldName.toLowerCase();
        
        // Primeiro verificar se já existe no cache
        WorldHardcoreConfig existing = worldConfigs.get(key);
        if (existing != null) {
            return existing;
        }
        
        // Tentar carregar do disco ou criar novo
        WorldHardcoreConfig config = loadConfig(key);
        if (config == null) {
            config = new WorldHardcoreConfig();
            HardcoreModeConfig globalConfig = globalConfigSupplier != null ? globalConfigSupplier.get() : null;
            if (globalConfig != null) {
                config.applyDefaultsFromGlobal(globalConfig);
            }
        }
        
        // Usar putIfAbsent para evitar race conditions
        WorldHardcoreConfig previous = worldConfigs.putIfAbsent(key, config);
        if (previous != null) {
            return previous;
        }
        
        // Salvar se foi criado novo
        saveWorldConfig(worldName);
        return config;
    }
    
    /**
     * Verifica se existe uma configuração salva para o mundo.
     */
    public boolean hasWorldConfig(String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            return false;
        }
        
        String key = worldName.toLowerCase();
        if (worldConfigs.containsKey(key)) {
            return true;
        }
        
        Path configFile = getConfigFile(key);
        return Files.exists(configFile);
    }
    
    /**
     * Salva a configuração de um mundo específico.
     */
    public void saveWorldConfig(String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            return;
        }
        
        String key = worldName.toLowerCase();
        WorldHardcoreConfig config = worldConfigs.get(key);
        if (config == null) {
            return;
        }
        
        Path configFile = getConfigFile(key);
        try {
            String json = GSON.toJson(config);
            Files.writeString(configFile, json);
        } catch (IOException e) {
            // Ignorar erros ao salvar
        }
    }
    
    /**
     * Salva todas as configurações de mundos.
     */
    public void saveAllConfigs() {
        for (String worldName : worldConfigs.keySet()) {
            saveWorldConfig(worldName);
        }
    }
    
    /**
     * Recarrega a configuração de um mundo do disco.
     */
    public void reloadWorldConfig(String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            return;
        }
        
        String key = worldName.toLowerCase();
        WorldHardcoreConfig config = loadConfig(key);
        if (config != null) {
            worldConfigs.put(key, config);
        }
    }
    
    /**
     * Remove a configuração de um mundo da memória (não deleta o arquivo).
     */
    public void unloadWorldConfig(String worldName) {
        if (worldName == null || worldName.isEmpty()) {
            return;
        }
        
        String key = worldName.toLowerCase();
        worldConfigs.remove(key);
    }
    
    /**
     * Verifica se o HardcoreMode está habilitado para um mundo.
     */
    public boolean isWorldEnabled(String worldName) {
        WorldHardcoreConfig config = getWorldConfig(worldName);
        return config.enabled;
    }
    
    /**
     * Define se o HardcoreMode está habilitado para um mundo.
     */
    public void setWorldEnabled(String worldName, boolean enabled) {
        WorldHardcoreConfig config = getWorldConfig(worldName);
        config.enabled = enabled;
        saveWorldConfig(worldName);
    }
    
    private WorldHardcoreConfig loadConfig(String worldKey) {
        Path configFile = getConfigFile(worldKey);
        if (!Files.exists(configFile)) {
            return null;
        }
        
        try {
            String json = Files.readString(configFile);
            return GSON.fromJson(json, WorldHardcoreConfig.class);
        } catch (IOException e) {
            return null;
        }
    }
    
    private Path getConfigFile(String worldKey) {
        return configDir.resolve(worldKey + ".json");
    }
    
    /**
     * Obtém todos os mundos que têm configurações carregadas.
     */
    public java.util.Set<String> getLoadedWorlds() {
        return new java.util.HashSet<>(worldConfigs.keySet());
    }

    /**
     * Obtém todos os mundos armazenados em disco (arquivos .json).
     */
    public java.util.Set<String> getStoredWorlds() {
        java.util.Set<String> result = new java.util.HashSet<>();
        try {
            ensureConfigDirectory();
            try (java.util.stream.Stream<Path> stream = Files.list(configDir)) {
                stream.filter(path -> path.getFileName().toString().toLowerCase().endsWith(".json"))
                        .forEach(path -> {
                            String fileName = path.getFileName().toString();
                            int dotIndex = fileName.lastIndexOf('.');
                            if (dotIndex > 0) {
                                result.add(fileName.substring(0, dotIndex));
                            }
                        });
            }
        } catch (IOException ignored) {
        }
        return result;
    }
}

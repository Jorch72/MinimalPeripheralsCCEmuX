package club.krist.minimalperipherals.ccemux;

import club.krist.minimalperipherals.ccemux.sound.SoundSystem;
import net.clgd.ccemux.peripherals.PeripheralFactory;
import net.clgd.ccemux.plugins.Plugin;
import net.clgd.ccemux.plugins.config.JSONConfigHandler;
import net.clgd.ccemux.plugins.config.PluginConfigHandler;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MinimalPlugin extends Plugin {
    public static final Logger log = LoggerFactory.getLogger(MinimalPlugin.class);
    
    private MinimalPluginConfig config;
    
    @Override
    public String getName() {
        return "MinimalPeripheralsCCEmuX";
    }

    @Override
    public String getDescription() {
        return "MinimalPeripherals for CCEmuX";
    }

    @Override
    public Optional<String> getVersion() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getAuthor() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getWebsite() {
        return Optional.empty();
    }
    
    @Override
    public Optional<PluginConfigHandler<?>> getConfigHandler() {
        return Optional.of(new JSONConfigHandler<MinimalPluginConfig>(new MinimalPluginConfig()) {
            @Override
            public void configLoaded(MinimalPluginConfig config) {
               MinimalPlugin.this.config = config;
            }
        });
    }
    
    @Override
    public void setup() {
        PeripheralFactory.implementations.put("iron_noteblock", IronNoteblock::new);
    
        Path assetsDirectory = OperatingSystem.get().getAppDataDir().resolve(".minecraft/assets");
        
        if (config.minecraftDir != null) {
            assetsDirectory = Paths.get(config.minecraftDir);
        }
        
        if (!assetsDirectory.toFile().exists()) {
            throw new RuntimeException("Minecraft directory not found.");
        }
        
        AtomicReference<String> indexName = new AtomicReference<>(config.minecraftVersion);
        
        if (config.minecraftVersion == null) {
            try {
                Files.list(assetsDirectory.resolve("indexes"))
					.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("json"))
                    .map(f -> FilenameUtils.getBaseName(f.getName()))
					.sorted(Comparator.reverseOrder())
					.findFirst()
					.ifPresent(indexName::set);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        log.info("Loading minecraft assets from {}, index {}", assetsDirectory.toString(), indexName.get());
        
        new SoundSystem(config, assetsDirectory.toFile(), "1.11");
    }
}

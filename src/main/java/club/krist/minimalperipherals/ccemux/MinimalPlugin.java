package club.krist.minimalperipherals.ccemux;

import club.krist.minimalperipherals.ccemux.sound.SoundSystem;
import net.clgd.ccemux.peripherals.PeripheralFactory;
import net.clgd.ccemux.plugins.Plugin;
import net.clgd.ccemux.plugins.config.JSONConfigHandler;
import net.clgd.ccemux.plugins.config.PluginConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MinimalPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(MinimalPlugin.class);
    
    private Path minecraftDirectory;
    
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
        return Optional.of(new JSONConfigHandler<Map<String, String>>(new HashMap<>()) {
            @Override
            public void configLoaded(Map<String, String> config) {
                if (config.containsKey("minecraftDir")) {
                    minecraftDirectory = Paths.get(config.get("minecraftDir"));
                }
            }
        });
    }
    
    @Override
    public void setup() {
        PeripheralFactory.implementations.put("iron_noteblock", IronNoteblock::new);
        
        if (minecraftDirectory == null) {
            minecraftDirectory = OperatingSystem.get().getAppDataDir().resolve(".minecraft");
        }
        
        if (!minecraftDirectory.toFile().exists()) {
            throw new RuntimeException("Minecraft directory not found.");
        }
        
        log.info("Loading minecraft assets from {}", minecraftDirectory.toString());
        
        new SoundSystem(minecraftDirectory.resolve("assets").toFile(), "1.11");
    }
}

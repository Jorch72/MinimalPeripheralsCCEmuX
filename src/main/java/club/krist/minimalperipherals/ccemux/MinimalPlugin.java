package club.krist.minimalperipherals.ccemux;

import club.krist.minimalperipherals.ccemux.sound.SoundSystem;
import net.clgd.ccemux.peripherals.PeripheralFactory;
import net.clgd.ccemux.plugins.Plugin;

import java.io.File;
import java.util.Optional;

public class MinimalPlugin extends Plugin {
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
    public void setup() {
        PeripheralFactory.implementations.put("iron_noteblock", IronNoteblock::new);
        new SoundSystem(new File("[APPDATA]/.minecraft/assets"), "1.11");
    }
}

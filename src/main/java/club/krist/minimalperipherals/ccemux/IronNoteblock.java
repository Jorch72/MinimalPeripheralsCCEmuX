package club.krist.minimalperipherals.ccemux;

import club.krist.minimalperipherals.ccemux.sound.SoundSystem;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;


public class IronNoteblock extends MinimalPeripheral {
    private static final String[] instruments = {"harp", "bd", "snare", "hat", "bassattack"};

    public IronNoteblock() {
        super("iron_noteblock");

        newMethod("test", o -> {
            SoundSystem.instance.playSound("minecraft:sounds/note/harp.ogg", 1.0f, 1.0f);
            return new Object[]{};
        });

        newMethod("playSound", arguments -> {
            if (arguments.length < 3)
                throw new LuaException("Wrong number of arguments. 3 expected.");
            if (!(arguments[0] instanceof String))
                throw new LuaException("Bad argument #1 (expected string)");
            if (!(arguments[1] instanceof Double))
                throw new LuaException("Bad argument #2 (expected number)");
            if (!(arguments[2] instanceof Double))
                throw new LuaException("Bad argument #3 (expected number)");

            String sound = (String) arguments[0];
            float volume = (float) (double) (Double) arguments[1];
            float pitch = (float) (double) (Double) arguments[2];

            if (sound.length() < 17 || !sound.substring(0, 17).equals("minecraft:sounds/"))
                sound = "minecraft:sounds/" + sound;

            if (sound.length() > 4 && !sound.substring(sound.length() - 4, sound.length()).equals(".ogg")) {
                sound = sound.replace('.', '/');
                sound = sound + ".ogg";
            }

            if (!SoundSystem.instance.soundExists(sound))
                throw new LuaException("Bad argument #1 (sound does not exist) " + sound);

            SoundSystem.instance.playSound(sound, pitch, volume);
            return new Object[]{};
        });

        newMethod("playNote", arguments -> {
            if (arguments.length < 2)
                throw new LuaException("Wrong number of arguments. 2 expected.");
            if (!(arguments[0] instanceof Double))
                throw new LuaException("Bad argument #1 (expected number)");
            if (!(arguments[1] instanceof Double))
                throw new LuaException("Bad argument #2 (expected number)");

            String instrument = "minecraft:sounds/note/" + instruments[(int) (double) (Double) arguments[0]] + ".ogg";
            float note = (float) Math.pow(2D, ((int) (double) (Double) arguments[1] - 12) / 12D);

            SoundSystem.instance.playSound(instrument, note, 3f);
            return new Object[]{};
        });
    }

    public void attach(IComputerAccess iComputerAccess) {

    }

    public void detach(IComputerAccess iComputerAccess) {

    }
}

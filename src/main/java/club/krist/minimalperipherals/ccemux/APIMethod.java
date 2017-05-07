package club.krist.minimalperipherals.ccemux;

import dan200.computercraft.api.lua.LuaException;

@FunctionalInterface
public interface APIMethod {
    Object[] accept(Object[] o) throws LuaException;
}

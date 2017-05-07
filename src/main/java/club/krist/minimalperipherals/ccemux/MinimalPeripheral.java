package club.krist.minimalperipherals.ccemux;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MinimalPeripheral implements IPeripheral {

    private String type;

    private final Map<String, APIMethod> methods = new LinkedHashMap<>();

    final void newMethod(String name, APIMethod method) {
        methods.put(name, method);
    }

    public MinimalPeripheral(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String[] getMethodNames() {
        return methods.keySet().toArray(new String[]{ });
    }

    @Override
    public Object[] callMethod(IComputerAccess iComputerAccess, ILuaContext iLuaContext, int method, Object[] arguments) throws LuaException, InterruptedException {
        return new ArrayList<APIMethod>(methods.values()).get(method).accept(arguments);
    }

    public abstract void attach(IComputerAccess iComputerAccess);

    public abstract void detach(IComputerAccess iComputerAccess);

    @Override
    public boolean equals(IPeripheral iPeripheral) {
        return this.getClass() == iPeripheral.getClass();
    }
}

package com.djayfresh.freshcaa.entity.task;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.StringRepresentable;

/** The jobs a hired Factory Worker can be given. Only {@link #isImplemented() implemented} types can be selected. */
public enum TaskType implements StringRepresentable {
    NONE("none", true, List.of()),
    HAUL("haul", true, List.of(BindingSlot.SUPPLY, BindingSlot.OUTPUT)),
    TEND("tend", false, List.of(BindingSlot.SUPPLY, BindingSlot.MACHINE, BindingSlot.OUTPUT)),
    MIX("mix", false, List.of(BindingSlot.SUPPLY, BindingSlot.MACHINE, BindingSlot.OUTPUT)),
    BUILD("build", false, List.of(BindingSlot.SUPPLY));

    public static final Codec<TaskType> CODEC = StringRepresentable.fromEnum(TaskType::values);

    private final String name;
    private final boolean implemented;
    private final List<BindingSlot> bindings;

    TaskType(String name, boolean implemented, List<BindingSlot> bindings) {
        this.name = name;
        this.implemented = implemented;
        this.bindings = bindings;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    /** False for task types that are planned but not yet runnable; their buttons show greyed out. */
    public boolean isImplemented() {
        return this.implemented;
    }

    /** The block bindings this task uses, in the order the Clipboard fills them. */
    public List<BindingSlot> bindings() {
        return this.bindings;
    }

    public String translationKey() {
        return "freshcaa.worker.task." + this.name;
    }

    public static TaskType byOrdinal(int ordinal) {
        TaskType[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NONE;
    }
}

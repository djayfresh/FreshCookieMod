package com.djayfresh.freshcaa.entity.task;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** The roles a bound block can play in a task. A task type lists which of these it uses. */
public enum BindingSlot implements StringRepresentable {
    SUPPLY("supply"),
    MACHINE("machine"),
    OUTPUT("output");

    public static final Codec<BindingSlot> CODEC = StringRepresentable.fromEnum(BindingSlot::values);

    private final String name;

    BindingSlot(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public String translationKey() {
        return "freshcaa.worker.binding." + this.name;
    }

    public static BindingSlot byOrdinal(int ordinal) {
        BindingSlot[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : SUPPLY;
    }
}

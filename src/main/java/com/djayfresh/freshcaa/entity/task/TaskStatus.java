package com.djayfresh.freshcaa.entity.task;

/** What a hired worker is doing, or why it is not. Shown in the task screen with a detail line. */
public enum TaskStatus {
    IDLE(0xFF404040),
    WORKING(0xFF2E7D32),
    WAITING_SUPPLY(0xFF8A6D00),
    OUTPUT_FULL(0xFF8A6D00),
    BLOCKED(0xFFB71C1C),
    MISSING_BINDING(0xFFB71C1C),
    NEEDS_MATERIALS(0xFF8A6D00);

    private final int color;

    TaskStatus(int color) {
        this.color = color;
    }

    /** ARGB text colour for the status line. */
    public int color() {
        return this.color;
    }

    public String translationKey() {
        return "freshcaa.worker.status." + this.name().toLowerCase(java.util.Locale.ROOT);
    }

    public static TaskStatus byOrdinal(int ordinal) {
        TaskStatus[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : IDLE;
    }
}

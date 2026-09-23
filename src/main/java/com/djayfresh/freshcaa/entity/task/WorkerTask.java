package com.djayfresh.freshcaa.entity.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;

/**
 * A worker's assignment: which job and which blocks it works between. Immutable; the worker swaps in a new one on
 * every change. Serialised with {@link #CODEC} into the entity's save data and, as JSON text, into its synched data so
 * the task screen can show it on the client.
 */
public record WorkerTask(TaskType type, Map<BindingSlot, BlockPos> bindings) {
    public static final WorkerTask EMPTY = new WorkerTask(TaskType.NONE, Map.of());

    public static final Codec<WorkerTask> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TaskType.CODEC.optionalFieldOf("type", TaskType.NONE).forGetter(WorkerTask::type),
            Codec.unboundedMap(BindingSlot.CODEC, BlockPos.CODEC).optionalFieldOf("bindings", Map.of()).forGetter(WorkerTask::bindings)
    ).apply(instance, WorkerTask::new));

    public WorkerTask {
        bindings = Map.copyOf(bindings);
    }

    public Optional<BlockPos> binding(BindingSlot slot) {
        return Optional.ofNullable(this.bindings.get(slot));
    }

    public boolean isBound(BindingSlot slot) {
        return this.bindings.containsKey(slot);
    }

    /** Switches job type; bindings are kept so a Supply chest survives a change from Haul to Tend. */
    public WorkerTask withType(TaskType newType) {
        return new WorkerTask(newType, this.bindings);
    }

    public WorkerTask withBinding(BindingSlot slot, BlockPos pos) {
        Map<BindingSlot, BlockPos> copy = new HashMap<>(this.bindings);
        copy.put(slot, pos.immutable());
        return new WorkerTask(this.type, copy);
    }

    public WorkerTask withoutBinding(BindingSlot slot) {
        Map<BindingSlot, BlockPos> copy = new HashMap<>(this.bindings);
        copy.remove(slot);
        return new WorkerTask(this.type, copy);
    }

    /** JSON form used for the synched entity data. */
    public String toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).result().map(JsonElement::toString).orElse("");
    }

    public static WorkerTask fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return EMPTY;
        }
        try {
            return CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).result().orElse(EMPTY);
        } catch (RuntimeException e) {
            return EMPTY;
        }
    }
}

package com.djayfresh.freshcaa.entity;

import com.djayfresh.freshcaa.Config;
import com.djayfresh.freshcaa.block.WorkPostBlock;
import com.djayfresh.freshcaa.entity.ai.DoAssignedTaskGoal;
import com.djayfresh.freshcaa.entity.ai.FindChestGoal;
import com.djayfresh.freshcaa.entity.ai.WanderInSunGoal;
import com.djayfresh.freshcaa.entity.ai.WanderNearHomeGoal;
import com.djayfresh.freshcaa.entity.task.BindingSlot;
import com.djayfresh.freshcaa.entity.task.TaskStatus;
import com.djayfresh.freshcaa.entity.task.TaskType;
import com.djayfresh.freshcaa.entity.task.WorkerTask;
import com.djayfresh.freshcaa.entity.task.WorkerTransfers;
import com.djayfresh.freshcaa.menu.FactoryWorkerMenu;
import com.djayfresh.freshcaa.registry.ModTags;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Prediction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * The Factory Worker. Wild ones wander in the sun and nose around chests, as they always did. Hand one enough cookies
 * and it is hired: it gets a home (a Work Post or the spot it was hired), a task screen, a four-slot carry inventory,
 * and it runs whatever {@link WorkerTask} it is given until told otherwise. Never despawns.
 */
public class FactoryWorker extends PathfinderMob {
    public static final int CARRY_SLOTS = 4;

    private static final float MAX_HEALTH = 10.0F;
    private static final double MOVEMENT_SPEED = 0.25;
    private static final float WILD_RANGE = 20.0F;
    private static final int UNHAPPY_TICKS = 40;

    private static final EntityDataAccessor<Boolean> DATA_HIRED = SynchedEntityData.defineId(FactoryWorker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_STATUS = SynchedEntityData.defineId(FactoryWorker.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_STATUS_DETAIL = SynchedEntityData.defineId(FactoryWorker.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_TASK = SynchedEntityData.defineId(FactoryWorker.class, EntityDataSerializers.STRING);

    private WorkerTask task = WorkerTask.EMPTY;
    private @Nullable BlockPos home;
    private final SimpleContainer carry = new SimpleContainer(CARRY_SLOTS);
    private final SimpleContainer filter = new SimpleContainer(1);
    private int unhappyTicks;

    // Client-side cache of the synched task JSON.
    private String cachedTaskJson = "";
    private WorkerTask cachedTask = WorkerTask.EMPTY;

    public FactoryWorker(EntityType<? extends FactoryWorker> type, Level level) {
        super(type, level);
        this.getNavigation().setCanOpenDoors(true);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(3, new DoAssignedTaskGoal(this));
        this.goalSelector.addGoal(4, new FindChestGoal(this, 1.0, (int) WILD_RANGE, () -> !this.isHired()));
        this.goalSelector.addGoal(4, new WanderNearHomeGoal(this));
        this.goalSelector.addGoal(5, new WanderInSunGoal(this, 1.0, WILD_RANGE, () -> !this.isHired()));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HIRED, false);
        builder.define(DATA_STATUS, TaskStatus.IDLE.ordinal());
        builder.define(DATA_STATUS_DETAIL, "");
        builder.define(DATA_TASK, "");
    }

    // ---- Hiring -------------------------------------------------------------------------------------------------

    public boolean isHired() {
        return this.entityData.get(DATA_HIRED);
    }

    private void hire(Player player) {
        this.entityData.set(DATA_HIRED, true);
        this.setPersistenceRequired();
        if (this.home == null) {
            this.home = this.blockPosition();
        }
        this.setTask(WorkerTask.EMPTY);
        this.setStatus(TaskStatus.IDLE, null);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.5, this.getZ(), 7, 0.5, 0.5, 0.5, 0.02);
            player.sendOverlayMessage(Component.translatable("freshcaa.worker.hired", this.getDisplayName()));
        }
    }

    /** Hands back whatever the worker is carrying and lets it go. */
    public void dismiss(Player player) {
        for (int slot = 0; slot < this.carry.getContainerSize(); slot++) {
            ItemStack stack = this.carry.removeItemNoUpdate(slot);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack, Prediction.SERVER_ONLY);
            }
        }
        ItemStack sample = this.filter.removeItemNoUpdate(0);
        if (!sample.isEmpty()) {
            player.getInventory().placeItemBackInInventory(sample, Prediction.SERVER_ONLY);
        }
        this.entityData.set(DATA_HIRED, false);
        this.home = null;
        this.setTask(WorkerTask.EMPTY);
        this.setStatus(TaskStatus.IDLE, null);
        this.updateHeldItem();
        player.sendOverlayMessage(Component.translatable("freshcaa.worker.dismissed", this.getDisplayName()));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!this.isHired()) {
            int cost = Config.HIRE_COST.get();
            if (!held.is(ModTags.Items.COOKIES) || held.getCount() < cost) {
                if (!this.level().isClientSide()) {
                    player.sendOverlayMessage(Component.translatable("freshcaa.worker.hire_hint", cost));
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide()) {
                held.consume(cost, player);
                this.hire(player);
            }
            return InteractionResult.SUCCESS;
        }
        if (!this.level().isClientSide()) {
            this.openTaskScreen(player);
        }
        return InteractionResult.SUCCESS;
    }

    public void openTaskScreen(Player player) {
        player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new FactoryWorkerMenu(id, inventory, this), this.getDisplayName()),
                buf -> buf.writeVarInt(this.getId()));
    }

    // ---- Task and status -------------------------------------------------------------------------------------------

    /** The current assignment. On the client this is decoded from the synched JSON. */
    public WorkerTask getTask() {
        if (this.level().isClientSide()) {
            String json = this.entityData.get(DATA_TASK);
            if (!json.equals(this.cachedTaskJson)) {
                this.cachedTaskJson = json;
                this.cachedTask = WorkerTask.fromJson(json);
            }
            return this.cachedTask;
        }
        return this.task;
    }

    public void setTask(WorkerTask task) {
        this.task = task;
        this.entityData.set(DATA_TASK, task.toJson());
        this.setStatus(TaskStatus.IDLE, null);
    }

    public void setTaskType(TaskType type) {
        if (type.isImplemented()) {
            this.setTask(this.task.withType(type));
        }
    }

    public void bind(BindingSlot slot, BlockPos pos) {
        this.setTask(this.task.withBinding(slot, pos));
    }

    public void unbind(BindingSlot slot) {
        this.setTask(this.task.withoutBinding(slot));
    }

    /**
     * Applies positions recorded on a Clipboard: a Work Post becomes the home, everything else fills the task's binding
     * slots in order. A worker with no job is put on Haul so the bindings have somewhere to go.
     */
    public void applyClipboard(java.util.List<BlockPos> positions) {
        WorkerTask updated = this.task.type() == TaskType.NONE ? this.task.withType(TaskType.HAUL) : this.task;
        java.util.Iterator<BindingSlot> slots = updated.type().bindings().iterator();
        for (BlockPos pos : positions) {
            BlockState state = this.level().getBlockState(pos);
            if (state.getBlock() instanceof WorkPostBlock) {
                this.home = pos.immutable();
                continue;
            }
            if (slots.hasNext()) {
                updated = updated.withBinding(slots.next(), pos);
            }
        }
        this.setTask(updated);
    }

    /** Scans the work area for the closest block that fits the slot and binds it; false if nothing fits. */
    public boolean bindNearest(BindingSlot slot) {
        BlockPos centre = this.getHomeOrPosition();
        int range = Config.WORKER_WORK_RANGE.get();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-range, -4, -range), centre.offset(range, 4, range))) {
            if (this.task.bindings().containsValue(pos)) {
                continue;
            }
            if (!WorkerTransfers.looksLikeContainer(this.level(), pos)) {
                continue;
            }
            double distance = pos.distSqr(this.blockPosition());
            if (distance < bestDistance) {
                bestDistance = distance;
                best = pos.immutable();
            }
        }
        if (best == null) {
            return false;
        }
        this.bind(slot, best);
        return true;
    }

    public TaskStatus getStatus() {
        return TaskStatus.byOrdinal(this.entityData.get(DATA_STATUS));
    }

    /** The status detail (a block name and position, or a binding name), or null if there is none. */
    public @Nullable Component getStatusDetail() {
        String json = this.entityData.get(DATA_STATUS_DETAIL);
        if (json.isEmpty()) {
            return null;
        }
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString(json)).result().orElse(null);
    }

    public void setStatus(TaskStatus status, @Nullable Component detail) {
        this.entityData.set(DATA_STATUS, status.ordinal());
        String json = detail == null ? "" : ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, detail).result().map(Object::toString).orElse("");
        this.entityData.set(DATA_STATUS_DETAIL, json);
    }

    /** "Chest (12, 64, -3)" style description of a bound block, for status lines and the task screen. */
    public static Component describe(Level level, BlockPos pos) {
        Component name = level.isLoaded(pos) ? level.getBlockState(pos).getBlock().getName() : Component.translatable("freshcaa.worker.unloaded");
        return Component.translatable("freshcaa.worker.block_at", name, pos.getX(), pos.getY(), pos.getZ());
    }

    public void showUnhappy() {
        this.unhappyTicks = UNHAPPY_TICKS;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getY() + 1.8, this.getZ(), 3, 0.3, 0.3, 0.3, 0.0);
        }
    }

    // ---- Home and inventory -----------------------------------------------------------------------------------------

    public @Nullable BlockPos getHome() {
        return this.home;
    }

    public void setHome(@Nullable BlockPos home) {
        this.home = home == null ? null : home.immutable();
    }

    public BlockPos getHomeOrPosition() {
        return this.home != null ? this.home : this.blockPosition();
    }

    public SimpleContainer getCarry() {
        return this.carry;
    }

    public SimpleContainer getFilter() {
        return this.filter;
    }

    /** Mirrors the first carried stack into the main hand so the crossed-arms layer shows what the worker carries. */
    private void updateHeldItem() {
        ItemStack shown = ItemStack.EMPTY;
        for (int slot = 0; slot < this.carry.getContainerSize(); slot++) {
            ItemStack stack = this.carry.getItem(slot);
            if (!stack.isEmpty()) {
                shown = stack;
                break;
            }
        }
        ItemStack current = this.getMainHandItem();
        if (!ItemStack.isSameItemSameComponents(current, shown) || current.getCount() != shown.getCount()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, shown.copy());
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            if (this.tickCount % 10 == 0) {
                this.updateHeldItem();
            }
            if (this.unhappyTicks > 0) {
                this.unhappyTicks--;
            }
        }
    }

    public boolean isUnhappy() {
        return this.unhappyTicks > 0;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
        super.dropCustomDeathLoot(level, source, killedByPlayer);
        Containers.dropContents(level, this, this.carry);
        Containers.dropContents(level, this, this.filter);
    }

    // ---- Persistence ------------------------------------------------------------------------------------------------

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("hired", this.isHired());
        output.store("task", WorkerTask.CODEC, this.task);
        output.storeNullable("home", BlockPos.CODEC, this.home);
        this.carry.storeAsItemList(output.list("carry", ItemStack.OPTIONAL_CODEC));
        this.filter.storeAsItemList(output.list("filter", ItemStack.OPTIONAL_CODEC));
        output.putInt("status", this.getStatus().ordinal());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(DATA_HIRED, input.getBooleanOr("hired", false));
        this.home = input.read("home", BlockPos.CODEC).orElse(null);
        this.carry.fromItemList(input.listOrEmpty("carry", ItemStack.OPTIONAL_CODEC));
        this.filter.fromItemList(input.listOrEmpty("filter", ItemStack.OPTIONAL_CODEC));
        this.setTask(input.read("task", WorkerTask.CODEC).orElse(WorkerTask.EMPTY));
        this.setStatus(TaskStatus.byOrdinal(input.getIntOr("status", 0)), null);
        this.updateHeldItem();
    }

    // ---- Sounds and despawning --------------------------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }
}

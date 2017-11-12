package com.minecolonies.api.colony.requestsystem.requestable;

import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.IToolType;
import com.minecolonies.api.util.constant.ToolType;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to represent tools inside the request system.
 */
public class Tool implements IDeliverable
{

    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_TYPE  = "Type";
    private static final String NBT_MIN_LEVEL = "MinLevel";
    private static final String NBT_MAX_LEVEL  = "MaxLevel";
    private static final String NBT_RESULT = "Result";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    @NotNull
    private final IToolType toolClass;

    @NotNull
    private final Integer minLevel;

    @NotNull
    private final Integer maxLevel;

    @NotNull
    private ItemStack result;

    public Tool(@NotNull final IToolType toolClass, @NotNull final Integer minLevel, @NotNull final Integer maxLevel)
    {
        this(toolClass, minLevel, maxLevel, ItemStackUtils.EMPTY);
    }

    public Tool(@NotNull final IToolType toolClass, @NotNull final Integer minLevel, @NotNull final Integer maxLevel, @NotNull final ItemStack result)
    {
        this.toolClass = toolClass;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.result = result;
    }

    /**
     * Returns the tool class that is requested.
     *
     * @return The tool class that is requested.
     */
    @NotNull
    public IToolType getToolClass()
    {
        return toolClass;
    }

    /**
     * The minimal tool level requested.
     *
     * @return The minimal tool level requested.
     */
    @NotNull
    public Integer getMinLevel()
    {
        return minLevel;
    }

    /**
     * The maximum tool level requested.
     *
     * @return The maximum tool level requested.
     */
    @NotNull
    public Integer getMaxLevel()
    {
        return maxLevel;
    }

    /**
     * The resulting stack if set during creation, else ItemStack.Empty.
     *
     * @return The resulting stack.
     */
    @NotNull
    public ItemStack getResult()
    {
        return result;
    }

    /**
     * Serializes this Tool into NBT.
     *
     * @param controller The IFactoryController used to serialize sub types.
     * @return The NBTTagCompound containing the tool data.
     */
    @NotNull
    public static NBTTagCompound serialize(IFactoryController controller, Tool tool) {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString(NBT_TYPE, tool.getToolClass().getName());
        compound.setInteger(NBT_MIN_LEVEL, tool.getMinLevel());
        compound.setInteger(NBT_MAX_LEVEL, tool.getMaxLevel());
        compound.setTag(NBT_RESULT, tool.getResult().serializeNBT());

        return compound;
    }

    /**
     * Static method that constructs an instance from NBT.
     *
     * @param controller The {@link IFactoryController} to deserialize components with.
     * @param nbt The nbt to serialize from.
     * @return An instance of Tool with the data contained in the given NBT.
     */
    @NotNull
    public static Tool deserialize(IFactoryController controller, NBTTagCompound nbt)
    {
        //API:Map the given strings a proper way.
        IToolType type = ToolType.getToolType(nbt.getString(NBT_TYPE));
        Integer minLevel = nbt.getInteger(NBT_MIN_LEVEL);
        Integer maxLevel = nbt.getInteger(NBT_MAX_LEVEL);
        ItemStack result = new ItemStack(nbt.getCompoundTag(NBT_RESULT));

        return new Tool(type, minLevel, maxLevel, result);
    }

    @Override
    public boolean matches(@NotNull final ItemStack stack)
    {
        if (getToolClass().equals(ToolType.HOE))
            return stack.getItem() instanceof ItemHoe;

        //API:Map the given strings a proper way.
        return !ItemStackUtils.isEmpty(stack)
                 && stack.getCount() >= 1
                 && stack.getItem().getToolClasses(stack).stream()
                      .filter(s -> getToolClass().getName().equalsIgnoreCase(s))
                      .map(ToolType::getToolType)
                      .filter(t -> t != ToolType.NONE)
                      .anyMatch(t -> ItemStackUtils.hasToolLevel(stack, t, getMinLevel(), getMaxLevel()));
    }

    @Override
    public int getCount()
    {
        return 1;
    }

    @Override
    public void setResult(@NotNull final ItemStack result)
    {
        this.result = result;
    }
}

package org.cyclops.integratedcrafting.api.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraftforge.common.util.Constants;
import org.cyclops.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import org.cyclops.integratedcrafting.api.recipe.PrioritizedRecipe;

/**
 * @author rubensworks
 */
public class CraftingJob {

    private final int id;
    private final int channel;
    private final PrioritizedRecipe recipe;
    private final IntList dependencyCraftingJobs;
    private final IntList dependentCraftingJobs;

    public CraftingJob(int id, int channel, PrioritizedRecipe recipe) {
        this.id = id;
        this.channel = channel;
        this.recipe = recipe;
        this.dependencyCraftingJobs = new IntArrayList();
        this.dependentCraftingJobs = new IntArrayList();
    }

    public int getId() {
        return id;
    }

    public int getChannel() {
        return this.channel;
    }

    public PrioritizedRecipe getRecipe() {
        return this.recipe;
    }

    public IntList getDependencyCraftingJobs() {
        return dependencyCraftingJobs;
    }

    public IntList getDependentCraftingJobs() {
        return dependentCraftingJobs;
    }

    public void addDependency(CraftingJob dependency) {
        dependencyCraftingJobs.add(dependency.getId());
        dependency.dependentCraftingJobs.add(this.getId());
    }

    public static NBTTagCompound serialize(CraftingJob craftingJob) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", craftingJob.id);
        tag.setInteger("channel", craftingJob.channel);
        tag.setIntArray("priorities", craftingJob.recipe.getPriorities());
        tag.setTag("recipeDefinition", IRecipeDefinition.serialize(craftingJob.recipe.getRecipe()));
        tag.setTag("dependencies", new NBTTagIntArray(craftingJob.getDependencyCraftingJobs()));
        tag.setTag("dependents", new NBTTagIntArray(craftingJob.getDependentCraftingJobs()));
        return tag;
    }

    public static CraftingJob deserialize(NBTTagCompound tag) {
        if (!tag.hasKey("id", Constants.NBT.TAG_INT)) {
            throw new IllegalArgumentException("Could not find an id entry in the given tag");
        }
        if (!tag.hasKey("channel", Constants.NBT.TAG_INT)) {
            throw new IllegalArgumentException("Could not find a channel entry in the given tag");
        }
        if (!tag.hasKey("priorities", Constants.NBT.TAG_INT_ARRAY)) {
            throw new IllegalArgumentException("Could not find a priorities entry in the given tag");
        }
        if (!tag.hasKey("recipeDefinition", Constants.NBT.TAG_COMPOUND)) {
            throw new IllegalArgumentException("Could not find a recipeDefinition entry in the given tag");
        }
        if (!tag.hasKey("dependencies", Constants.NBT.TAG_INT_ARRAY)) {
            throw new IllegalArgumentException("Could not find a dependencies entry in the given tag");
        }
        if (!tag.hasKey("dependents", Constants.NBT.TAG_INT_ARRAY)) {
            throw new IllegalArgumentException("Could not find a dependents entry in the given tag");
        }
        int id = tag.getInteger("id");
        int channel = tag.getInteger("channel");
        int[] priorities = tag.getIntArray("priorities");
        IRecipeDefinition recipeDefinition = IRecipeDefinition.deserialize(tag.getCompoundTag("recipeDefinition"));
        CraftingJob craftingJob = new CraftingJob(id, channel, new PrioritizedRecipe(recipeDefinition, priorities));
        for (int dependency : tag.getIntArray("dependencies")) {
            craftingJob.dependencyCraftingJobs.add(dependency);
        }
        for (int dependent : tag.getIntArray("dependents")) {
            craftingJob.dependentCraftingJobs.add(dependent);
        }
        return craftingJob;
    }

    @Override
    public String toString() {
        return String.format("[Crafting Job %s]", getId());
    }
}

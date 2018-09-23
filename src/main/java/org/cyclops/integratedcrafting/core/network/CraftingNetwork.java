package org.cyclops.integratedcrafting.core.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.cyclops.integratedcrafting.api.crafting.ICraftingInterface;
import org.cyclops.integratedcrafting.api.network.ICraftingNetwork;
import org.cyclops.integratedcrafting.api.recipe.IRecipeIndexModifiable;
import org.cyclops.integratedcrafting.api.recipe.PrioritizedRecipe;
import org.cyclops.integratedcrafting.core.RecipeIndexDefault;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;

import java.util.Map;
import java.util.Set;

/**
 * A crafting handler network with multiple channels.
 * @author rubensworks
 */
public class CraftingNetwork implements ICraftingNetwork {

    private final Set<ICraftingInterface> allCraftingInterfaces = Sets.newHashSet();
    private final TIntObjectMap<Set<ICraftingInterface>> craftingInterfaces = new TIntObjectHashMap<>();
    private final Map<PrioritizedRecipe, ICraftingInterface> allRecipeCraftingInterfaces = Maps.newIdentityHashMap();
    private final TIntObjectMap<Map<PrioritizedRecipe, ICraftingInterface>> recipeCraftingInterfaces = new TIntObjectHashMap<>();
    private final IRecipeIndexModifiable allRecipesIndex = new RecipeIndexDefault();
    private final TIntObjectMap<IRecipeIndexModifiable> recipeIndexes = new TIntObjectHashMap<>();

    @Override
    public int[] getChannels() {
        return craftingInterfaces.keys();
    }

    @Override
    public Set<ICraftingInterface> getCraftingInterfaces(int channel) {
        if (channel == IPositionedAddonsNetwork.WILDCARD_CHANNEL) {
            return allCraftingInterfaces;
        }
        Set<ICraftingInterface> craftingInterfaces = this.craftingInterfaces.get(channel);
        if (craftingInterfaces == null) {
            craftingInterfaces = Sets.newHashSet();
            this.craftingInterfaces.put(channel, craftingInterfaces);
        }
        return craftingInterfaces;
    }

    @Override
    public Map<PrioritizedRecipe, ICraftingInterface> getRecipeCraftingInterfaces(int channel) {
        if (channel == IPositionedAddonsNetwork.WILDCARD_CHANNEL) {
            return allRecipeCraftingInterfaces;
        }
        Map<PrioritizedRecipe, ICraftingInterface> recipeCraftingInterfaces = this.recipeCraftingInterfaces.get(channel);
        if (recipeCraftingInterfaces == null) {
            recipeCraftingInterfaces = Maps.newIdentityHashMap();
            this.recipeCraftingInterfaces.put(channel, recipeCraftingInterfaces);
        }
        return recipeCraftingInterfaces;
    }

    @Override
    public IRecipeIndexModifiable getRecipeIndex(int channel) {
        if (channel == IPositionedAddonsNetwork.WILDCARD_CHANNEL) {
            return allRecipesIndex;
        }
        IRecipeIndexModifiable recipeIndex = this.recipeIndexes.get(channel);
        if (recipeIndex == null) {
            recipeIndex = new RecipeIndexDefault();
            this.recipeIndexes.put(channel, recipeIndex);
        }
        return recipeIndex;
    }

    @Override
    public boolean addCraftingInterface(int channel, ICraftingInterface craftingInterface) {
        // Only process deeper indexes if the interface was not yet present
        if (getCraftingInterfaces(channel).add(craftingInterface)) {
            allCraftingInterfaces.add(craftingInterface);
            IRecipeIndexModifiable recipeIndex = getRecipeIndex(channel);
            Map<PrioritizedRecipe, ICraftingInterface> recipeCraftingInterfaces = getRecipeCraftingInterfaces(channel);
            for (PrioritizedRecipe recipe : craftingInterface.getRecipes()) {
                // Save the recipes in the index
                recipeIndex.addRecipe(recipe);
                allRecipesIndex.addRecipe(recipe);
                // Save a mapping from each of the recipes to this crafting interface
                recipeCraftingInterfaces.put(recipe, craftingInterface);
                allRecipeCraftingInterfaces.put(recipe, craftingInterface);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeCraftingInterface(int channel, ICraftingInterface craftingInterface) {
        // Only process deeper indexes if the interface was present
        if (getCraftingInterfaces(channel).remove(craftingInterface)) {
            allCraftingInterfaces.remove(craftingInterface);
            IRecipeIndexModifiable recipeIndex = getRecipeIndex(channel);
            Map<PrioritizedRecipe, ICraftingInterface> recipeCraftingInterfaces = getRecipeCraftingInterfaces(channel);
            for (PrioritizedRecipe recipe : craftingInterface.getRecipes()) {
                // Remove the recipes from the index
                recipeIndex.removeRecipe(recipe);
                allRecipesIndex.removeRecipe(recipe);
                // Remove the mappings from each of the recipes to this crafting interface
                recipeCraftingInterfaces.remove(recipe, craftingInterface);
                allRecipeCraftingInterfaces.remove(recipe, craftingInterface);
            }

            // Try cleaning up the channel
            cleanupChannelIfEmpty(channel);
            return true;
        }
        return false;
    }

    protected void cleanupChannelIfEmpty(int channel) {
        Set<ICraftingInterface> craftingInterfaces = this.craftingInterfaces.get(channel);
        if (craftingInterfaces != null && craftingInterfaces.isEmpty()) {
            this.craftingInterfaces.remove(channel);
            this.recipeIndexes.remove(channel);
            this.recipeCraftingInterfaces.remove(channel);
        }
    }
}

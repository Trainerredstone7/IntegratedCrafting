package org.cyclops.integratedcrafting;

import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.cyclopscore.config.ConfigurableTypeCategory;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfig;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.tracking.Analytics;
import org.cyclops.cyclopscore.tracking.Versions;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    /**
     * The current mod version, will be used to check if the player's config isn't out of date and
     * warn the player accordingly.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "Config version for " + Reference.MOD_NAME +".\nDO NOT EDIT MANUALLY!")
    public static String version = Reference.MOD_VERSION;

    /**
     * If the debug mode should be enabled. @see Debug
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "Set 'true' to enable development debug mode. This will result in a lower performance!", requiresMcRestart = true)
    public static boolean debug = false;

    /**
     * If the recipe loader should crash when finding invalid recipes.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If the recipe loader should crash when finding invalid recipes.", requiresMcRestart = true)
    public static boolean crashOnInvalidRecipe = false;

    /**
     * If mod compatibility loader should crash hard if errors occur in that process.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If mod compatibility loader should crash hard if errors occur in that process.", requiresMcRestart = true)
    public static boolean crashOnModCompatCrash = false;

    /**
     * If an anonymous mod startup analytics request may be sent to our analytics service.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    /**
     * If the version checker should be enabled.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    /**
     * The minimal update frequency in ticks to use for crafting interfaces.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.MACHINE, comment = "The minimal update frequency in ticks to use for crafting interfaces.", minimalValue = 1)
    public static int minCraftingInterfaceUpdateFreq = 5;

    /**
     * If the crafting interface should validate recipes on insertion.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.MACHINE, comment = "If the crafting interface should validate recipes on insertion.", isCommandable = true)
    public static boolean validateRecipesCraftingInterface = true;

    /**
     * The maximum amount of crafting jobs that could be scheduled within one crafting interface without being started.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.MACHINE, comment = "The maximum amount of crafting jobs that could be scheduled within one crafting interface without being started", minimalValue = 1, isCommandable = true)
    public static int maxPendingCraftingJobs = 256;

    /**
     * The base energy usage for the crafting writer.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the crafting writer.", minimalValue = 0)
    public static int craftingWriterBaseConsumption = 1;

    /**
     * The base energy usage for the crafting interface per crafting job being processed.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the crafting interface per crafting job being processed.", minimalValue = 0)
    public static int interfaceCraftingBaseConsumption = 5;

    /**
     * The type of this config.
     */
    public static ConfigurableType TYPE = ConfigurableType.DUMMY;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(IntegratedCrafting._instance, true, "general", null, GeneralConfig.class);
    }

    @Override
    public void onRegistered() {
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_INVALID_RECIPE, GeneralConfig.crashOnInvalidRecipe);
        getMod().putGenericReference(ModBase.REFKEY_DEBUGCONFIG, GeneralConfig.debug);
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_MODCOMPAT_CRASH, GeneralConfig.crashOnModCompatCrash);

        if(analytics) {
            Analytics.registerMod(getMod(), Reference.GA_TRACKING_ID);
        }
        if(versionChecker) {
            Versions.registerMod(getMod(), IntegratedCrafting._instance, Reference.VERSION_URL);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}

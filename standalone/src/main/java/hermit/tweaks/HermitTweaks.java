package hermit.tweaks;

import basemod.BaseMod;
import basemod.EasyConfigPanel;
import basemod.abstracts.CustomUnlockBundle;
import basemod.extension.BaseModEx;
import basemod.extension.interfaces.PostReloadPrefsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.SetUnlocksSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hermit.cards.*;
import hermit.characters.hermit;
import hermit.relics.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static com.megacrit.cardcrawl.unlock.AbstractUnlock.UnlockType.RELIC;
import static hermit.characters.hermit.Enums.HERMIT;

@SpireInitializer
public class HermitTweaks implements PostInitializeSubscriber, PostReloadPrefsSubscriber,
        EditCharactersSubscriber, EditStringsSubscriber, SetUnlocksSubscriber {
    public static final Color MAIN_COLOR = Color.valueOf("#8C5A2F");
    public static final Color TINY_COLOR = Color.valueOf("#ED9D26");
    public static final Color RELIC_COLOR = Color.valueOf("#FFBB33");
    public static final ModInfo modInfo;
    public static final Logger logger;

    static {
        modInfo = Arrays.stream(Loader.MODINFOS)
                .filter(modInfo -> modInfo.ID.equals("HermitTweaks"))
                .findFirst().orElseThrow(() -> new RuntimeException("Failed to load ModInfo"));

        logger = LogManager.getLogger(HermitTweaks.class.getName());
    }

    public HermitTweaks() {
        BaseMod.subscribe(this);
        logger.info("Subscribed to BaseMod");
    }

    public static void initialize() {
        new HermitTweaks();
    }

    @Override
    public void receivePostInitialize() {
        Texture badge = ImageMaster.loadImage("hermit/tweaks/images/ModBadge.png");
        BaseMod.registerModBadge(badge, modInfo.Name, Arrays.toString(modInfo.Authors),
                modInfo.Description, new Config());
        if (Config.replaceSplashScreen) {
            BaseMod.playerPortraitMap.replace(HERMIT, "hermit/tweaks/images/HermitSelect.png");
        }
    }

    @Override
    public void receivePostReloadPrefs() {
        CardLibrary.getCardList(hermit.Enums.LIBRARY_COLOR).forEach(card -> {
            if (UnlockTracker.isCardSeen(card.cardID)) card.isSeen = true;
        });
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.playerSelectButtonMap.replace(HERMIT, "hermit/tweaks/images/HermitButton.png");
        BaseMod.customModeCharacterButtonMap.replace(HERMIT, "hermit/tweaks/images/CustomButton.png");
        BaseModEx.replaceAllColors(hermit.Enums.COLOR_YELLOW, MAIN_COLOR);
        BaseModEx.replaceBackColor(hermit.Enums.COLOR_YELLOW, TINY_COLOR);
        BaseModEx.replaceFrameOutlineColor(hermit.Enums.COLOR_YELLOW, RELIC_COLOR);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, "hermit/tweaks/strings/UIStrings.json");
    }

    @Override
    public void receiveSetUnlocks() {
        UnlockTracker.addCharacter("Hermit");
        CustomUnlockBundle bundle1 = new CustomUnlockBundle(LoneWolf.ID, FullyLoaded.ID, Showdown.ID);
        CustomUnlockBundle bundle2 = new CustomUnlockBundle(RELIC, BartenderGlass.ID, Spyglass.ID, RedScarf.ID);
        CustomUnlockBundle bundle3 = new CustomUnlockBundle(CursedWeapon.ID, BlackWind.ID, Purgatory.ID);
        CustomUnlockBundle bundle4 = new CustomUnlockBundle(RELIC, Horseshoe.ID, CharredGlove.ID, PetGhost.ID);
        CustomUnlockBundle bundle5 = new CustomUnlockBundle(Reprieve.ID, FromBeyond.ID, DeadMansHand.ID);
        BaseMod.addUnlockBundle(bundle1, HERMIT, 0);
        BaseMod.addUnlockBundle(bundle2, HERMIT, 1);
        BaseMod.addUnlockBundle(bundle3, HERMIT, 2);
        BaseMod.addUnlockBundle(bundle4, HERMIT, 3);
        BaseMod.addUnlockBundle(bundle5, HERMIT, 4);

        if (!UnlockTracker.isCharacterLocked("Hermit")) return;
        if (UnlockTracker.unlockProgress.getInteger("WATCHERTotalScore", 0) > 0) {
            BaseModEx.unlockCharacter(HERMIT);
        }
        if (UnlockTracker.isCardSeen("hermit:Reprieve")) {
            BaseModEx.unlockAllBundles(HERMIT);
        }
    }

    public static class Config extends EasyConfigPanel {
        public static boolean increasedMaxHP = true;
        public static boolean renameConcentrate = true;
        public static boolean replaceSplashScreen = true;

        public Config() {
            super(modInfo.ID, CardCrawlGame.languagePack.getUIString(
                    String.format("%s:Config", modInfo.ID)), modInfo.ID);
        }
    }
}

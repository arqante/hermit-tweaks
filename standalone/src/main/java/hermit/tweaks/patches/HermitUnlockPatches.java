package hermit.tweaks.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import com.megacrit.cardcrawl.ui.buttons.ReturnToMenuButton;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.unlock.UnlockCharacterScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
import static hermit.characters.hermit.Enums.HERMIT;
import static hermit.tweaks.HermitTweaks.modInfo;

public class HermitUnlockPatches {

    //Patches the Death and Victory screens to open the unlock screen for the Hermit
    //Victory screen patch is needed to account for unlocking after an act 4 victory
    @SpirePatch2(clz = DeathScreen.class, method = "update")
    @SpirePatch2(clz = VictoryScreen.class, method = "update")
    public static class UnlockScreen {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert() {
            if (!Settings.isDemo && !Settings.isDailyRun && AbstractDungeon.unlocks.isEmpty()
                    && UnlockTracker.isCharacterLocked("Hermit")
                    && player.chosenClass == PlayerClass.WATCHER) {
                AbstractDungeon.unlocks.add(new HermitUnlock());
                AbstractDungeon.unlockScreen.open(AbstractDungeon.unlocks.remove(0));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                MethodCallMatcher matcher = new MethodCallMatcher(ReturnToMenuButton.class, "hide");
                return LineFinder.findAllInOrder(patchTarget, matcher);
            }
        }
    }

    //Replaces the Death screen's "Main Menu" button text with "Unlock" when unlocking the Hermit
    @SpirePatch2(clz = ReturnToMenuButton.class, method = "appear")
    public static class UnlockButton {
        @SuppressWarnings("StringEquality")
        public static void Prefix(@ByRef String[] label) {
            String[] TEXT = CardCrawlGame.languagePack.getUIString("DeathScreen").TEXT;
            if (label[0] == TEXT[37] && UnlockTracker.isCharacterLocked("Hermit")
                    && player.chosenClass == PlayerClass.WATCHER) {
                label[0] = TEXT[40];
            }
        }
    }

    //Fixes the position of the "New Character" banner (for some reason appears lower than should be)
    @SpirePatch2(clz = UnlockCharacterScreen.class, method = "open")
    public static class BannerPosition {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(AbstractUnlock unlock) {
            if (!unlock.key.equals("Hermit")) return SpireReturn.Continue();

            AbstractDungeon.dynamicBanner.appearInstantly(
                    Settings.HEIGHT / 2.0F + 320.0F * Settings.scale,
                    CardCrawlGame.languagePack.getUIString("UnlockCharacterScreen").TEXT[3]
            );
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                MethodCallMatcher matcher = new MethodCallMatcher(DynamicBanner.class, "appearInstantly");
                return LineFinder.findInOrder(patchTarget, matcher);
            }
        }
    }

    //Replaces the regular button with the locked one in the Select screen
    @SpirePatch2(clz = BaseMod.class, method = "generateCharacterOptions")
    public static class LockedButton {
        @SpireInsertPatch(locator = Locator.class, localvars = {"character", "option"})
        public static void Insert(AbstractPlayer character, @ByRef CharacterOption[] option) {
            if (character.chosenClass == HERMIT && UnlockTracker.isCharacterLocked("Hermit")) {
                option[0] = new CharacterOption(CardCrawlGame.characterManager
                        .recreateCharacter(character.chosenClass));
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                MethodCallMatcher matcher = new MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(patchTarget, matcher);
            }
        }
    }

    //In the Select screen, adds a tooltip explaining how to unlock the character
    @SpirePatch2(clz = CharacterOption.class, method = "updateHitbox")
    public static class LockedDescription {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(CharacterOption __instance) {
            if (__instance.hb.hovered && __instance.locked && __instance.c.chosenClass == HERMIT) {
                TipHelper.renderGenericTip(
                        (float) InputHelper.mX + 70.0F * Settings.xScale,
                        (float) InputHelper.mY - 10.0F * Settings.scale,
                        CharacterOption.TEXT[0], CardCrawlGame.languagePack
                                .getUIString(String.format("%s:CharacterOption", modInfo.ID))
                                .TEXT[Settings.language.ordinal()]
                );
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                Matcher matcher = new FieldAccessMatcher(CharacterOption.class, "locked");
                return LineFinder.findInOrder(patchTarget, matcher);
            }
        }
    }

    private static class HermitUnlock extends AbstractUnlock {
        public HermitUnlock() {
            this.type = UnlockType.CHARACTER;
            this.key = "Hermit";
            this.title = "Hermit";
        }

        @Override
        public void onUnlockScreenOpen() {
            this.player = CardCrawlGame.characterManager.getCharacter(HERMIT);
            this.player.drawX = (float) Settings.WIDTH / 2.0F - 20.0F * Settings.scale;
            this.player.drawY = (float) Settings.HEIGHT / 2.0F - 150.0F * Settings.scale;
        }
    }
}

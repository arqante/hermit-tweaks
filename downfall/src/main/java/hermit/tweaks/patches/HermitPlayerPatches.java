package hermit.tweaks.patches;

import basemod.AutoAdd;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hermit.HermitMod;
import hermit.cards.AbstractHermitCard;
import hermit.characters.hermit;
import hermit.tweaks.HermitTweaks.Config;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import static hermit.tweaks.HermitTweaks.logger;

public class HermitPlayerPatches {

    @SpirePatch2(clz = hermit.class, method = "getLoadout")
    public static class IncreasedMaxHP {
        public static SpireReturn<CharSelectInfo> Prefix(hermit __instance) {
            int maxHP = Config.increasedMaxHP ? 77 : 75;

            return SpireReturn.Return(new CharSelectInfo(
                    CardCrawlGame.languagePack.getCharacterString("hermit:hermit").NAMES[0],
                    CardCrawlGame.languagePack.getCharacterString("hermit:hermit").TEXT[0],
                    maxHP, maxHP, 0, 99, 5, __instance,
                    __instance.getStartingRelics(), __instance.getStartingDeck(), false
            ));
        }
    }

    @SpirePatch2(clz = hermit.class, method = "doCharSelectScreenSelectEffect")
    public static class SelectSoundPitch {
        public static SpireReturn<Void> Prefix() {
            CardCrawlGame.sound.playA(HermitMod.makeID("GUN1"), MathUtils.random(-0.2F, 0.2F));
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
            return SpireReturn.Return();
        }
    }

    //Makes all Hermit's cards unseen
    @SpirePatch2(clz = HermitMod.class, method = "receiveEditCards")
    public static class UnseeCards {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert() {
            new AutoAdd("downfall").packageFilter(AbstractHermitCard.class).cards();
            logger.info("Done adding cards!");
            return SpireReturn.Return();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                Matcher matcher = new MethodCallMatcher(HermitMod.class, "autoAddCards");
                return LineFinder.findInOrder(patchTarget, matcher);
            }
        }
    }

    //Insert after Memento (in the base game, starting relics are always seen and unlocked)
    @SpirePatch2(clz = HermitMod.class, method = "receiveEditRelics")
    public static class UnseeRelics {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert() {
            logger.info("Done adding relics!");
            return SpireReturn.Return();
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                Matcher matcher = new MethodCallMatcher(UnlockTracker.class, "markRelicAsSeen");
                return new int[] {LineFinder.findAllInOrder(patchTarget, matcher)[1]};
            }
        }
    }
}

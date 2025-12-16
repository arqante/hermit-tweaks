package hermit.tweaks.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipTracker;
import hermit.HermitMod;
import hermit.cards.AbstractHermitCard;
import hermit.util.HermitTutorials;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.HashMap;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
import static hermit.characters.hermit.Enums.HERMIT;

//These patches allow Hermit's tutorial to show more than once
public class HermitTutorialPatches {

    @SpirePatch2(clz = TipTracker.class, method = "refresh")
    public static class AddHermitTip {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert() {
            TipTracker.tips.put("HERMIT_TIP", TipTracker.pref.getBoolean("HERMIT_TIP", false));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                Matcher matcher = new MethodCallMatcher(HashMap.class, "get");
                return LineFinder.findInOrder(patchTarget, matcher);
            }
        }
    }

    @SpirePatch2(clz = HermitMod.class, method = "receiveOnBattleStart")
    public static class ShowTutorial {
        public static SpireReturn<Void> Prefix() {
            if (player.chosenClass == HERMIT && !TipTracker.tips.get("HERMIT_TIP")) {
                AbstractDungeon.ftue = new HermitTutorials();
                TipTracker.neverShowAgain("HERMIT_TIP");
            }
            HermitMod.tackybypass = true;
            AbstractHermitCard.deadOnThisTurn.clear();
            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = HermitTutorials.class, method = SpirePatch.CONSTRUCTOR)
    public static class ReplaceImages {
        public static void Postfix(HermitTutorials __instance) {
            ReflectionHacks.setPrivate(__instance, HermitTutorials.class, "img1",
                    ImageMaster.loadImage("hermit/tweaks/images/HermitTip-1.png"));
            ReflectionHacks.setPrivate(__instance, HermitTutorials.class, "img2",
                    ImageMaster.loadImage("hermit/tweaks/images/HermitTip-2.png"));
        }
    }
}

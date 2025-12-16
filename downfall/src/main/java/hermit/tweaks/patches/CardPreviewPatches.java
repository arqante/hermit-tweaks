package hermit.tweaks.patches;

import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import hermit.cards.*;

public class CardPreviewPatches {

    @SpirePatch2(clz = TakeCover.class, method = SpirePatch.CONSTRUCTOR)
    public static class DefendPreview {
        public static void Postfix(CustomCard __instance) {
            __instance.cardsToPreview.updateCost(-1);
        }
    }

    @SpirePatch2(clz = Maintenance.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch2(clz = Showdown.class, method = SpirePatch.CONSTRUCTOR)
    public static class StrikePreview {
        public static void Postfix(CustomCard __instance) {
            __instance.cardsToPreview = new Strike_Hermit();
        }
    }

    @SpirePatch2(clz = FullyLoaded.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch2(clz = HighNoon.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch2(clz = Shortfuse.class, method = SpirePatch.CONSTRUCTOR)
    public static class StrikeDefendPreview {
        public static void Postfix(CustomCard __instance) {
            MultiCardPreview.add(__instance, new Strike_Hermit(), new Defend_Hermit());
        }
    }
}

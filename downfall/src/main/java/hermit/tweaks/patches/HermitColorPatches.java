package hermit.tweaks.patches;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.vfx.combat.ViolentAttackEffect;
import hermit.cards.NoHoldsBarred;
import hermit.characters.hermit;
import hermit.potions.BlackBile;
import hermit.potions.Eclipse;
import hermit.potions.Tonic;
import hermit.tweaks.HermitTweaks;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

public class HermitColorPatches {

    //Affects the character name color in the Statistics page
    @SpirePatch2(clz = hermit.class, method = "getCardRenderColor")
    public static class CardRenderColor {
        public static SpireReturn<Color> Prefix() {
            return SpireReturn.Return(Color.GOLD.cpy()); //without cpy() some text becomes invisible
        }
    }

    @SpirePatch2(clz = hermit.class, method = "getCardTrailColor")
    public static class CardTrailColor {
        public static SpireReturn<Color> Prefix() {
            return SpireReturn.Return(Color.GOLDENROD.cpy()); //just in case
        }
    }

    //This is only used when attacking the Heart in act 3
    @SpirePatch2(clz = hermit.class, method = "getSlashAttackColor")
    public static class SlashAttackColor {
        public static SpireReturn<Color> Prefix() {
            return SpireReturn.Return(Color.GOLD.cpy()); //just in case
        }
    }

    @SpirePatch2(clz = BlackBile.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch2(clz = Eclipse.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch2(clz = Tonic.class, method = SpirePatch.CONSTRUCTOR)
    public static class PotionOutlines {
        public static void Postfix(@ByRef Color[] ___labOutlineColor) {
            ___labOutlineColor[0] = HermitTweaks.RELIC_COLOR;
        }
    }

    //Changes the color of the slash effects to gold
    @SpirePatch2(clz = NoHoldsBarred.class, method = "use")
    public static class NoHoldsBarredPatch {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                private final String replacement = String.format(
                        "{ $3 = %s.GOLD; $_ = $proceed($$); }",
                        Color.class.getName()
                );

                @Override
                public void edit(NewExpr e) throws CannotCompileException {
                    if (e.getClassName().equals(ViolentAttackEffect.class.getName())) {
                        e.replace(replacement);
                    }
                }
            };
        }
    }
}

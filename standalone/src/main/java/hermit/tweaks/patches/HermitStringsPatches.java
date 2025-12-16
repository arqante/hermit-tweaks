package hermit.tweaks.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import hermit.HermitMod;
import hermit.powers.ComboPower;
import hermit.powers.HorrorPower;
import hermit.powers.SnipePower;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static hermit.tweaks.HermitTweaks.modInfo;

//Replaces English strings from the original mod with my own
public class HermitStringsPatches {
    private static boolean renameConcentrate;

    static {
        try {
            Properties defaults = new Properties();
            defaults.setProperty("renameConcentrate", "true");
            SpireConfig config = new SpireConfig(modInfo.ID, modInfo.ID, defaults);
            renameConcentrate = config.getBool("renameConcentrate");
        } catch (IOException e) {
            renameConcentrate = false;
        }
    }

    @SpirePatch2(clz = HermitMod.class, method = "GetLocString")
    public static class ReplaceStrings {
        public static SpireReturn<String> Prefix(String locCode, String name) {
            if (!locCode.equals("eng")) return SpireReturn.Continue();

            String strings;
            switch (name) {
                case "CardStrings":
                case "KeywordStrings":
                case "PowerStrings":
                case "RelicStrings":
                    strings = renameConcentrate ?
                            String.format("hermit/tweaks/strings/%sA.json", name) :
                            String.format("hermit/tweaks/strings/%sB.json", name);
                    break;
                case "PotionStrings":
                    strings = String.format("hermit/tweaks/strings/%s.json", name);
                    break;
                default:
                    return SpireReturn.Continue();
            }

            return SpireReturn.Return(Gdx.files.internal(strings)
                            .readString(String.valueOf(StandardCharsets.UTF_8)));
        }
    }

    @SpirePatch2(clz = ComboPower.class, method = "updateDescription")
    public static class ComboPowerPatch {
        public static void Postfix(ComboPower __instance) {
            if (Settings.language == Settings.GameLanguage.ENG) {
                __instance.description = (__instance.amount == 1) ? ComboPower.DESCRIPTIONS[0] :
                        ComboPower.DESCRIPTIONS[1] + __instance.amount + ComboPower.DESCRIPTIONS[2];
            }
        }
    }

    @SpirePatch2(clz = HorrorPower.class, method = "updateDescription")
    public static class HorrorPowerPatch {
        public static void Postfix(HorrorPower __instance) {
            if (Settings.language == Settings.GameLanguage.ENG) {
                __instance.description = (__instance.amount == 1) ? HorrorPower.DESCRIPTIONS[0] :
                        HorrorPower.DESCRIPTIONS[1] + __instance.amount + HorrorPower.DESCRIPTIONS[2];
            }
        }
    }

    @SpirePatch2(clz = SnipePower.class, method = "updateDescription")
    public static class SnipePowerPatch {
        public static void Postfix(SnipePower __instance) {
            if (Settings.language == Settings.GameLanguage.ENG) {
                __instance.description = (__instance.amount == 1) ? SnipePower.DESCRIPTIONS[0] :
                        SnipePower.DESCRIPTIONS[1] + __instance.amount + SnipePower.DESCRIPTIONS[2];
            }
        }
    }
}

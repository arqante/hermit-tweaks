package hermit.tweaks.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import downfall.downfallMod;
import downfall.downfallMod.otherPackagePaths;
import hermit.potions.Eclipse;
import hermit.powers.ComboPower;
import hermit.powers.HorrorPower;
import hermit.powers.SnipePower;
import hermit.relics.BartenderGlass;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static downfall.downfallMod.otherPackagePaths.PACKAGE_HERMIT;
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

    @SpirePatch2(clz = downfallMod.class, method = "makeLocalizationPath", paramtypez =
            {Settings.GameLanguage.class, String.class, otherPackagePaths.class})
    public static class ReplaceStrings {
        public static SpireReturn<String> Prefix(Settings.GameLanguage language,
                                                 String filename, otherPackagePaths otherPackage) {
            if (otherPackage != PACKAGE_HERMIT || language != Settings.GameLanguage.ENG) {
                return SpireReturn.Continue();
            }

            String strings;
            switch (filename) {
                case "CardStrings":
                case "PowerStrings":
                case "RelicStrings":
                    strings = renameConcentrate ?
                            String.format("hermit/tweaks/strings/%sA.json", filename) :
                            String.format("hermit/tweaks/strings/%sB.json", filename);
                    break;
                case "PotionStrings":
                    strings = String.format("hermit/tweaks/strings/%s.json", filename);
                    break;
                default:
                    return SpireReturn.Continue();
            }
            return SpireReturn.Return(strings);
        }
    }

    @SpirePatch2(clz = downfallMod.class, method = "loadModKeywords")
    public static class ReplaceKeywords {
        @SpireInsertPatch(locator = Locator.class, localvars = {"json", "lang"})
        public static SpireReturn<Void> Insert(@ByRef String[] json, String lang,
                                               otherPackagePaths otherPath) {
            if (otherPath != PACKAGE_HERMIT || !lang.equals("eng")) {
                return SpireReturn.Continue();
            }

            json[0] = renameConcentrate ?
                    Gdx.files.internal("hermit/tweaks/strings/KeywordStringsA.json")
                            .readString(String.valueOf(StandardCharsets.UTF_8)) :
                    Gdx.files.internal("hermit/tweaks/strings/KeywordStringsB.json")
                            .readString(String.valueOf(StandardCharsets.UTF_8));
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior patchTarget) throws CannotCompileException, PatchingException {
                Matcher matcher = new MethodCallMatcher(Gson.class, "fromJson");
                return LineFinder.findInOrder(patchTarget, matcher);
            }
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

    @SpirePatch2(clz = Eclipse.class, method = "initializeData")
    public static class EclipsePatch {
        public static void Postfix(Eclipse __instance) {
            if (Settings.language == Settings.GameLanguage.ENG) {
                __instance.description = (__instance.getPotency() == 1) ? Eclipse.DESCRIPTIONS[0] :
                        Eclipse.DESCRIPTIONS[1] + __instance.getPotency() + Eclipse.DESCRIPTIONS[2];

                __instance.tips.clear();
                __instance.tips.add(new PowerTip(__instance.name, __instance.description));
            }
        }
    }

    @SpirePatch2(clz = BartenderGlass.class, method = "getUpdatedDescription")
    public static class BartenderGlassPatch {
        public static SpireReturn<String> Prefix(BartenderGlass __instance) {
            if (Settings.language == Settings.GameLanguage.ENG) {
                return SpireReturn.Return(__instance.DESCRIPTIONS[0]);
            }
            return SpireReturn.Continue();
        }
    }
}

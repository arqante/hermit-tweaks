# Hermit Tweaks
Assorted tweaks for the Hermit to make them feel even more "vanilla".

### Requires:
- Steam version of [Slay the Spire](https://store.steampowered.com/app/646570/Slay_the_Spire/)
- [ModTheSpire](https://steamcommunity.com/sharedfiles/filedetails/?id=1605060445)
- [BaseMod](https://steamcommunity.com/sharedfiles/filedetails/?id=1605833019)
- [BaseModEx](https://steamcommunity.com/sharedfiles/filedetails/?id=3617683629)
- [StSLib](https://steamcommunity.com/sharedfiles/filedetails/?id=1609158507)
- [The Hermit](https://steamcommunity.com/sharedfiles/filedetails/?id=2137404781) or [Downfall](https://steamcommunity.com/sharedfiles/filedetails/?id=1610056683)

### Features:
- (English only.) Changed a lot of card descriptions as well as some relic, power and keyword descriptions to be closer 
to how they are usually written in the base game.
- Changed some of their "colors" from hot yellow to more gold and brown colors: the Card Library bar, their name in the Stats screen, relic and potion outlines in the Compendium, "tiny card" colors in the Run History, etc.
- Added a new Custom mode button (they didn't have any).
- The pitch of their select sound in now randomized like it is for the base game characters.
- Made their tutorial on "Dead On" recurring. Before, it used to be shown exactly once - after you install the mod and play as the Hermit for the first time, and that's it. Now it should be shown the first time you play as them on a new profile. I also updated the images with the new card descriptions.
- Ported their unlocks from Downfall to the standalone version. Additionally, the Hermit themselves are now locked until you complete a single run with the Watcher.
- (Optional.) Increased their max HP from 75 to 77 (71 -> 73 on Ascension 14+), so that there can be this nice progression of max HPs: Silent - 70, Watcher - 72, Defect - 75, Hermit - 77, Ironclad - 80. (Also the lucky 7s, kinda fits.)
- (Optional.) Renamed one of their keywords from "Concentrate" to "Aim", so that it doesn't overlap with the Silent's card "Concentrate". Other than the name, nothing's changed.
- And other changes I forgot to mention.

## Building
### Requirements:
- Java 8+
- Maven v3.9+
- [ModTheSpire](https://github.com/kiooeht/ModTheSpire) v3.30.3+
- [BaseMod](https://github.com/daviscook477/BaseMod) v5.56.0+
- [BaseModEx](https://github.com/arqante/BaseModEx) v1.2.0+
- [StSLib](https://github.com/kiooeht/StSLib) v2.12.0+
- [The Hermit](https://steamcommunity.com/sharedfiles/filedetails/?id=2137404781) v1.56+
- [Downfall](https://steamcommunity.com/sharedfiles/filedetails/?id=1610056683) v5.3.6+ (yes, you need both)

### How to build:
1. Clone this repository to a location of your choice.
2. Inside the root `pom.xml`, set the `steam.path` property to your `steamapps` folder.
3. Subscribe to all the required mods on Steam Workshop if you haven't done so already.
4. Run `mvn package`. The output `.jar` files should be inside the game's `mods` folder.

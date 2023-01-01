# Gensokyo Pixel Dungeon

This is heavily inspired by Shattered PD v1.3.1 (obviously), (old) TouhouPD and Tommorow's RougeNights. The game also borrow idea from some other forks (usually from TrashBox stuffs though). So check out TouhouPD, Summoning PD, RKPD2-DLC (now RK-Adventure), Too Cruel PD, RougeNights, etc.

# Progress 
(The source code, not the release beta/alpha/main game so some of this may not be in the apk you downloaded):
- 4 new characters, they are a based on the classic existing 4. Plus Sakuya who is (literally) broken, she's there because it's harder to remove her, you can pick her to handicap yourself since half her kit doesn't work.
![Heros list](https://github.com/GrampHoang/Gensokyo-pixel-dungeon/blob/main/github-readme-img/heros.PNG)
- All new character have their own unique subclasses, no armor ability
- A bunch more weapon to spice up the gameplay, most of them have special skill that you can gain "skill point" to use by just hitting the enemies, spread evenly between tiers (Currently T1, T2, T3 are done). They have lower drop rate though. Might nerf later, or change them into secret weapons. High Priority
![Weapons](https://github.com/GrampHoang/Gensokyo-pixel-dungeon/blob/main/github-readme-img/weaponlist.PNG)
![Skill demo](https://github.com/GrampHoang/Gensokyo-pixel-dungeon/blob/main/github-readme-img/weaponskill.png)
- New challenges: Lunatic - where all enemy have additional perk and skill, doesn't affect normal boss though. It require constant kiting and re-positioning so becareful.
- Alternative path (Currently up to 20 floors, last 5 are still Demon Hall): Gensokyo challenge. New enemies, new bosses. Not play tested enough, but on paper it should be harder in general but easier if you don't get gang up on. Read monster's description, dodge their skill and you should be fine. Except lots of repositioning though, and don't too rely on narrow pathway. I expect the first boss to be good enough (may need slight nerf), 2nd might be too hard (on Lunatic) and may take a few try to figure her out, third boss fight might take too long and 4th one can be a bit overwhelming. Medium Priority
- Ascension Run: Nothing, Gensokyo monster doesn't get buff by ascension, but killing them doesn't seem to reduce the curse down so it is still a challenge, albeit a bit different. Low priority

# Plan:
- Finish the first 4 reagion: mobs, bosses and items for now.
- Draw more stuffs, need to re-draw lots sprite from TouhouPD too.
- A lot more weapons, Currently doing T4, I'll start adding secret weapon after this.
- A few artifact (recycle useless gear into alchemy, bomb maker, recycle scroll into guarantee PoFlame or something lol). Some might have hidden upgrade
- A few more spell that can sygnergy with weapon skill and help adapt with new challenges.
- Maybe set effect (Like RougeNight), just maybe, to encourage keeping rings and artifact instead of just throw them away.
- NPCs and Description that hint the above things.
- Music (will compressed) and SFX, medium priority
- No new armor, I'm considering it but not sure what to implement.
- Rouge-like shop: On planning, doesn't seem feasible, very low priority.
- Collectible: seperated bag slot for these kind of items, they will be used to slightly buff stats, special effect for special occasion, consumable and hidden recipe. Though I need to somehow make the game scale harder to counter this power up, or make secret boss/hard side quest to get them, mhm... very low priority
- Badges/UI/About/Changelogs: A few idea, low priority
- Last 5 floors will be developed later.

# Problem with the game:
- It's unfinished, obviously
- I intended for this game to be slightly harder than SPD, this game focus a lot more on positioning because Gensokyo mobs wil try to make your position as bad as possible, kinda goes against SPD but we will have to see if it's too obnoxious or not
- Harder game also mean less player, if there is any at all xD 
- I also wanted the fun to come with exploration and figuring out hidden thing, but the difficulty might be a fun blocker in this aspect.


If there is any issue, you can use the feedback link inside the game.

# ShatteredPD
A Roguelike RPG, with randomly generated levels, items, enemies, and traps! Based on the [source code of Pixel Dungeon](https://github.com/00-Evan/pixel-dungeon-gradle), by [Watabou](https://www.watabou.ru).

Shattered Pixel Dungeon currently compiles for Android, iOS and Desktop platforms. It is available from [Google Play](https://play.google.com/store/apps/details?id=com.shatteredpixel.shatteredpixeldungeon), [the App Store](https://apps.apple.com/app/shattered-pixel-dungeon/id1563121109), and right here on [GitHub](https://github.com/00-Evan/shattered-pixel-dungeon/releases).

If you like this game, please consider [supporting me on Patreon](https://www.patreon.com/ShatteredPixel)!

There is an official blog for this project at [ShatteredPixel.com](https://www.shatteredpixel.com).

The game also has a translation project hosted on [Transifex](https://www.transifex.com/shattered-pixel/shattered-pixel-dungeon/).

Note that **this repository does not accept pull requests!** The code here is provided in hopes that others may find it useful for their own projects, not to allow community contribution. Issue reports of all kinds (bug reports, feature requests, etc.) are welcome.

If you'd like to work with the code, you can find the following guides in `/docs`:
- [Compiling for Android.](docs/getting-started-android.md)
    - **[If you plan to distribute on Google Play please read the end of this guide.](docs/getting-started-android.md#distributing-your-apk)**
- [Compiling for desktop platforms.](docs/getting-started-desktop.md)
- [Compiling for iOS.](docs/getting-started-ios.md)
- [Recommended changes for making your own mod.](docs/recommended-changes.md)

# Three Musket - Game Design Document

## Game Summary
   
   This game is a team deathmatch multiplayer 3V3 first-person shooter.

   The game will be action and strategy focused, fast-paced, and gameplay driven with less focus on story and characters.
   We plan to assign general roles to each character such tank, DPS, and support that varies in their weaponry and playstyle.
   The map will have walls, obstacles, and possibly vehicles as well as unique objectives to win the game.

   Communication between players will be a critical aspect of the game.
   Collaboration will be rewarded while preserving the possibility for one player to carry the game.
   An unique aspect of the game is the timeout mechanic where players are able to pause the game, similar to basketball, and discuss strategy and change their setups.

   The game will be initially developed for a single player before expanding to multiplayer, adding bots to fill the role of the opposing team.

## Genre
   
   Multiplayer FPS

## Inspiration
   
   Our inspiration is multiplayer FPS games with unique weapons such as CS:GO or Team Fortress 2.

   ### CS:GO

   Specifically, we are looking at Wingman and Deathmatch modes in CS:GO.
   We will further refine our team deathmatch game style with adding unique objectives to win the game.
   https://blog.counter-strike.net/index.php/about/

   ### Team Fortress 2

   Also, 8 roles in Team Fortress 2 - Scout, Soldier, Pyro, Demoman, Heavy, Engineer, Medic, Sniper, Spy.
   We plan to implement a simpler role system with unique weaponry and playstyles for each role.
   https://wiki.teamfortress.com/wiki/Team_Fortress_2

   ### Future Development
   For future development, we plan to add elements of strategy and teamwork, inspired by games like Deadlock and Spectre Divide.
   
## Gameplay
   
   * Projectile-heavy weaponry
     * Instead of hitscan, projectiles are used for most weapons
      * Overall low utility options (smoke, flash, grenade, etc.)
      * Hitscan weapons and utility options will be highly valuable
    * 3 roles with different weapon builds for each (builds can be changed during timeout)
      * Tank: choose from additional health and utility options
        * shield and supportive play / mobility and engagement
      * DPS: choose from long range and melee
        * long range for hitscan / melee for flanking
      * Support: choose from debuff and healing
        * debuff for aggressive engage / heal for supporting and stability
    * Timeouts (3 times per game)
      * each team can timeout in the middle of the battle to setup and edit their strats
    * Single player mode
      * Bots to fill the role of the opposing team
    * Future development: Co-op or 1V1 modes
      * RTS style FPS (consider Chess, Starcraft, or AOS)

## Developement

   * First deliverable: We have coded a crosshair, a gun shooting mechanism with collision detection, and some targets. The player is also able to jump and move around the map.
   * We use a scene graph were targets and bullets are nodes and when collided, the bullet disappears and the node also gets pruned.
   * For input we use addMapping on the WASD keys for 2D movement and spacebar for jumping. We use left click to listen for shooting.
   * For the crosshair we use a guiNode and use local translation to calculate the location for the middle of the screen.
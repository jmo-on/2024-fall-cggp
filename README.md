# Game Design Document
## 
1. Game Summary
   Our game is a Multiplayer FPS game that resembles popular games such as Spectre Divide, Deadlock, and Team Fortress.
   While the specifics of the game are yet to be decided, the general idea is that we will combine Real-time strategy and First-person shooter.
   The game will be action and strategy focused, fast-paced, and gameplay driven with little focus on story and characters.
   We plan to assign general roles to each character such tank, DPS, and support and a concise background story.
   The main setting is yet to be decided, but the maps will have walls, buildings, and vehicles as well as objectives to win the game.

   Communication between players will also be a critical aspect of the game and collaboration will be rewarded while preserving the possibility for one player to carry the game.
   An aspect that differentiates our games from other games is the timeout mechanic where players are able to pause the game, similar to basketball, and discuss strategy and change their setups. We are hoping this will encourage collaboration and the social aspect of the game.

3. Genre
   Multiplayer FPS
4. Inspiration
   Our inspiration is multiplayer FPS games with unique weapons such as CS:GO or Team Fortress 2.
   
5. Gameplay
    * Firearm-heavy 3V3 FPS: low utility options
    * Tank, DPS, Support: different weapon builds for each
        - Tank: vehicle (unique option for engagement)
        - DPS: choose from gun and melee (gun for long range, melee for flanking)
        - Support: choose from debuff and healing (debuff for agressive engage, heal for supporting)
    * Control 2 or more character per player like Spectre Divide
        - maybe cut down to 1V1 in this case - easier server handling
        - more like RTS style FPS
            fight only when engaged, timeouts, make dudes go to certain pos, sth like simulation too
    * Timeouts: each team can timeout in the middle of the battle to setup and edit their strats

6. Developement
   * First deliverable: We have coded a crosshair, a gun shooting mechanism with collision detection, and some targets. The player is also able to jump and move around the map.
   * We use a scene graph were targets and bullets are nodes and when collided, the bullet disappears and the node also gets pruned.
   * For input we use addMapping on the WASD keys for 2D movement and spacebar for jumping. We use left click to listen for shooting.
   * For the crosshair we use a guiNode and use local translation to calculate the location for the middle of the screen.
   *  

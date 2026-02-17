# Additional Additions Editor
This mod allows you to make new additions for [Legend of Dragoon: Severed Chains](https://legendofdragoon.org/projects/severed-chains).

### Important Note
**You need the [Additional Additions mod](https://github.com/Legend-of-Dragoon-Modding/Additional-Additions) to use the additions made with this mod!**

## How to Install
Download the mod from the releases page of this repository and put it in your Severed Chains `mods` folder.

## How to Use
1. Install the mod (see previous section for installation instructions)
2. Launch Severed Chains
3. Select "New Campaign" on the title screen
4. Select "Addition Editor" as the campaign type for your new campaign
5. Start the campaign and it will launch directly into the addition editor

## Creating New Additions
This section is a work in progress.

- totalFrames
  - The total number of frames for this hit
  - Usually `buttonDelay + buttonWindow + 1`
  - Increasing it past this amount will increase the amount of time after the button press
  - Decreasing it below `buttonDelay + buttonWindow + 1` will have no effect, it will still wait that long before continuing
  - If the animation is too short, the player will stay on the last frame of the animation until the hit finishes
- buttonDelay
  - The number of frames until the player has to press the addition button
  - Higher values make the button press later
- buttonWindow
  - The number of frames the player has to press the addition button before it's considered a miss
- moveFrames
  - How long it takes for the player to get to the enemy
  - Higher values take longer to get to the enemy
  - Retail additions have values for all hits, but it only seems to have an effect on the first hit
- knockback
  - The amount this hit pushes the enemy back
- cameraMovementX
  - The left-right camera movement
- cameraMovementZ
  - The forward-backward camera movement
- cameraMovementTicks
  - The number of frames it takes the camera to move
- damageMultiplier
  - The Percentage of the base damage this hit does
- sp
  - How much SP this hit gives

## Sharing Additions
Every addition you make will be saved to the `mods/additional-additions` directory in your Severed Chains directory as JSON files. You can share these files with others so they can use your custom additions! All they need is the Additional Additions mod.

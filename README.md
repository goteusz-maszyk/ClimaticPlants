![Compatible with Sinytra Connector](https://raw.githubusercontent.com/Sinytra/.github/refs/heads/main/badges/connector/cozy.svg)

**NOTE: while the mod is generally stable, you may encounter issues with config when updating the mod.**

# General

Some *improvements* to the plant growth. Inspired by [Demaning Saplings](https://modrinth.com/mod/demanding-saplings).

Well, now trying to plant a spruce on badlands or a cactus in freezing cold mountains will result in failure. Depending how much different the temperature is to the preferred one, the lower is the chance of growing.

This mod uses Minecraft's already existing biome temperature system and also a height mechanic for more dynamic values on higher altitudes.

If a sapling is placed on a temperature outside of its compatible range, it will perish into 1 of 3 different dead bushes, if the temperature is way too high for the sapling, it will become into a vanilla dead bush, if the temperature is slightly off, it will become a dead sapling, and if the temperature is way too cold, it will become a frozen bush.

## Plans
(the entries will be removed from the list as they are added)
- add frozen and burnt crops (right now they only stop growing)
- show how good the current climate is for the plant
- add rainfall/humidity checks
- mod support (farmer's delight and more)

## Config

All config values are explained in the file.
Check out `config/demanding_plants.json5`
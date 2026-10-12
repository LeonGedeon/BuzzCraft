# BuzzCraft Wiki

BuzzCraft adds **Snus** to your Minecraft server: a craftable, findable item that
heals you and grants temporary buffs — but use it too much and you'll build up
**tolerance** and start suffering withdrawal.

---

## Crafting

Snus is crafted from a mix of dried kelp, sugar and paper:

```
 T T T
 S P S
 T T T
```

* **T** = Dried Kelp
* **S** = Sugar
* **P** = Paper

This yields **4 Snus** per craft.

## Finding Snus in the world

You don't have to craft everything yourself:

* **Loot chests** — naturally generated chests (dungeons, structures, etc.) have a
  configurable chance to contain Snus.
* **Wild plants** — breaking grass, ferns and large ferns has a chance to drop Snus.

Both are configured in `config.yml` (see below).

## Using Snus

Hold a Snus and **right-click** to use it. You will:

* be healed, and
* receive a buff or debuff depending on your current **tolerance**.

## Tolerance & tiers

Every Snus you use raises your **tolerance**. Tolerance slowly **decays over time**,
so casual use is rewarded and spamming is punished. Your tier determines the effect:

| Tier | Tolerance | Effect on use |
|------|-----------|---------------|
| **Fresh** | 0 – 29 | Full heal + **Strength II** (30s) |
| **Tolerant** | 30 – 59 | Half heal + **Strength I** (15s) |
| **Dependent** | 60 – 89 | Small heal + brief **Strength I** + **Slowness** |
| **Withdrawal** | 90 – 100 | No heal — **Weakness**, **Nausea** and **Slowness** |

## Commands

| Command | Description |
|---------|-------------|
| `/snus` | Show the command list |
| `/snus stats` | Show your own Snus stats |
| `/snus stats <player>` | Show another player's Snus stats |
| `/snus leaderboard [page]` | Show the players who have taken the most Snus |
| `/snus scoreboard` | Toggle the personal scoreboard sidebar |

Commands also support tab-completion (subcommands, player names and page numbers).

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `buzzcraft.use` | everyone | Use Snus items |
| `buzzcraft.stats` | everyone | View your own stats |
| `buzzcraft.stats.others` | everyone | View other players' stats (implies `buzzcraft.stats`) |
| `buzzcraft.leaderboard` | everyone | View the leaderboard |
| `buzzcraft.scoreboard` | everyone | Toggle the scoreboard sidebar |
| `buzzcraft.admin` | operators | Grants all BuzzCraft permissions |

## Configuration (`config.yml`)

| Key | Default | Description |
|-----|---------|-------------|
| `healAmount` | `10` | Health healed per Snus at the Fresh tier (higher tiers heal a fraction) |
| `startingTolerance` | `0` | Tolerance a new player starts with |
| `maxTolerance` | `100` | Maximum reachable tolerance |
| `tolerancePerUse` | `15` | Tolerance gained per Snus used |
| `decayAmount` | `1` | Tolerance lost per decay interval |
| `decayIntervalSeconds` | `30` | Seconds between tolerance decay ticks |
| `tierThresholds.tolerant` | `30` | Minimum tolerance for the Tolerant tier |
| `tierThresholds.dependent` | `60` | Minimum tolerance for the Dependent tier |
| `tierThresholds.withdrawal` | `90` | Minimum tolerance for the Withdrawal tier |
| `showScoreboard` | `false` | Whether the sidebar is shown by default |
| `lootChestChance` | `0.1` | Chance (0.0–1.0) a loot chest contains Snus |
| `lootChestAmount` | `1` | Snus inserted when a loot chest roll succeeds |
| `wildPlantChance` | `0.05` | Chance (0.0–1.0) a wild plant drops Snus |
| `wildPlantAmount` | `1` | Snus dropped when a wild plant roll succeeds |
| `wildPlantMaterials` | grass/fern/tall grass/large fern | Blocks treated as wild plants |

## Data

Per-player data (Snus taken, tolerance, scoreboard preference) is stored in
`plugins/BuzzCraft/playerdata/<uuid>.yml`.

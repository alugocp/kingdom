import std/options
import kingdom/game
import kingdom/builtin/channels
import kingdom/entities/types
import kingdom/entities/unit
import kingdom/entities/ability
import kingdom/entities/signals
import kingdom/generation/manager

# Labels for mod content
const ABILITY_MOVE = "Move"

# Mod initialization procedure
proc initKingdomMod(game: Game): void {.exportc, dynlib.} =
    game.unitGeneration.addGenerator("test", proc (): Unit =
        let unit = newUnit()
        unit.name = "Vanilla mod unit"
        unit.desc = some("Imported via modding!")
        return unit
    )
    game.abilityGeneration.addGenerator(ABILITY_MOVE, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_MOVE
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            echo("Move!")
        )
        return ability
    )

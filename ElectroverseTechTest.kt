/*
 * Electroverse Intern Tech Test — Kotlin starter
 * ------------------------------------------------
 * Paste this whole file into https://play.kotlinlang.org and press Run.
 * No installs, no dependencies — it works in the browser as-is.
 *
 * `rawChargers` below is a loosely-typed list of maps. Think of it as the
 * messy JSON a mobile app receives from a server before it is decoded.
 * Your job for Part 1 is to turn it into clean, typed, trustworthy data and
 * then answer some questions with it. Look for the TODO markers.
 *
 * Notes on the data (read carefully — these edge cases are deliberate):
 *   - "pricePerKwh" can be null, or the key can be missing entirely.
 *   - "powerKw" can be 0 (a charger that is listed but delivering no power).
 *   - "status" is one of: AVAILABLE, IN_USE, OUT_OF_SERVICE, UNKNOWN.
 */

val rawChargers: List<Map<String, Any?>> = listOf(
    mapOf("id" to "ev-001", "name" to "Old Street Roundabout",      "operator" to "Electroverse",         "distanceMeters" to 120,  "connectorType" to "CCS",     "powerKw" to 150, "pricePerKwh" to 0.49, "status" to "AVAILABLE"),
    mapOf("id" to "ev-002", "name" to "Shoreditch High St Car Park","operator" to "PodPoint",             "distanceMeters" to 430,  "connectorType" to "Type 2",  "powerKw" to 22,  "pricePerKwh" to 0.39, "status" to "IN_USE"),
    mapOf("id" to "ev-003", "name" to "Moorgate Station",           "operator" to "BP Pulse",             "distanceMeters" to 980,  "connectorType" to "CCS",     "powerKw" to 50,  "pricePerKwh" to 0.59, "status" to "AVAILABLE"),
    mapOf("id" to "ev-004", "name" to "Barbican Centre",            "operator" to "Electroverse",         "distanceMeters" to 1250, "connectorType" to "CHAdeMO", "powerKw" to 50,  "pricePerKwh" to 0.52, "status" to "OUT_OF_SERVICE"),
    mapOf("id" to "ev-005", "name" to "Liverpool Street Multi-Storey","operator" to "Ionity",             "distanceMeters" to 1610, "connectorType" to "CCS",     "powerKw" to 350, "pricePerKwh" to 0.74, "status" to "AVAILABLE"),
    mapOf("id" to "ev-006", "name" to "Angel Islington",            "operator" to "Shell Recharge",       "distanceMeters" to 2040, "connectorType" to "Type 2",  "powerKw" to 7,   "pricePerKwh" to 0.45, "status" to "AVAILABLE"),
    mapOf("id" to "ev-007", "name" to "Spitalfields Market",        "operator" to "Electroverse",         "distanceMeters" to 760,  "connectorType" to "CCS",     "powerKw" to 120, "pricePerKwh" to null, "status" to "AVAILABLE"),
    mapOf("id" to "ev-008", "name" to "Hoxton Square",              "operator" to "PodPoint",             "distanceMeters" to 540,  "connectorType" to "Type 2",  "powerKw" to 22,  "pricePerKwh" to 0.41, "status" to "UNKNOWN"),
    mapOf("id" to "ev-009", "name" to "Canary Wharf East",          "operator" to "BP Pulse",             "distanceMeters" to 6820, "connectorType" to "CCS",     "powerKw" to 150, "pricePerKwh" to 0.63, "status" to "AVAILABLE"),
    mapOf("id" to "ev-010", "name" to "Clerkenwell Green",          "operator" to "Electroverse",         "distanceMeters" to 1490, "connectorType" to "CCS",     "powerKw" to 0,   "pricePerKwh" to 0.55, "status" to "AVAILABLE"),
    mapOf("id" to "ev-011", "name" to "Bethnal Green Depot",        "operator" to "Octopus Electroverse", "distanceMeters" to 3180, "connectorType" to "CHAdeMO", "powerKw" to 50,                         "status" to "IN_USE"),
    mapOf("id" to "ev-012", "name" to "Finsbury Park Interchange",  "operator" to "Shell Recharge",       "distanceMeters" to 4750, "connectorType" to "CCS",     "powerKw" to 175, "pricePerKwh" to 0.68, "status" to "AVAILABLE")
)

// ---------------------------------------------------------------------------
// TODO 1 — Model the data.
// Define a Charger type (and any supporting types, e.g. for status) that
// represents a charger safely. Decide how to represent a missing/unknown price.
// ---------------------------------------------------------------------------

enum class ChargerStatus {
    AVAILABLE,
    IN_USE,
    OUT_OF_SERVICE,
    UNKNOWN;

    companion object {
        fun fromRaw(raw: String?): ChargerStatus = when (raw?.uppercase()) {
            "AVAILABLE" -> AVAILABLE
            "IN_USE" -> IN_USE
            "OUT_OF_SERVICE" -> OUT_OF_SERVICE
            else -> UNKNOWN
        }
    }
}

// Separate type for cost so we never guess a price when it's missing
sealed class EstimatedCost {
    data class Known(val pounds: Double) : EstimatedCost()
    object Unknown : EstimatedCost()
}

data class Charger(
    val id: String,
    val name: String,
    val operator: String,
    val distanceMeters: Int,
    val connectorType: String,
    val powerKw: Int,
    val pricePerKwh: Double?, // null = missing or unknown
    val status: ChargerStatus,
)

// ---------------------------------------------------------------------------
// TODO 2 — Map rawChargers into a List<Charger>.
// Decide what to do with bad or missing values rather than crashing.
// ---------------------------------------------------------------------------

fun toInt(value: Any?): Int? = when (value) {
    is Int -> value
    is Number -> value.toInt()
    else -> null
}

fun toPrice(value: Any?): Double? = when (value) {
    null -> null
    is Number -> value.toDouble().takeIf { it >= 0 }
    else -> null
}

val chargers: List<Charger> = rawChargers.mapNotNull { row ->
    val id = row["id"] as? String ?: return@mapNotNull null
    val name = row["name"] as? String ?: return@mapNotNull null
    val distance = toInt(row["distanceMeters"]) ?: return@mapNotNull null

    Charger(
        id = id,
        name = name,
        operator = row["operator"] as? String ?: "Unknown",
        distanceMeters = distance,
        connectorType = row["connectorType"] as? String ?: "Unknown",
        powerKw = toInt(row["powerKw"]) ?: 0,
        pricePerKwh = toPrice(row["pricePerKwh"]),
        status = ChargerStatus.fromRaw(row["status"] as? String),
    )
}

// ---------------------------------------------------------------------------
// TODO 3 — availableChargers(minPowerKw: Int): List<Charger>
// Return only chargers a driver could actually use right now, fast enough to
// meet a minimum power requirement, sorted nearest-first.
// ---------------------------------------------------------------------------

fun availableChargers(minPowerKw: Int): List<Charger> =
    chargers
        .filter {
            it.status == ChargerStatus.AVAILABLE &&
                it.powerKw >= minPowerKw // filters out 0 kW listings too
        }
        .sortedBy { it.distanceMeters }

// ---------------------------------------------------------------------------
// TODO 4 — estimatedCost(charger, kWh): something sensible
// Estimate the cost to add `kWh` of energy at a charger. The price may be
// unknown — your return type should make the caller deal with that honestly.
// ---------------------------------------------------------------------------

fun estimatedCost(charger: Charger, kWh: Double): EstimatedCost {
    val price = charger.pricePerKwh ?: return EstimatedCost.Unknown
    return EstimatedCost.Known(price * kWh)
}

// ---------------------------------------------------------------------------
// TODO 5 — formatDistance(meters: Int): String
// Human-friendly distance, e.g. 120 -> "120 m", 1490 -> "1.5 km".
// ---------------------------------------------------------------------------

fun formatDistance(meters: Int): String {
    if (meters < 1000) return "$meters m"
    val km = kotlin.math.round(meters / 100.0) / 10.0
    return if (km % 1.0 == 0.0) "${km.toInt()} km" else "$km km"
}

fun main() {
    // ---------------------------------------------------------------------------
    // TODO 6 — Demonstrate your work.
    // Print the chargers available for a driver who needs at least 50 kW,
    // and for each show: name, formatted distance, and the estimated cost
    // of a 30 kWh top-up. Make the output readable.
    // ---------------------------------------------------------------------------

    val minPowerKw = 50
    val topUpKwh = 30.0

    println("Available chargers (${minPowerKw} kW+, nearest first)")
    println("--------------------------------------------------------")

    for (charger in availableChargers(minPowerKw)) {
        val distance = formatDistance(charger.distanceMeters)
        val costText = when (val cost = estimatedCost(charger, topUpKwh)) {
            is EstimatedCost.Known -> "£%.2f for 30 kWh".format(cost.pounds)
            EstimatedCost.Unknown -> "price unavailable"
        }
        println("${charger.name} | $distance | $costText")
    }
}

# Electroverse EV Charger Finder - Kotlin Tech Test

A clean, safe Kotlin solution for processing messy EV charger data.

**Live Demo:** https://pl.kotl.in/fuByQe6rp

## Features
- Safe parsing of raw JSON-like data (`List<Map<String, Any?>>`)
- Proper domain modeling with `data class`, `enum class`, and `sealed class`
- Filters available fast chargers with minimum power requirement
- Calculates charging cost while gracefully handling missing prices
- Human-friendly distance formatting

## What I Learned
- Working with loosely-typed data and turning it into trustworthy models
- Using sealed classes for better type safety
- Defensive programming with `mapNotNull` and safe conversions

## Tech Stack
- Kotlin (100% browser-based, no dependencies)

## Output Example

<img width="832" height="244" alt="Screenshot 2026-05-28 at 18 47 05" src="https://github.com/user-attachments/assets/a999f2b9-99b1-4530-9b0a-8177569479ff" />

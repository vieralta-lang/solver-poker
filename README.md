# Parser Backend

> High-performance PokerStars hand history parser built with Quarkus — transforms raw hand transcripts into structured, replay-ready JSON.

---

## Overview

**Parser Backend** is a RESTful microservice that ingests plain-text PokerStars hand histories and produces structured game metadata. It enables downstream tooling such as hand replayers, equity calculators, and statistical analysis engines.

### Key Capabilities

- **Full street-by-street parsing** — Pre-Deal, Pre-Flop, Flop, Turn, River, Showdown, and Summary
- **Accurate pot tracking** — handles bets, raises, uncalled bets, and multi-street contributions
- **Player position assignment** — Button, Small Blind, Big Blind detection
- **Hero identification** — automatic detection of the analyzed player's hole cards
- **Table snapshots** — complete game state at each street boundary
- **Replay-ready output** — frame-based JSON structure for UI-driven hand replayers

---

## Tech Stack

| Component        | Technology                   |
|------------------|------------------------------|
| Language         | Java 17+                     |
| Framework        | Quarkus 3.28.2               |
| Build            | Maven                        |
| REST             | Jakarta RS (quarkus-rest)    |
| Serialization    | Jackson                      |
| Database         | MongoDB (Panache)            |
| Testing          | JUnit 5, REST Assured        |
| Containerization | Docker, Docker Compose       |
| Native Build     | GraalVM Native Image support |

---

## Architecture

```
POST /api/replay  (raw hand text)
        │
        ▼
┌─────────────────────────────┐
│     HandSectionSplitter     │  Split text into sections
└──────────────┬──────────────┘
               │
        ┌──────┴──────────────────────────────┐
        ▼              ▼              ▼        ▼
  PreDeal       PreFlop        PostFlop   Showdown
  Processor     Processor      Processor  Processor
        │              │              │        │
        └──────┬──────────────────────────────┘
               ▼
┌─────────────────────────────┐
│   HeaderParser              │  Hand ID, Tournament, Blinds
│   ActionParser              │  Player actions per street
│   CommunityCardsParser      │  Board cards
│   RevealedCardsParser       │  Showdown cards
│   UncalledBetParser         │  Returned bets
│   PositionAssigner          │  BTN / SB / BB
│   PotTracker                │  Running pot & contributions
│   SnapshotBuilder           │  State snapshots per street
└──────────────┬──────────────┘
               ▼
┌─────────────────────────────┐
│     HandMetaDataResponse    │  Structured JSON output
└─────────────────────────────┘
```

---

## Getting Started

### Prerequisites

- **Java 17+** (or GraalVM for native builds)
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **Docker** (optional, for containerized execution)

### Run in Dev Mode

```bash
./mvnw quarkus:dev
```

The application starts at `http://localhost:8080` with live reload enabled.

### Build & Run (JVM)

```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

### Build & Run (Native)

```bash
./mvnw package -Pnative
./target/parser-backend-1.0.0-SNAPSHOT-runner
```

### Docker

```bash
docker compose up --build
```

---

## API Reference

### Parse Hand History

```
POST /api/replay
Content-Type: text/plain
```

**Request body:** Raw PokerStars hand history text.

**Response:** JSON with complete hand metadata including players, actions, pot snapshots, and community cards.

<details>
<summary>Example response structure</summary>

```json
{
  "handId": "260030132622",
  "tournamentId": "3982026132",
  "blinds": { "smallBlind": 25, "bigBlind": 50, "ante": 6 },
  "players": [
    {
      "name": "BerserkGutts",
      "seat": 3,
      "chips": 1500,
      "position": "BTN",
      "isHero": true,
      "holeCards": ["Jc", "Jh"]
    }
  ],
  "streets": {
    "preflop": { "actions": [...], "pot": 160 },
    "flop":    { "communityCards": ["Ks", "7d", "2h"], "actions": [...], "pot": 410 },
    "turn":    { "communityCards": ["Ks", "7d", "2h", "9c"], "actions": [...], "pot": 610 }
  }
}
```

</details>

---

## Project Structure

```
src/main/java/org/torinelli/
├── ParserCommand.java              # Orchestrates the parsing pipeline
├── api/
│   └── dto/                        # Response DTOs (replay frames, player state, etc.)
├── domain/
│   ├── Card.java                   # Card with Rank & Suit
│   ├── Hand.java                   # Hole cards (max 2)
│   ├── Player.java                 # Player state & actions
│   ├── Table.java                  # Table snapshot
│   ├── HandMetaData.java           # Complete parsed hand result
│   ├── Blind.java                  # Blind structure
│   ├── Seat.java                   # Seat assignments
│   └── enums/                      # Rank, Suit, Position, ActionType
├── parser/
│   ├── HeaderParser.java           # Hand/tournament metadata
│   ├── ActionParser.java           # Player action extraction
│   ├── CommunityCardsParser.java   # Board cards
│   ├── RevealedCardsParser.java    # Showdown cards
│   ├── UncalledBetParser.java      # Uncalled bet handling
│   ├── PositionAssigner.java       # Position calculation
│   ├── PotTracker.java             # Pot tracking engine
│   ├── SnapshotBuilder.java        # Street snapshots
│   ├── HandSectionSplitter.java    # Section splitter
│   ├── ParserPatterns.java         # Regex patterns
│   ├── HandProcessingContext.java  # Pipeline state
│   ├── PreDealHandProcessor.java   # Pre-deal stage
│   ├── PreFlopHandProcessor.java   # Pre-flop stage
│   ├── PostFlopStreetHandProcessor.java  # Flop/Turn/River
│   ├── ShowdownHandProcessor.java  # Showdown stage
│   └── SummaryHandProcessor.java   # Summary stage
├── resources/                      # REST endpoints
└── utils/                          # Utility classes
```

---

## Running Tests

```bash
./mvnw test
```

---

## License

This project is proprietary. All rights reserved.
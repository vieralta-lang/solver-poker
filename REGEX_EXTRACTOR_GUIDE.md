# PokerHandRegexExtractor - Referência Completa

## 📋 Visão Geral

Classe utilitária com **30+ métodos regex** para extrair informações de mãos de poker em formato PokerStars.

## 🎯 Padrões Regex por Categoria

### 1️⃣ HAND HEADER

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractHandId()` | `PokerStars Hand #(\d+):` | `PokerStars Hand #260030132622:` | `260030132622` |
| `extractTournamentId()` | `Tournament #(\d+)` | `Tournament #3982026132` | `3982026132` |
| `extractStakes()` | `(\$[\d.]+\+\$[\d.]+)\s+USD` | `$4.40+$0.60 USD` | `$4.40+$0.60` |
| `extractLevelAndBlinds()` | `Level\s+([A-Za-z]+)\s+\((\d+)/(\d+)\)` | `Level III (25/50)` | `{level: III, sb: 25, bb: 50}` |
| `extractTimestamp()` | `(\d{4}/\d{2}/\d{2}\s+\d{2}:\d{2}:\d{2}\s+ET)` | `2026/03/09 22:03:18 ET` | `2026/03/09 22:03:18 ET` |

### 2️⃣ TABLE INFO

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractTableName()` | `Table '([^']+)'` | `Table '3982026132 1'` | `3982026132 1` |
| `extractMaxPlayers()` | `(\d+)-max` | `9-max` | `9` |
| `extractButtonSeat()` | `Seat #(\d+) is the button` | `Seat #9 is the button` | `9` |

### 3️⃣ PLAYER INFO

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractPlayerLine()` | `Seat\s+(\d+):\s+(.+?)\s+\((\d+)\s+in chips\)` | `Seat 2: WestwoodMuff (1357 in chips)` | `{seat: 2, name: WestwoodMuff, chips: 1357}` |
| `extractAllPlayers()` | Múltiplas linhas | Multiple seat lines | `List<Map>` com todos |
| `extractBlindPosition()` | `posts (small blind\|big blind)` | `posts small blind` | `small blind` |

### 4️⃣ ANTE & BLINDS

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractAnte()` | `posts the ante (\d+)` | `posts the ante 6` | `6` |
| `extractBlindValue()` | `posts (?:small blind\|big blind) (\d+)` | `posts small blind 25` | `25` |

### 5️⃣ HOLE CARDS

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractHeroName()` | `Dealt to (\S+)\s+\[` | `Dealt to BerserkGutts [Jc Jh]` | `BerserkGutts` |
| `extractHeroCards()` | `Dealt to\s+\S+\s+\[([^\]]+)\]` | `[Jc Jh]` | `Jc Jh` |
| `extractHeroCardsAsList()` | Split de cards | `Jc Jh` | `[Jc, Jh]` |

### 6️⃣ ACTIONS (PRE-FLOP & STREETS)

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractAction()` | `^(\S+):\s+(.+)$` | `BerserkGutts: raises 55 to 105` | `{player: BerserkGutts, action: raises 55 to 105}` |
| `extractAllActions()` | Múltiplas linhas | Multiple action lines | `List<Map>` |
| `extractActionType()` | `(folds\|checks\|bets\|raises\|calls\|posts)` | `raises 55 to 105` | `raises` |
| `extractActionValues()` | `\b(\d+)\b` | `raises 55 to 105` | `[55, 105]` |

### 7️⃣ BOARD CARDS

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractBoardCards()` | `\*\*\*\s+(\w+)\s+\*\*\*\s+\[([^\]]+)\]` | `*** FLOP *** [Td Qc Kd]` | `{street: FLOP, cards: Td Qc Kd}` |
| `extractBoardCardsAsList()` | Split de cards | `Td Qc Kd` | `[Td, Qc, Kd]` |

### 8️⃣ SUMMARY INFO

| Método | Regex | Exemplo | Saída |
|--------|-------|---------|-------|
| `extractPotAndRake()` | `Total pot (\d+)\s+\|\s+Rake (\d+)` | `Total pot 2235 \| Rake 0` | `{pot: 2235, rake: 0}` |
| `extractFinalBoard()` | `^Board\s+\[([^\]]+)\]` | `Board [Td Qc Kd Ad 4h]` | `Td Qc Kd Ad 4h` |
| `extractShowdownResult()` | Complex | `Seat 3: josnos showed [5d 2d] and won` | `{player, action, cards, result}` |

## 💡 Exemplos de Uso

### Exemplo 1: Extrair info do header
```java
String hand = "PokerStars Hand #260030132622: Tournament #3982026132, $4.40+$0.60 USD Hold'em No Limit - Level III (25/50) - 2026/03/09 22:03:18 ET";

String handId = PokerHandRegexExtractor.extractHandId(hand);
String tournamentId = PokerHandRegexExtractor.extractTournamentId(hand);
Map<String, String> blinds = PokerHandRegexExtractor.extractLevelAndBlinds(hand);

System.out.println("Hand: " + handId);
System.out.println("Tournament: " + tournamentId);
System.out.println("Blinds: " + blinds.get("smallBlind") + "/" + blinds.get("bigBlind"));
```

### Exemplo 2: Extrair todos os jogadores
```java
String fullText = """
Seat 2: WestwoodMuff (1357 in chips)
Seat 3: josnos (4192 in chips)
Seat 5: EdsonAlabama (1437 in chips)
""";

List<Map<String, String>> players = PokerHandRegexExtractor.extractAllPlayers(fullText);

for (Map<String, String> player : players) {
    System.out.println("Seat " + player.get("seat") + ": " + 
                      player.get("name") + " (" + player.get("chips") + ")");
}
```

### Exemplo 3: Extrair hole cards e flop
```java
String heroLine = "Dealt to BerserkGutts [Jc Jh]";
String flopLine = "*** FLOP *** [Td Qc Kd]";

String heroName = PokerHandRegexExtractor.extractHeroName(heroLine);
List<String> heroCards = PokerHandRegexExtractor.extractHeroCardsAsList(heroLine);
List<String> flopCards = PokerHandRegexExtractor.extractBoardCardsAsList(flopLine);

System.out.println(heroName + " has " + heroCards);
System.out.println("Flop: " + flopCards);
```

### Exemplo 4: Extrair ações e seus valores
```java
String actionLine = "BerserkGutts: raises 55 to 105";

Map<String, String> action = PokerHandRegexExtractor.extractAction(actionLine);
String actionType = PokerHandRegexExtractor.extractActionType(action.get("action"));
List<String> values = PokerHandRegexExtractor.extractActionValues(action.get("action"));

System.out.println(action.get("player") + " " + actionType + " " + values);
// Output: BerserkGutts raises [55, 105]
```

## 📝 Padrões Regex Explicados

### Hand ID Pattern
```regex
PokerStars Hand #(\d+):
```
- Busca por "PokerStars Hand #" seguido de 1 ou mais dígitos
- Grupo 1: Os dígitos do Hand ID

### Player Line Pattern
```regex
Seat\s+(\d+):\s+(.+?)\s+\((\d+)\s+in chips\)
```
- `Seat\s+` = "Seat" seguido de whitespace
- `(\d+)` = Número do seat (Grupo 1)
- `:\s+` = ":" seguido de whitespace
- `(.+?)` = Nome do player, non-greedy (Grupo 2)
- `\s+\(` = whitespace + "("
- `(\d+)` = Stack em chips (Grupo 3)
- `\s+in chips\)` = "in chips)"

### Blinds Pattern
```regex
Level\s+([A-Za-z]+)\s+\((\d+)/(\d+)\)
```
- `Level\s+` = "Level" + whitespace
- `([A-Za-z]+)` = Level name como letras (Grupo 1)
- `\s+\(` = whitespace + "("
- `(\d+)` = Small blind (Grupo 2)
- `/` = "/"
- `(\d+)` = Big blind (Grupo 3)
- `\)` = ")"

## 🚀 Casos de Uso

✅ Parsing de mãos de poker  
✅ Extração de dados para análise  
✅ Importação em banco de dados  
✅ Geração de relatórios  
✅ Replay de mãos  
✅ Cálculo de estatísticas  

## 📂 Arquivos

- `PokerHandRegexExtractor.java` - Classe com todos os métodos regex
- `PokerHandRegexExtractorExample.java` - Exemplos de uso

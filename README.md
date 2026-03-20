INÍCIO DA STREET
┌──────────────────────────────┐
│ streetStartPot = runningPot  │  ← tira uma "foto" do pote aqui
└───────────────┬──────────────┘
│
▼
AÇÕES DA STREET (loop linha a linha)
┌─────────────────────────────────────────────────────────────┐
│ ação válida (call/bet/raise)                               │
│ contribution = potTracker.calculateActionContribution(...) │
│ runningPot = runningPot + contribution                     │
└─────────────────────────────────────────────────────────────┘
│
▼
UNCALLED BET (se aparecer)
┌────────────────────────────────────────────┐
│ "Uncalled bet (X) returned to Player"      │
│ runningPot = runningPot - X                │
└────────────────────────────────────────────┘
│
▼
FIM DA STREET (snapshot)
┌──────────────────────────────────────────────────────────────┐
│ potAtStreetStart   = streetStartPot                         │
│ totalPot           = runningPot                             │
│ streetContribution = runningPot - streetStartPot            │
└──────────────────────────────────────────────────────────────┘

Começo do FLOP:
streetStartPot = 160
runningPot     = 160

Ações:
bet 50   -> runningPot = 210
raise 100 to 150 (incremento 100) -> runningPot = 310
call 100 -> runningPot = 410

Uncalled bet (120) returned -> runningPot = 290   (se ocorrer nessa street)

Fim:
streetContribution = runningPot - streetStartPot
= 290 - 160
= 130
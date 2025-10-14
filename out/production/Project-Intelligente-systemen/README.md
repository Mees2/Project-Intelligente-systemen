# Project-Intelligente-systemen
project intelligente systemen, leerteam 4, hanze HBO-ICT SE jaar 2

## Project Structuur

Dit project volgt de standaard Maven/Gradle directory layout voor best practices:

```
Project-Intelligente-systemen/
├── src/
│   └── main/
│       └── java/
│           ├── Main.java
│           ├── framework/
│           │   ├── ai/              - AI algoritmes (AbstractMinimaxAI)
│           │   ├── bordspel/        - Bordspel logica (AbstractBordSpel, Positie, Zet)
│           │   ├── gui/             - GUI componenten (AbstractSpelGUI, SpelKnop)
│           │   ├── netwerk/         - Netwerk communicatie (AbstractClient, AbstractServer)
│           │   └── spelers/         - Speler management (AbstractSpeler, AISpeler)
│           ├── menu/                - Menu systeem en GUI
│           ├── server/              - Server client implementatie
│           └── tictactoe/           - TicTacToe implementatie
├── README.md
└── .gitignore
```

## Framework Structuur

Dit project bevat een herbruikbaar framework voor bordspellen zoals TicTacToe en Reversi/Othello.

### Framework Packages:
- **framework.bordspel** - Abstracte bordspel logica (AbstractBordSpel, Positie, Zet, SpelStatus)
- **framework.spelers** - Speler management (AbstractSpeler, MenselijkeSpeler, AISpeler)
- **framework.ai** - AI algoritmes (AbstractMinimaxAI met minimax implementatie)
- **framework.gui** - GUI componenten (AbstractSpelGUI, SpelKnop)
- **framework.netwerk** - Netwerk communicatie (AbstractClient, AbstractServer)

### Geïmplementeerde Spellen:
- **TicTacToe** - Volledig werkend met PVP en PVA modes

## Server Configuratie

### Server Starten:
```bash
java -jar newgamesver-release-V1.jar
```

### Server Details:
- **Game-server poort:** 7789
- **Web-interface:** http://127.0.0.1:8081
- **Default host:** 127.0.0.1 (localhost)

### Server Verbinding vanuit GUI:
1. Start het programma met `Main.java`
2. Ga naar: Main Menu → TicTacToe Menu
3. Klik op de "Server" button
4. De client verbindt automatisch met 127.0.0.1:7789

### Server Commands:
- `login <naam>` - Inloggen met gebruikersnaam
- `get gamelist` - Lijst van beschikbare spellen ophalen
- `subscribe tic-tac-toe` - Inschrijven voor TicTacToe match
- `move <positie>` - Zet doen (positie 0-8)
- `forfeit` - Opgeven

## Applicatie Starten

```bash
java Main
```


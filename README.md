# Quantum Client Mod

Fabric mod for Minecraft 1.21.4 — client-side utility mod.

## Modules
- **Fullbright** — Maximum brightness everywhere
- **Sprint** — Always sprint automatically  
- **NoHurtCam** — Disable camera shake when damaged
- **NoFall** — Prevent fall damage
- **FastPlace** — Remove delay between block placements
- **AntiFireOverlay** — Hide fire overlay on screen

## In-game
Press **Right Shift** to open the module menu.

## How to build via GitHub

1. Create a new repository on GitHub (e.g. `quantum-client-mod`)
2. Open terminal in this `mod/` folder
3. Run:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/YOUR_USERNAME/quantum-client-mod.git
   git push -u origin main
   ```
4. GitHub Actions will automatically build the `.jar` and publish it as a Release
5. Download the `.jar` from the Releases tab

## Manual install
1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.4
2. Place the `.jar` into `.minecraft/mods/`
3. Launch with Fabric profile

## Requirements
- Minecraft 1.21.4
- Fabric Loader 0.16.9+
- Fabric API 0.114.0+

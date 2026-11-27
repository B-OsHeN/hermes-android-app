# Hermes Agent Android App

A pro Android client for [Hermes Agent](https://github.com/NousResearch/hermes-agent) (MIT).

## Architecture
Native Kotlin/Compose frontend ↔ Hermes Agent in Termux (localhost HTTP/SSE). All data stays on-device.

## Setup
1. Install Hermes in Termux: https://hermes-agent.nousresearch.com/docs/getting-started/termux
2. Run the backend: `cd backend && pip install -r requirements.txt && python main.py`
3. Build the Android app: `./gradlew assembleDebug`
4. Install APK on device.

## License
- App code: (your license)
- Hermes Agent: MIT (Nous Research) — copyright notice retained in backend/
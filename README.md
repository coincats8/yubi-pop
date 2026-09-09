# Yubi Pop!

Yubi Pop! is a fast, mobile-first thumb guessing game inspired by a Japanese party game.

## Current prototype

- Four players stay in every round.
- Each player reveals a thumb at random.
- The player guesses the total number of thumbs.
- Correct guesses build a streak.

## Android build preparation

This project is configured for Capacitor, which packages the game as an Android application.

1. Install Node.js and Android Studio.
2. Run `npm install` in this folder.
3. Run `npx cap add android` once.
4. Run `npm run sync:android`.
5. Run `npm run open:android`, then build an APK in Android Studio.

## Hackathon roadmap

1. Finish the core game and multiplayer flow.
2. Add Mobile Wallet Adapter for Seeker wallet connection.
3. Add an honest SKR-powered Daily Pop Challenge.
4. Produce an APK, GitHub repository, demo video, and pitch deck.

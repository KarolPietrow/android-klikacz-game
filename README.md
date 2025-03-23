# Klikacz

![version1](https://github.com/user-attachments/assets/0d990fe9-0cb3-42cd-b1a9-0c5456f383cd)

## An Android clicker game written in Kotlin using Jetpack Compose UI.
- **Game view** - Click to increase the score and earn achievements!
- **Shop view** - Buy upgrades to increase your score faster!
- **Ranking view** - Compete with others and browse the top players!
- **Profile view** - View your profile and browse your achievements!
- **Personalisation view** - Personalize your gameplay by editing the game screen and changing your profile picture!
- **Fortune Wheel** - Watch an ad, then shake your phone to win useful rewards!
- **Ads** - The application uses Google AdMob SDK to display Banner ads (at the bottom of the game screen) and Reward ads (required to use the Fortune Wheel)

## Login and registration
Ability to log in and register with an email address and password, or using a Google account.
Service handled by Google Firebase Authentication.
Terms and Privacy Policy of the Klikacz application: https://www.karolpietrow.pl/klikacz/tos

## Game data storing
User data is saved both locally to SharedPreferences and online to Firebase Firestore, so the user can log in from any device and access their progress.

## Purchasing upgrades
### A shop is available, where the user can purchase upgrades that will help them increase their score faster. The main types of upgrades are:
- Increase multiplier (clicking the button increases the counter by a higher value),
- AutoClicker - an upgrade that clicks for the user at a specified time interval.
- Upgrades to AutoClicker frequency
- Upgrades to AutoClicker multiplier
- Other upgrades - including personalization

## Achievements
As the user progresses through the game, they earn various achievements that they can view in the Profile tab. Some achievements are secret!

## Personalisation
- User can buy an upgrade that unlocks customization. With it, they can edit the texts and emotes that are displayed on the game's home screen.
- User can set their own profile picture, which is then stored in Firebase Firestore, encoded in Base64.

## Fortune Wheel
The Klikacz Fortune Wheel lets you draw useful prizes that will help you earn points faster! Device sensors (accelerometer) are used for the spin.

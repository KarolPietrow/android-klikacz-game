# Klikacz

![version1](https://github.com/user-attachments/assets/0d990fe9-0cb3-42cd-b1a9-0c5456f383cd)

## Gra typu Clicker, napisana w Kotlinie w Jetpack Compose.
- **Widok gry** - Klikaj, aby zwiększać wynik i zdobywać osiągnięcia!
- **Widok sklepu** - Kupuj ulepszenia aby szybciej zwiększać wynik!
- **Widok rankingu** - Rywalizuj z innymi i przeglądaj najlepszych graczy!
- **Widok profilu** - Przeglądaj swój profil i przeglądaj zdobyte osiągnięcia!
- **Widok personalizacji** - Personalizuj swoją rozgrywkę poprzez edycję ekranu gry, oraz zmianę zdjęcia profilowego!
- **Koło fortuny** - Obejrzyj reklamę, a potem potrząśnij telefonem, aby wylosować przydatne nagrody!
- **Reklamy** - Aplikacja wykorzystuje Google AdMob w celu wyświetlania reklam banerowych na dole ekranu gry, oraz reklam typu Reward (po obejrzeniu reklamy możliwe jest losowanie Koła fortuny)

## Logowanie i rejestracja
Możliwość zalogowania i rejestracji adresem e-mail i hasłem, lub korzystając z konta Google.
Usługa obsługiwana przez Google Firebase Authentication.
Regulamin i Polityka prywatności aplikacji Klikacz: https://www.karolpietrow.pl/klikacz/tos

## Przechowywanie danych gry
Dane użytkownika są zapisywane zarówno lokalnie, do SharedPreferences, jak i do Firebase Firestore, dzięki czemu użytkownik może zalogować się na dowolnym urządzeniu i mieć dostęp do swojego postępu.

## Kupowanie ulepszeń
### Dostępny jest sklep, w którym użytkownik może zakupić ulepszenia, które pomogą mu w szybszym zwiększaniu wyniku. Główne typy ulepszeń to:
- Zwiększanie mnożnika (klikanie przycisku zwiąksza licznik o większą wartość),
- AutoClicker – ulepszenie, które klika za użytkownika co określony interwał czasowy.
- Ulepszenia częstotliwości AutoClickera
- Ulepszenia mnożnika AutoClickera
- Inne ulepszenia – m.in. ulepszenia personalizacji

## Osiągnięcia
coming soon xd

## Personalizacja
- Użytkownik może kupić ulepszenie odblokowujące personalizację. Dzięki niemu może edytować teksty i emotki wyświetlane na ekranie głównym gry.
- Użytkownik może ustawić własne zdjęcie profilowe. Jest ono przechowywane w Firebase Firestore zakodowane w formacie Base64.

## Koło fortuny
Koło Fortuny Klikacza pozwala wylosować przydatne nagrody, które pomogą szybciej zdobywać punkty! W celu losowania wykorzystywane są czujniki (akcelerometr).

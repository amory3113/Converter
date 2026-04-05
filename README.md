# 💱 Currency Converter App

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room-Local_DB-4CAF50?style=for-the-badge&logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Dagger_Hilt-DI-red?style=for-the-badge)

A modern, fast, and fully offline-capable Currency Converter app built for Android using the latest tech stack: **Kotlin, Jetpack Compose, MVVM, and Clean Architecture**. 

It goes beyond simple conversions by offering a custom math-expression keyboard, multi-currency simultaneous exchange, and a sleek home screen widget.

## ✨ Features

* **🧮 Custom Keyboard & Calculator:** A fully custom bottom-sheet keyboard that doesn't just input numbers, but evaluates complex math expressions (e.g., `(100+50)*1.1`) directly in the input field!
* **🌍 Multi-Exchange Screen:** Select one base currency and see its value converted into a personalized list of multiple target currencies simultaneously.
* **📱 Jetpack Glance Widget:** A beautiful, scrollable home screen widget. Keep track of your favorite exchange rates right from your home screen without opening the app.
* **✈️ 100% Offline Support:** Fetched exchange rates are cached locally using **Room Database**, allowing you to convert currencies even in airplane mode.
* **🏦 Bank Commission Mode:** Toggleable bank commission calculation to see exactly how much you'll get in real life after fees.
* **🗣️ Multilingual:** Fully localized in English, Ukrainian (🇺🇦), and Polish (🇵🇱).
* **🎨 Material 3 Design:** Sleek UI with dynamic gradient cards, smooth animations, and automatic Dark/Light theme support.

## 📸 Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/a6f41e1b-323b-4cd3-a9c2-79385c4362ae" width="19%">
  <img src="https://github.com/user-attachments/assets/d2d28d74-776f-49e2-8254-256418f894e0" width="19%">
  <img src="https://github.com/user-attachments/assets/ad52e77d-0032-4b7d-8066-2ffcb4cff7fb" width="19%">
  <img src="https://github.com/user-attachments/assets/1b5f4132-c6ca-4a5d-bdf3-866a55eecacd" width="19%">
  <img src="https://github.com/user-attachments/assets/be788a0d-84bf-4e92-8f30-93191fc38bc7" width="19%">
</p>

## 🛠 Tech Stack & Libraries

This app takes advantage of modern Android development practices:

* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative, reactive UI.
* **Widgets:** [Jetpack Glance](https://developer.android.com/jetpack/compose/glance) for building Compose-style app widgets.
* **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles.
* **Dependency Injection:** [Dagger-Hilt](https://dagger.dev/hilt/).
* **Local Database:** [Room](https://developer.android.com/training/data-storage/room) for caching exchange rates.
* **Preferences:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for saving user settings (selected currencies, commission rates).
* **Networking:** [Retrofit2](https://square.github.io/retrofit/) + [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for fast API calls.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/) for loading currency flags efficiently.
* **Math Evaluation:** [exp4j](https://www.objecthunter.net/exp4j/) for parsing and evaluating math formulas in the custom keyboard.
* **Navigation:** Jetpack Navigation Compose.

## 🚀 Getting Started

To run this project locally:

1. Clone the repository:
   ```bash
   git clone [https://github.com/YourUsername/CurrencyConverter.git](https://github.com/YourUsername/CurrencyConverter.git)
2. Open the project in Android Studio.
3. Note: If you are using a specific Currency API (e.g., ExchangeRate-API or OpenExchangeRates), make sure to add your API Key in the local.properties file or network module (depending on your setup).
4. Build and run the app on an emulator or a physical device.

👨‍💻 Author Maksym Shevelenko, Telegram: @amory3113, Gmail: maksshevelenko@gmail.com

# 💸 Mone – Smart Expense Tracker

**Mone** is a modern, sleek, and offline-first expense tracking app built with **Jetpack Compose**, **Koin**, and **Realm**. Designed for simplicity, privacy, and performance – Mone automatically parses your bank SMS messages to help you stay on top of your finances.

---

## 🛠 Tech Stack

| Layer                | Tech                                                  |
|----------------------|-------------------------------------------------------|
| UI                   | Jetpack Compose, Material 3                           |
| Architecture         | MVVM                                                  |
| State Management     | StateFlow + ViewModel                                 |
| Dependency Injection | [Koin](https://insert-koin.io/)                       |
| Database             | Realm Kotlin SDK                                      |
| Data Storage         | Jetpack DataStore                                     |
| Charts               | Vico, Compose-Charts                                  |
| Permissions          | Jetpack Activity Compose                              |

---

## 📱 Features

- 📊 Visualize your spending and income trends
- 🧾 Automatically parse and log SMS-based bank transactions
- 📈 Dashboards with live financial stats
- 🧠 Smart categorization of merchants and transactions
- 🔒 100% offline and secure (no data leaves your device)
- 🎯 Clean onboarding with currency selection and SMS permission flow
- ⚙️ Easily manage transactions and keyword mappings

---

## 🧱 App Architecture

- **MVVM** with clean separation of concerns
- **Koin DI** manages all ViewModels and Repositories
- **Composable-first UI** for reusability and maintainability
- **StateFlows** power all reactive data pipelines
- **ViewModel-to-ViewModel** sharing temporarily supported (planned to abstract into shared state modules)

---

## 🧹 Modules

- `viewmodels/` – State + logic containers
- `pages/` – UI screens (e.g., Expenses, Analytics, Dashboard)
- `data/` – Realm DB, Repositories, and seeders
- `components/` – Reusable UI elements and utilities
- `utils/` – SMS parser, filters, number and date utilities
- `datastore/` – Persistent user preferences and onboarding state

---

## 🤪 Project Structure

```bash
Mone/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/money/
│   │   │   │   ├── components/         # Charts, UI widgets, input controls
│   │   │   │   ├── pages/              # Main screens (Dashboard, Analytics, etc.)
│   │   │   │   ├── viewmodels/         # ViewModels for each screen/module
│   │   │   │   ├── data/               # DB setup, repositories, models
│   │   │   │   ├── utils/              # Parser, filters, date/number helpers
│   │   │   │   └── navigation/         # AppRoutes and BottomNavItems
│   │   │   └── res/                    # UI assets, themes, and images
│   ├── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚪 Contribution Guide

We welcome contributions! Here's how to get started:

### 1. Fork the repository
Click on the "Fork" button at the top-right of this page to create your own copy.

### 2. Clone your fork
```bash
git clone https://github.com/yourusername/Mone.git
cd Mone
```

### 3. Create a feature branch
```bash
git checkout -b feature/your-feature-name
```

### 4. Make changes and commit
```bash
git add .
git commit -m "Add your message"
```

### 5. Push and open a pull request
```bash
git push origin feature/your-feature-name
```
Open a PR from your fork's branch into the main `blue-mix/Mone` repo.

Please follow proper commit message hygiene, write clean code, and test your features before submitting a PR. 🙌

---

## 🚀 Getting Started

### ✅ Prerequisites

- Android Studio Hedgehog or newer
- Kotlin 2.0+
- Gradle 8.9+
- JDK 17+

### ▶️ Run the App

```bash
git clone https://github.com/blue-mix/Mone.git
cd Mone
./gradlew installDebug
```

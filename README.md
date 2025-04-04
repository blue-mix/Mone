# 💸 MoneyMate – Smart Expense Tracker

**MoneyMate** is a modern, sleek, and offline-first expense tracking app built with **Jetpack Compose**, **Koin**, and **Realm**. Designed for simplicity, privacy, and performance – MoneyMate automatically parses your bank SMS messages to help you stay on top of your finances.

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

## 🧩 Modules

- `viewmodels/` – State + logic containers
- `pages/` – UI screens (e.g., Expenses, Analytics, Dashboard)
- `data/` – Realm DB, Repositories, and seeders
- `components/` – Reusable UI elements and utilities

---

## 🧪 Testing

We're in the process of expanding coverage with:
- `koin-test` for ViewModel unit testing
- Mockable interfaces for repository layer
- Separation of pure functions into utils for fast, isolated tests

---

## 🚀 Getting Started

### ✅ Prerequisites

- Android Studio Hedgehog or newer
- Kotlin 2.0+
- Gradle 8.9+
- JDK 17+

### ▶️ Run the App

```bash
git clone https://github.com/yourusername/moneymate.git
cd moneymate
./gradlew installDebug

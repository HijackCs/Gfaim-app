# Gfaim – Smart Meal & Family Nutrition Tracker

Gfaim is a modern Android application designed to help individuals and families track their meals, manage recipes, monitor nutrition, and coordinate grocery activities. Built with a clean architecture and leveraging the latest Android best practices, Gfaim offers a comprehensive and user-friendly experience for healthy eating and collaborative meal planning.

> **Note:** This project was developed as part of a university course by a team of four students.

## Features

- **Meal Tracking:**  
  Log and visualize daily meals (breakfast, lunch, dinner, snacks) with nutritional breakdown (calories, proteins, carbs, fats) for each meal.

- **Recipe Management:**  
  Browse, create, and edit recipes. Step-by-step cooking instructions and ingredient management are included.

- **Family & Group Support:**  
  Invite family members, manage household profiles, and coordinate shared meal and grocery planning.

- **Grocery List Integration:**  
  Generate and manage grocery lists based on planned meals and shared family needs.

- **Nutrition Insights:**  
  Get automatic nutrition summaries per meal and per day.

- **Calendar View:**  
  Visualize and plan meals over days or weeks with an integrated calendar.

- **User Authentication & Onboarding:**  
  Secure login, registration, and onboarding flows with support for password recovery.

- **Profile & Settings:**  
  Manage user profiles, dietary preferences, allergies, and family memberships.

- **Modern Android Stack:**  
  Uses Retrofit for API calls, ViewModels for state management, and follows modular, maintainable code organization.


## Getting Started

### Prerequisites

- Android Studio (latest stable version recommended)
- Android device or emulator running Android 8.0 (API 26) or above
- Java 11 or newer

### Installation

1. **Clone the repository:**
   ```sh
   git clone https://github.com/HijackCs/Gfaim-app.git
   cd gfaim-client
   ```

2. **Open in Android Studio:**
   - Select `File > Open` and choose the project directory.

3. **Configure the backend API URL:**
   - By default, the app points to `http://10.0.2.2:8080` for local development (see `ApiClient.java`).
   - Update this if your backend server runs elsewhere.

4. **Build & Run:**
   - Click `Run` or use `Shift+F10` to deploy on an emulator or device.

### Dependencies

Key dependencies (see `app/build.gradle`):
- Retrofit (API communication)
- Gson (JSON parsing)
- OkHttp (networking)
- AndroidX libraries
- Lombok (code generation)
- Espresso, JUnit (testing)

## Project Structure

```
app/
  └── src/main/java/com/gfaim/
      ├── activities/        # Main UI screens (auth, calendar, groceries, home, recipe, settings)
      ├── api/               # API clients and service interfaces
      ├── auth/              # Authentication logic
      ├── models/            # Data models (meals, recipes, users, etc.)
      └── utility/           # Utilities and helpers
```

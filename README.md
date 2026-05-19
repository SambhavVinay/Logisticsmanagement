# MADECIE3

A native Android logistics and shipment management application built with Kotlin. MADECIE3 lets users authenticate, manage shipments, browse a product catalog, simulate checkout, and track deliveries through a modern, card-based interface.

---

## Project Overview

**MADECIE3** is a logistics-focused Android app that combines shipment creation, order viewing, payment simulation, and tracking in one workflow. The dashboard surfaces live product data from the [FakeStore API](https://fakestoreapi.com/), while completed shipments are stored locally so users can review history and see their latest shipment on the home screen.

The app uses **Firebase Authentication** for sign-in (email/password and Google) and **Retrofit** for REST calls. Shipment records created after payment are persisted with **Room**.

---

## Features

- **Login & Signup** — Email/password authentication via Firebase; signup supports display name, validation, and optional avatar image selection (local preview only).
- **Google Authentication** — Google Sign-In integrated with Firebase Auth (`GoogleAuthProvider`); requires `google-services.json` and matching SHA-1 in Firebase Console.
- **Dashboard** — Central hub with time-based greeting, quick actions (create shipment, shipments list, track, orders), horizontal product catalog, hero card for the latest local shipment, theme toggle, analytics entry, and AI assistant FAB.
- **Create Shipment** — Form for sender, receiver, weight, pickup/delivery addresses, and optional package image; shipping cost is calculated as `weight × 50` (₹) before checkout.
- **Track Shipment** — Lookup by numeric ID (1–20); uses FakeStore product data to display simulated tracking details; remembers the last successful search.
- **Orders** — Lists carts from FakeStore API in a `RecyclerView`; tapping an order opens shipment/order details.
- **Payment Flow** — Checkout screen with UPI, card, and cash-on-delivery options; payment is simulated via `POST /carts` on FakeStore; successful payments generate a tracking ID and save the shipment locally.
- **SharedPreferences** — App preferences and lightweight session/cache data (theme, launch stats, profile cache, last tracking ID).
- **API Integration** — Retrofit + Gson client against `https://fakestoreapi.com/` for products, carts, users, and simulated payments.

**Also included in the codebase:**

- **Shipments list** — Room-backed list of user-created shipments.
- **Shipment / product details** — Detail screen for products (from dashboard), carts (from orders), or legacy tracking IDs.
- **Order confirmation** — Post-payment summary with tracking ID, payment method, and amount.
- **Analytics** — Dashboard metrics derived from FakeStore cart data.
- **AI Assistant** — Chat UI with context from local shipments and FakeStore products (requires configured AI backend in `ai/` package).

---

## Tech Stack

| Layer | Technology |
|--------|------------|
| Language | **Kotlin** |
| UI | **XML** layouts, Material 3, `ConstraintLayout` / `LinearLayout` |
| Auth | **Firebase Authentication**, Google Play Services Auth |
| Networking | **Retrofit**, OkHttp logging, **Gson** |
| API | **FakeStore API** (`fakestoreapi.com`) |
| Local storage | **SharedPreferences**, **Room** (shipments database) |
| Lists | **RecyclerView** with custom adapters |
| Async | **Kotlin Coroutines**, `lifecycleScope` |
| Images | **Coil** |
| Build | Android Gradle Plugin, `minSdk 24`, `targetSdk 35`, JVM 17 |

---

## App Flow

```
SplashActivity (2s)
    ├── Firebase user signed in → DashboardActivity
    └── Not signed in → LoginActivity
            ├── Sign up → SignupActivity → DashboardActivity
            ├── Email login → DashboardActivity
            └── Google sign-in → DashboardActivity

DashboardActivity
    ├── Create Shipment → CreateShipmentActivity → PaymentActivity
    │       → OrderConfirmationActivity → DashboardActivity
    ├── Shipments → ShipmentsActivity (Room list)
    ├── Track Shipment → TrackShipmentActivity
    ├── Orders → OrdersActivity → ShipmentDetailsActivity (cart mode)
    ├── Profile → ProfileActivity → Logout → LoginActivity
    ├── Product tap → ShipmentDetailsActivity → CreateShipmentActivity (optional)
    ├── Analytics → AnalyticsActivity
    └── AI Assistant FAB → AiAssistantActivity
```

Authentication state is checked on splash via `FirebaseAuth.getInstance().currentUser`. Most secondary screens use the system back button or explicit navigation to return to the dashboard.

---

## Folder Structure

```
app/src/main/
├── java/com/example/madecie3/
│   ├── Activities (screens)
│   ├── api/              # Retrofit client & FakeStoreApi
│   ├── data/             # Room database, DAO, entities
│   └── ai/               # AI assistant client & context
└── res/
    ├── layout/           # Activity & item XML layouts
    ├── drawable/         # Buttons, cards, payment icons
    └── values/           # colors, strings, themes
```

### Activities

| Activity | Role |
|----------|------|
| **SplashActivity** | Launcher; applies saved theme, increments launch count, routes to Dashboard or Login after 2 seconds based on Firebase session. |
| **LoginActivity** | Email/password and Google sign-in; theme toggle; navigates to signup or dashboard. |
| **SignupActivity** | Firebase `createUserWithEmailAndPassword`, display name update, optional avatar picker (UI only). |
| **DashboardActivity** | Main hub: navigation, product list, latest shipment hero, theme toggle, analytics & AI entry. |
| **CreateShipmentActivity** | Shipment form and image picker; forwards cost and address data to payment. |
| **PaymentActivity** | Payment method selection, simulated API payment, Room insert, then order confirmation. |
| **TrackShipmentActivity** | Product ID lookup against FakeStore; persists last tracking ID in SharedPreferences. |
| **OrdersActivity** | Fetches carts from API; displays with `CartAdapter`. |
| **ProfileActivity** | Shows Firebase user info, caches name/email, theme toggle, logout (clears prefs + Firebase sign-out). |
| **ShipmentsActivity** | Lists all shipments from Room via `ShipmentAdapter`. |
| **ShipmentDetailsActivity** | Product, cart/order, or tracking-ID detail modes. |
| **OrderConfirmationActivity** | Shows tracking ID, payment method, amount; returns home. |
| **AnalyticsActivity** | Displays cart-derived delivery/RTO-style stats. |
| **AiAssistantActivity** | Chat interface using shipment and product context. |

### Adapters

| Adapter | Used by | Purpose |
|---------|---------|---------|
| **ProductAdapter** | Dashboard | Horizontal product cards; opens product details on click. |
| **CartAdapter** | Orders | Renders FakeStore carts; opens order details on click. |
| **ShipmentAdapter** | Shipments | Renders locally saved `ShipmentEntity` rows. |
| **ChatAdapter** | AI Assistant | Chat message list. |

`OrderAdapter` exists in the project but is not wired to any activity; orders use `CartAdapter`.

### XML layout files

Layouts follow the `activity_<screen>.xml` and `item_<row>.xml` naming convention:

- **Auth:** `activity_splash.xml`, `activity_login.xml`, `activity_signup.xml`
- **Core flow:** `activity_dashboard.xml`, `activity_create_shipment.xml`, `activity_payment.xml`, `activity_order_confirmation.xml`
- **Shipments & tracking:** `activity_shipments.xml`, `activity_track_shipment.xml`, `activity_shipment_details.xml`
- **Orders & catalog:** `activity_orders.xml`, `item_order.xml`, `item_product.xml`, `item_shipment.xml`
- **Profile & extras:** `activity_profile.xml`, `activity_analytics.xml`, `activity_ai_assistant.xml`, `item_chat_message.xml`

Drawables under `res/drawable/` provide industrial-style cards (`bg_card_industrial`), auth form styling (`bg_auth_form_card`, `bg_auth_input`), and payment option assets.

---

## API Usage

All HTTP traffic goes through `RetrofitClient` with base URL `https://fakestoreapi.com/`.

### FakeStore API

| Endpoint | Usage in app |
|----------|----------------|
| `GET /products` | Dashboard product carousel; AI context; rates-style screens in codebase |
| `GET /products/{id}` | Track shipment lookup by numeric ID |
| `GET /carts` | Orders list; analytics metrics |
| `GET /users` | Support/webhook-style activities (present in code, not in main manifest flow) |
| `POST /carts` | **Payment simulation** — sends `PaymentRequest`; response `id` helps build a tracking ID |

### Payment simulation

`PaymentActivity` builds a `PaymentRequest` (fixed `userId`, current date, mock cart line item) and calls `simulatePayment()`. On success:

1. A tracking ID is generated (e.g. `TRK` + API cart id + random suffix).
2. A `ShipmentEntity` is inserted into Room.
3. `OrderConfirmationActivity` displays the result.

This is a **demo flow**, not a real payment gateway.

### Product fetching

`DashboardActivity` loads products on a background coroutine, shows a progress indicator, and binds results to `ProductAdapter`. Failures show an inline error message. Product images load with Coil.

---

## Local Storage

### SharedPreferences (`prefs`)

| Key | Written by | Purpose |
|-----|------------|---------|
| `app_theme` | `ThemeUtils` | `"dark"` (default) or `"light"` |
| `app_launch_count` | `SplashActivity` | Incremented on each app launch |
| `last_destination` | `SplashActivity` | Last routing target activity name |
| `last_tracking_id` | `TrackShipmentActivity` | Last successful track search (pre-fills input) |
| `cached_name`, `cached_email` | `ProfileActivity` | Profile display cache |

On logout, `ProfileActivity` clears the entire `prefs` file before signing out of Firebase.

### Room (`logistics_db`)

Table `shipments` stores user-created shipments after payment:

- Sender, receiver, pickup/delivery addresses, weight, cost, tracking ID, payment method, timestamp

Used by `ShipmentsActivity`, dashboard hero card, and AI assistant context.

---

## UI Design

- **Dark theme by default** — Industrial zinc palette (`dark_bg`, `dark_card`, slate accents) with red primary accent (`#E53935`).
- **Light/dark toggle** — Available on login, dashboard, and profile via `ThemeUtils` and `AppCompatDelegate`.
- **Logistics-inspired UI** — Card-based layouts (`bg_card_industrial`, `bg_auth_form_card`), checkout total card, payment method tiles with custom icons (UPI, card, COD).
- **Responsive structure** — `ScrollView` on longer forms; horizontal `RecyclerView` for products; `ConstraintLayout`/`LinearLayout` patterns with consistent padding and typography.

Material 3 `Theme.MADECIE3` (`DayNight.NoActionBar`) drives system colors and control highlights.

---

## Architecture Summary

### Frontend structure

- **Single-activity-per-screen** pattern with explicit `Intent` navigation.
- **XML + Activity** presentation layer; business logic lives in activities and small utilities (`ThemeUtils`).
- **RecyclerView adapters** decouple list UI from API/Room models.
- **Coroutines** (`lifecycleScope`) for network and database work off the main thread.

### API integration flow

```
Activity → RetrofitClient.api → FakeStoreApi (suspend)
         → Response<T> → UI update / Room save
```

 Gson models live in `FakeStoreApi.kt`. OkHttp logging is enabled at `BODY` level for debugging.

### Firebase authentication flow

```
LoginActivity / SignupActivity
    → FirebaseAuth (email/password or Google credential)
    → On success → DashboardActivity
SplashActivity
    → currentUser != null → skip login
ProfileActivity
    → auth.signOut() + clear prefs → LoginActivity
```

Google Sign-In requires `default_web_client_id` from `google-services.json` and correct Firebase project configuration (package name + SHA-1).

---

## Getting Started

### Prerequisites

- Android Studio (recommended: latest stable)
- JDK 17
- Android SDK 35
- Firebase project with Authentication enabled (Email/Password + Google)
- `google-services.json` in `app/`

### Run the app

1. Clone or open the project in Android Studio.
2. Sync Gradle and ensure `google-services` plugin applies successfully.
3. For Google Sign-In on a device/emulator, add your app SHA-1 to Firebase (debug SHA-1 is logged from `LoginActivity` on startup).
4. Run on a device or emulator with internet access (`INTERNET` permission is declared).

### Build

```bash
./gradlew assembleDebug
```

---

## Project Info

| Item | Value |
|------|--------|
| Application ID | `com.example.madecie3` |
| Min SDK | 24 |
| Target SDK | 35 |
| Version | 1.0 (versionCode 1) |

---

## License

This project is provided as an academic / demonstration Android application. Add a license file here if you distribute or open-source the repository.

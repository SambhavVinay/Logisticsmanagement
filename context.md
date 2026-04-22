# ShipWise Logistics - Project Context

## High-Level Overview
ShipWise Logistics is a professional, high-density Android application designed for streamlined logistics management. It prioritizes a minimal, sleek, and premium aesthetic (no emojis, professional typography, and cohesive dark/light themes).

## Core Architecture

### 1. Data Layer (Room Persistence)
- **AppDatabase**: The central Room database instance.
- **ShipmentEntity**: Stores detailed shipment records including:
  - `trackingId`: Unique identifier (e.g., `TRK123` or `TRKAI123`).
  - `sender`/`receiver`: Contact details.
  - `pickupAddress`/`deliveryAddress`: Location data.
  - `weight`: Measured in Kilograms (KG).
  - `cost`: Calculated at a flat rate of ₹50/KG.
  - `timestamp`: Record creation time.
  - `paymentMethod`: Method used (e.g., `Cash`, `Card`, or `AI Managed`).
- **ShipmentDao**: Data access object for retrieving all shipments (sorted by time) and inserting new records.

### 2. Networking & APIs
- **Retrofit / OkHttp**: Used for all external communications.
- **FakeStoreApi**: Integrates with `fakestoreapi.com` to fetch a live product catalog for the dashboard.
- **NIM AI API**: Powers the AI Assistant using the `google/gemma-2-9b-it` model via NVIDIA's API surface.

### 3. AI Assistant (Logistics Concierge)
- **AiAssistantActivity**: A refined chat interface that avoids traditional bubbles for a textual "concierge" experience. Features a "Thinking" animation and professional status management.
- **AiContextManager**: Dynamically builds system prompts by injecting:
  - Global app rules (rates, tracking formats).
  - Current product inventory from the web API.
  - Local shipment history from the Room database to provide "memory" to the AI.
- **Action Tag System**: A specialized protocol allowing the AI to perform autonomous actions.
  - Format: `[ACTION:CREATE_SHIPMENT|SENDER:x|RECEIVER:y|...]`
  - Function: When detected, the app silently logs a shipment to the local database and suppresses the tag from the user's view.

### 4. UI & Theming
- **ThemeUtils**: Manages the dynamic light/dark mode transition.
- **Aesthetic Principles**:
  - Industrial, high-density grid layouts.
  - Strict professional tone (No decorative emojis).
  - Text-based iconography and sleek card designs (`bg_card_sleek`).

## Key Feature Flow
1. **User Auth**: Professional Login/Signup with profile image support.
2. **Dashboard**: Unified view of quick actions (Create, Manage, Track) and an inventory catalog.
3. **Shipment Creation**: 
   - **Manual**: Via `CreateShipmentActivity` form.
   - **Autonomous**: Via chat with the AI Assistant.
4. **Management**: `ShipmentsActivity` provides a comprehensive history of both user-created and AI-managed logistics.
5. **Tracking**: Dedicated `TrackShipmentActivity` for real-time status updates (simulated).

## Technical Constants
- **Shipping Rate**: ₹50 / Kilogram.
- **Tracking ID Format**: `TRK` + 5 digits (User) | `TRKAI` + 5 digits (AI).

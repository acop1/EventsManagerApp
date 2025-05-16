# EventsManagerApp

# Events Manager Application

A comprehensive event management Android application that allows users to create, manage, and track events with both public and private visibility options.

## Features

### Core Functionality
- **User Authentication**: Secure login and registration system
- **Event Management**:
  - Create public/private events with details (title, date, time, location, description)
  - Edit existing events
  - Delete events
- **Saved Events**: Bookmark interesting events for quick access
- **Invitation System**: Manage invitations for private events
- **Search**: Find events by keywords
- **Event Categories**: 
  - Upcoming vs past events
  - Public vs private events

### Technical Highlights
- MVC Architecture with Clean Code principles
- Firestore backend for real-time data sync
- Modern Android components:
  - ViewBinding
  - RecyclerView with adapters
  - Fragments with navigation
- Comprehensive input validation

## Installation

### Prerequisites
- Android Studio (latest version)
- Android device/emulator with API level 24+
- Google Firebase account for backend services

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/events-manager.git
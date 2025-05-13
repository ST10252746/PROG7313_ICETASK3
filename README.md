
<img src="images/PixlyLogo.png" alt="Pixly Logo" width="200"/>

# Pixly – A Social Media App

Pixly is a modern, Firebase-powered social media application built with Kotlin in Android Studio. It allows users to share photo posts, engage with others, update profiles, and enjoy a smooth, aesthetic UI experience. This project follows a sprint-based agile development approach.

> [![YouTube](https://img.shields.io/badge/Demo%20Video-Watch%20on%20YouTube-red?logo=youtube&logoColor=white)]

---

## 🎨 Color Scheme

| Color Name        | Hex Code   |
|-------------------|------------|
| Light Blue        | `#ADD8E6`  |
| Light Sea Green   | `#20B2AA`  |
| Midnight Blue     | `#191970`  |
| Medium Turquoise  | `#48D1CC`  |

---

## 🚀 Key Features

- Firebase Authentication (Email/Google)
- Firestore integration for posts and user data
- Image uploads with Firebase Storage
- Realtime feed with likes & comments
- User profiles and profile picture updates
- Post creation via CameraX or Gallery
- Bottom navigation bar with Material UI
- Light/Dark mode support
- Pull-to-refresh for latest posts
- Post and user search functionality
- Ranked feed algorithm (optional enhancement)

---

## 🧩 Sprint Breakdown

### ✅ Sprint 1: User Auth + Firebase Setup
- Project initialized with Kotlin + XML
- Firebase Authentication, Firestore, and Storage integrated
- User Registration/Login (Email/Google)
- Store user info (name, email, profile pic)
- Designed Login and Signup screens

### ✅ Sprint 2: Post System
- Post creation screen UI
- Bottom Navigation Bar (Home, Back, Profile)
- Image capture (CameraX) and Gallery access
- Upload image as base64 to Firebase
- Store: image, caption, user ID, timestamp
- Display post feed using RecyclerView

### ✅ Sprint 3: Engagement & Profiles
- Like/Comment functionality on posts
- Comments stored in nested Firestore collections
- User profile screen shows only their posts
- Update profile picture, name, password
- UI updates for post layout to show interactions

### ✅ Sprint 4: UI & Navigation
- Username-based user search
- Tabbed post search interface
- Floating Action Button for quick post creation
- Light/Dark Mode using SharedPreferences
- Pull-to-refresh on post feed

---

## ⚙️ Tech Stack

- **Language:** Kotlin
- **IDE:** Android Studio
- **Backend:** Firebase (Auth, Firestore, Storage)
- **UI:** Material Design Components
- **Libraries:** CameraX, Glide/Picasso (optional), Firebase SDKs

---

## 👥 Authors

- **ST10252746 - Theshara Narain**
- **ST10266994 - Alyssia Sookdeo**
- **ST10251759 - Cameron Chetty**

---

## 📦 How to Run

1. Clone the repository.
2. Add your `google-services.json` file to the `app/` directory.
3. Sync Gradle and run on emulator or device.
4. Create a Firebase project and enable Authentication + Firestore + Storage.

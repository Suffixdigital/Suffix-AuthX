# Smart Authenticator – Android (Kotlin + Firebase)

A modular and production-ready **multi-provider authentication boilerplate** for Android.  
Built with **MVVM architecture** and **Firebase Authentication**, supporting secure login via Email/Password, Phone (OTP), Google, Facebook, and Twitter.

---

## Project Badges

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Kotlin-blue.svg)
![License](https://img.shields.io/github/license/Suffixdigital/Smart-Authenticator)
![Stars](https://img.shields.io/github/stars/Suffixdigital/Smart-Authenticator?style=social)

---

## Overview

Smart Authenticator is designed to help Android developers integrate authentication flows faster.  
It provides a **clean MVVM-based structure**, **pre-configured Firebase integration**, and **common third-party login providers** out-of-the-box.

**Use cases:**
- Apps needing quick integration of user authentication
- Projects requiring scalable and reusable auth modules
- Prototyping or production-ready implementations with Firebase

---

## Features

| Category              | Feature Description                                                                 |
|-----------------------|-------------------------------------------------------------------------------------|
| Authentication        | Email/Password, Phone (OTP), Google, Facebook, Twitter                              |
| Session Handling      | Firebase Auth state listeners with auto-login and auto-logout support               |
| Architecture          | Clean MVVM pattern with ViewModels, LiveData, and Repositories                      |
| Deep Links            | Handles social login redirects and intent handling                                  |
| Input Validation      | Realtime validation with `TextWatcher`                                              |
| Network Management    | Internet connectivity checks with fallback handling                                 |
| UI                    | Material Design components, Dark & Light mode support                               |
| Secure Logout         | Sign out from Firebase and all linked providers                                     |
| Analytics Ready       | Easily extendable with Firebase Analytics                                           |

---

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model–View–ViewModel) + LiveData
- **Firebase Services:**
  - Authentication (Email, Phone, OAuth)
- **Third-Party SDKs:**
  - Google Sign-In
  - Facebook SDK
  - Twitter Kit
- **UI:**
  - Material Components
  - ConstraintLayout
  - Dark Mode Support

---

## Getting Started

### Prerequisites

- Android Studio Giraffe or newer
- Firebase project with Auth providers enabled
- OAuth credentials from:
  - Google Cloud Console
  - Facebook Developer Portal
  - Twitter Developer Platform

### Setup Instructions

```bash
git clone https://github.com/Suffixdigital/Smart-Authenticator.git
cd Smart-Authenticator
```

1. Add your `google-services.json` to `app/`
2. Add Facebook & Twitter keys in `strings.xml`
3. Sync Gradle and Run

---

## Project Structure

```
com.suffixdigital.smartauthenticator/
├── auth/               # Authentication logic (ViewModels, Repos)
├── ui/                 # Activities, Fragments
├── utils/              # Network & validation helpers
├── model/              # Data models
└── MainActivity.kt     # Entry point
```

---

## Screenshots

| Main Screen | Phone Number | OTP Verification | Email/Password Login |
|-------------|--------------|------------------|-----------------------|
| ![Main](screenshots/main_screen.png) | ![Phone](screenshots/phone_number_screen.png) | ![OTP](screenshots/otp_screen.png) | ![Login](screenshots/login_screen.png) |

| Registration | Forgot Password | Facebook Login | Facebook Success |
|--------------|----------------|----------------|------------------|
| ![Reg](screenshots/registration_screen.png) | ![Forgot](screenshots/forgot_password.png) | ![FB Login](screenshots/facebook_login_screen.png) | ![FB Success](screenshots/facebook_login_success.png) |

---

## Testing Checklist

- [x] Manual login/signup with all providers
- [x] Auto session resume after app restart
- [x] Network disconnection handling
- [x] Incorrect credentials feedback

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact

**Suffix Digital**  
📧 suffixdigital@gmail.com

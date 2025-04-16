# Smart Authenticator

Smart Authenticator is an Android application developed in Kotlin that provides users with multiple secure and convenient authentication methods. It leverages Firebase Authentication and other social login providers to enable a seamless login experience.

## Features

-   **Email/Password Authentication:** Traditional login using email and password.
-   **Google Sign-In:** Allows users to sign in with their Google accounts.
-   **Facebook Login:** Allows users to sign in with their Facebook accounts.
-   **Twitter Login:** Enables authentication via Twitter accounts.
-   **GitHub Login:** Supports user authentication with GitHub credentials.
-   **Yahoo Login:** Enables users to sign in with their Yahoo accounts.
-   **Microsoft Login:** Supports authentication with Microsoft accounts.
-   **Phone Authentication (OTP):** Verifies user identity via one-time passwords sent to their mobile phone.
-   **Logout Functionality:** Users can sign out of any authentication provider.
- **Email Verification:** Users must verify their email address before accessing the app's main features.
- **Clear Task and New Task Intent Flags:** Ensures proper activity lifecycle management during sign-in and sign-out processes.

## Technologies Used

-   **Kotlin:** The primary programming language.
-   **Android SDK:** For building the Android application.
-   **Firebase Authentication:** For managing user authentication.
-   **Google Sign-In SDK:** For integrating Google sign-in.
-   **Facebook SDK:** For integrating Facebook login.
- **OAuthProvider:** For integrating Twitter, Github, Yahoo and Microsoft sign-in.
- **GoogleSignInOptions:** to configure the Google Sign-In client.
- **FirebaseUser:** to get the current user in the app.
-   **View Binding:** For easy access to UI elements.
-   **LiveData:** For handling data that changes over time.
-   **Lifecycle Components:** For managing the activity and fragment lifecycle.
-   **AppCompat:** For providing backwards compatibility for newer Android features.
-   **Core KTX:** Collection of extension functions for Android.
- **Data Binding:** For binding UI elements in layouts to data sources in your app.
- **Activity:** Base class for all activities.

## Setup Instructions

1.  **Firebase Project:**
    -   Create a Firebase project in the Firebase console.
    -   Add an Android app to your Firebase project.
    -   Download the `google-services.json` file and place it in the `app` directory of your project.
    -   Enable Email/Password, Google, Facebook, Twitter, GitHub, Yahoo, and Microsoft as sign-in methods in the Firebase Authentication section.
    - Configure your app's SHA-1 certificate fingerprint in the Firebase console for each sign in provider.
2.  **Google Sign-In:**
    -   Follow Firebase instructions to set up Google Sign-In for Android.
    -   Make sure to add the web client ID in `strings.xml` (`default_web_client_id`).
    - Refer to [Authenticate with Google on Android](https://firebase.google.com/docs/auth/android/google-signin)
3.  **Facebook Login:**
    -   Create a Facebook Developer account and create a new app.
    -   Configure the app with the necessary credentials.
    -   Add the Facebook SDK for Android to your project.
4. **Twitter, Github, Yahoo, Microsoft Login:**
- No extra configuration need, follow the firebase guidelines to enable OAuthprovider.
5. **Phone Authentication:**
   -Follow the firebase instructions to enable phone authentication.
6. **Dependencies:**
    - Make sure your `build.gradle.kts` contains the following dependencies:

kotlin dependencies { 
// Firebase Authentication with BoM 
implementation(platform("com.google.firebase:firebase-bom:32.8.0")) 
implementation("com.google.firebase:firebase-auth-ktx")   
//Google Login
implementation("com.google.android.gms:play-services-auth:21.1.0")    
//Facebook Login
implementation("com.facebook.android:facebook-login:latest.release")
}

7. **Build:**
- Sync the project with Gradle files and Build the project.

## Usage

1.  Launch the app on an Android device or emulator.
2.  Choose your preferred method from the available options:
    -   **Phone:** Enter your phone number to receive an OTP.
    -   **Email/Password:** Enter your email and password to log in.
    -   **Google:** Click on the "Sign In with Google" button.
    -   **Facebook:** Click on the "Sign In with Facebook" button.
    -   **Twitter:** Click on the "Sign In with Twitter" button.
    -   **GitHub:** Click on the "Sign In with Github" button.
    -   **Yahoo:** Click on the "Sign In with Yahoo" button.
    -   **Microsoft:** Click on the "Sign In with Microsoft" button.
3. Once logged in, the button label will change to `logout`.
4. Click on `logout` button, to logout from the app.

## Project Structure

-   `MainActivity.kt`: Main activity responsible for handling all authentication methods and the UI.
- `OtpSendActivity`: Activity that handle phone authentication.
- `SignInScreen`: Activity that handles email/password authentication.
- `HomeScreen`: Home screen when user has been successfully authenticated.
- `VerifyEmailScreen`: Screen to verify email address.
-   `res/`: Resources such as layouts, strings, and drawables.
-   `build.gradle.kts`: Configuration file for dependencies and build settings.
-   `AndroidManifest.xml`: Application configuration file.

## Contributing

Contributions to Smart Authenticator are welcome! Please feel free to submit a pull request or open an issue to discuss any potential changes.

## License

This project is licensed under the [MIT License](LICENSE) - see the `LICENSE` file for details.

## Contact

[Suffix Digital] - [suffixdifital@gmail.com]

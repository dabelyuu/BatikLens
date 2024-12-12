# BatikLens

This is the public repository for the BatikLens mobile application, developed by the **C242-PS249** team from the Bangkit program.<br>

## Aplication Description
**BatikLens** BatikLens is an Android based application that uses machine learning technology to accurately recognize and classify batik patterns. This app allows users to identify various batik types such as Batik Cendrawasih, Batik Dayak, Batik Geblek Renteng, Batik Megamendung, Batik Parang, Batik Pring Sedapur, Batik Tambal, and Batik Truntum by simply taking a photo with their phone camera. As an interactive educational tool, BatikLens provides detailed information about the history and meaning of each pattern, while also encouraging appreciation for Indonesia's cultural heritage.

## Application Architecture
The Batik Lens application is built using a modular architecture to ensure organized and efficient development. It uses a TensorFlow Lite (TFLite) machine learning model to recognize batik patterns from images captured by the user. This model is directly integrated into the Android project, which is developed using the Kotlin programming language and follows the Model-View-ViewModel (MVVM) architecture to separate application logic from the user interface.

- ### The application architecture includes the following main components:
  **Machine Learning Model (TFLite)**, The TFLite model included in the project is used for image classification. This model is optimized to run on mobile devices, enabling real-time batik recognition through the camera or uploaded images.

  **Backend API and Data Repository**, The app connects to a backend using APIs, managed through classes like **ApiService** and **ApiConfig**. Data retrieved from the API, such as batik metadata or classification results, is processed and temporarily stored in a repository, implemented in files like **DataRepository.kt.**

    - ### MVVM Architectur
  **Model**: Manages the app’s data, such as user entities and API responses.

  **View**: Activities and fragments display the user interface, such as **CameraFragment.kt** for the camera and **HistoryActivity.kt** for viewing history.

  **ViewModel**: Connects the View with the Model and handles business logic through classes like **HomeViewModel.kt** and **AuthViewModel.kt.**


**Camera and Image Classification Feature**, The app uses **ImageClassifierHelper.kt** to classify images. The camera functionality is managed by **CameraFragment.kt**, allowing users to take pictures for batik recognition.

**User Interface (UI)**, The UI is designed using XML to provide a simple and user-friendly experience. Fragments, activities, and adapters are used to enable smooth navigation within the app.


- ### Features of the Batik Lens Application

  **Splash Screen**, This application features a splash screen that appears briefly before navigating to the main page, enhancing the initial user experience.

    * **Introduction Guide**, A tutorial or onboarding feature guides users through the application when they first open it, making it easier to understand and use the available functions.

    * **Theme Adaptation (Light/Dark Mode)**, The application automatically adapts to your device's theme settings, whether light or dark mode, providing a comfortable user interface based on your preferences.

    * **Upload Image from Gallery**, Users can select a batik image from their device's gallery to begin the recognition process.

    * **Capture Image with Camera**, With integrated camera functionality, users can directly take a photo of a batik fabric and analyze it instantly within the app.

    * **Image Analysis**, Once an image is selected or captured, users can send it to a server to identify the batik pattern. The app provides detailed results based on the analysis.

    * **View Batik Information**, Users can access detailed descriptions and historical information about the recognized batik patterns, enriching their knowledge of traditional art.

    * **Search History**, The app keeps track of previously analyzed images, allowing users to revisit their history for convenience.

    * **Personalized User Account**, Users can register, log in, and manage their accounts, enabling a personalized experience across devices.

    * **Settings and Customization**, Offers settings for notification preferences, language options, and other customizations to enhance usability.

    * **Educational Content**, The app includes articles and resources about batik, providing users with insightful knowledge about this traditional art form.
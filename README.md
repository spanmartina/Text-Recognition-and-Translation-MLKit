# Text Recognition and Translation from Images with ML Kit

The development of the Android application was divided into two main processes:
1. One process is responsible for recognizing and extracting text from an image (uploaded from gallery or captured with the camera directly from the app).
2. One process responsible for translating the text recognized in the first process in the target language (English).
   
The application implements Optical Character Recognition technology to extract and recognize text from the input image and translate it to the target language (English) using the ML Software Development Kit.

ML Kit is the mobile SDK that brings Google's machine learning experience to Android and iOS applications. Machine learning models run directly on the user's device, allowing on-device processing without the need for a constant internet connection. The models are downloaded via Goole Play Store.

## Dependecies
    // Machine Learning dependencies
    implementation 'com.google.mlkit:text-recognition:16.0.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2'
    implementation 'com.google.mlkit:language-id:17.0.4'
    implementation 'com.google.mlkit:translate:17.0.1'

Firebase Authentication is used for user authentication and managing user identity within the application.  Firebase Realtime Database is employed for storing and synchronizing data in real-time.

    // Firebase dependencies
    implementation 'com.google.firebase:firebase-database-ktx:20.3.0'
    implementation 'com.google.firebase:firebase-database:20.3.0'
    implementation 'com.google.firebase:firebase-auth:20.0.1'

 The CameraX library simplifies the implementation of camera functionalities in the Android application, abstracting the complexity associated with camera management. More specifically, it is used for camera initialization, image capture, and preview management.

     // CameraX dependencies
    def camerax_version = "1.2.2"
    implementation "androidx.camera:camera-core:${camerax_version}"
    implementation "androidx.camera:camera-camera2:${camerax_version}"
    implementation "androidx.camera:camera-lifecycle:${camerax_version}"
    implementation "androidx.camera:camera-video:${camerax_version}"
    implementation "androidx.camera:camera-view:${camerax_version}"
    implementation "androidx.camera:camera-extensions:${camerax_version}"

## Demo

| Sign-up Activity | Landing Page | Settings Activity |
|---------------------------|---------------------------|---------------------------|
|![WhatsApp Image 2024-02-12 at 12 31 52 (1)](https://github.com/spanmartina/Text-Recognition-and-Translation-MLKit/assets/86255277/ab3cc8a8-e576-42dd-abcb-fb6a07c22de6)|![Image 2](https://github.com/spanmartina/Text-Recognition-and-Translation-MLKit/assets/86255277/ba66e224-03ce-4d1c-96b5-93d69b08e318)|![WhatsApp Image 2024-02-12 at 12 29 57](https://github.com/spanmartina/Text-Recognition-and-Translation-MLKit/assets/86255277/89f62dae-dbe1-4ef2-8481-fe4645d86330)|



| Camera Activity | Preview Page | Translator Activity | 
|-----------------|--------------|-----------------|
|![Image 3](https://github.com/spanmartina/Text-Recognition-and-Translation-MLKit/assets/86255277/580889c3-60d8-4fe9-8d3c-4cdcf1e10dde)|![Image 4](https://github.com/spanmartina/Text-Recognition-and-Translation-MLKit/assets/86255277/7ade1cb7-1931-4993-95ea-e6f2df398e96) |![WhatsApp Image 2024-02-12 at 12 29 56 (2)](https://github.com/spanmartina/Text-Recognition-and-Translation-MLKit/assets/86255277/351b65ab-15b6-4770-bf66-c32dd59ed7f5) | 

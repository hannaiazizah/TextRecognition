# Text Recognition and Location App

This is a text recognition app that uses optical character recognition (OCR) technology to 
extract text from images and captures the location where the image was taken. 
It then calculates the distance and estimated time from that location 
to Plaza Indonesia Jakarta using a car. The extracted text, distance, and estimated time 
are uploaded to Firebase and can be viewed on a second screen as editable text.

## Features
- Take a picture of printed text and the app will extract the text from the image.
- Capture the current location where the picture was taken using GPS.
- Calculate the distance and estimated time from the location to Plaza Indonesia Jakarta using a car.
- Upload the extracted text, distance, and estimated time to Firebase.
- View the extracted text, distance, and estimated time on a second screen as editable text.

## Technologies Used
- Kotlin
- Google Maps API
- ML-Kit OCR
- Firebase
- CameraX

## Installation
- Clone the repository:
  ```bash
  git@github.com:hannaiazizah/TextRecognition.git
  ```
- Import the project into Android Studio.
- Set up a Firebase project and connect the app to the Firebase Realtime Database.
- Enable the Google Maps SDK for Android in the Google Cloud Console and add an API key to the app.

## Usage
- Open the app and grant permission to access the camera and location.
- Take a picture of printed text by clicking the camera button.
- Wait for the app to extract the text from the image and capture the location where the picture was taken.
- View the extracted text, distance, and estimated time on the second screen.
- Edit the text if necessary and click the "Upload" button to upload the changes to Firebase.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
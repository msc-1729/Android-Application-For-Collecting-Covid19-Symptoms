<h2 align="center"> Android-Application-For-Collecting-Covid19-Symptoms </h2>

### Introduction:
This is an Android application that collects COVID-19-related symptoms and stores them in a database on the smartphone. It basically does two measurements which are Heart rate sensing - using the back camera of the smartphone and Respiratory rate which is measured using the accelerometer or the orientation sensor of the smartphone. The application is developed using Android Studio using JAVA language.

### Heart Rate Sensing: 
For heart rate sensing we will utilize the back camera of the smartphone with flash enabled. We will take 45 s video from the back camera with the flash on. While taking the video the user should softly press their index finger on the camera lens while covering the flashlight. From the variation of the red coloration in the image we will derive the heart rate of the subject.
The libraries that are used for accessing the camera are: 
 *android.hardware.camera2.CameraDevice;
 *android.hardware.camera2.CameraManager;

### Respiratory rate:
For respiratory rate sensing we will utilize the accelerometer or orientation sensor of the smartphone. The user will be asked to lay down and place the smartphone on their chest for a period of 45 seconds. The respiratory rate will be computed from the accelerometer or orientation data. 


Both these activities are performed by taking in values for a time of 1 minute, and these values are passed into a function calculation_average. This function uses a data structure queue named Window, that adds all the data received in this period of 1 minute and then calculates the average of all these data values with respect to the window size.

The whole application contains two pages of which one is the main page that displays the options for measuring the heart rate and the respiratory rate of the user. The second page displays a list of symptoms from which the user has to select related symptoms and then rate the severity of the symptoms. The user interface of the application looks as follows:

<img src = "https://github.com/msc-1729/Android-Application-For-Collecting-Covid19-Symptoms/blob/main/assets/Pages%20of%20the%20application.png"/>

#### Functioning of the application is as follows: </br>
The user will be asked to click on the measure heart rate button and measure respiratory rate button to collect data from the smartphone sensors. The numbers will be stored in a database corresponding to the user. In this assignment, we will assume only a single user. The user will then hit the upload signs button which will create a database with the user’s last name in the smartphone. The entry of the database will be a table with the first two columns heart rate and respiratory rate respectively. Each entry of the database will have 10 additional columns which will be filled in the next page.
Once the user is done collecting signs data, the user will be taken to the second page to collect symptoms data. The user will select a symptom and then select a rating out of 5. The user does not need to select all the symptoms. Whichever symptoms the user has not reported will be marked with a 0 rating. After this, the user will click an upload symptoms button. At this point, a database table entry with 12 entries will be created and stored in the database on the smartphone. 

The demo which shows the working and usage of the functionalities of the application can be found at : 
https://youtu.be/qB5b08arvXI







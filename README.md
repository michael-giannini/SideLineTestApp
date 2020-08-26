
# SideLine Test App: User Manual

The SideLine Neurological Test App is designed to administer a battery of tests to athletes to evaluate cognitive effects of sub-concussive and concussive injuries. The tests within the app are designed to be given to athletes before and after activity or injury. The test results of an athlete can be compared before and after injury to evaluate if any cognitive deficiencies have occurred due to the injury.  

This ReadMe will describe the SideLine Test App tests, the outputs, and how to install the app. 

## Getting Started

To install the app, copy the 'SidelineTestApp.apk' file to any directory on your Android phone or tablet. Once copied, navigate to the file on your device and open the file to install the app. Accept all warnings about installing a third part app. 

## Tests Within the App

This section will explain the tests that are available inside the app. 

### Anti-Saccade Test

The Anti-Saccade test contains two sub-tasks, the ProPoint and AntiPoint test. The two tests are variants of each other. In each test, the user firsts presses and holds the middle circle on the screen. After 100ms, a random surrounding square will become solid. In the ProPoint test, the user is required to press the filled-in square where in the AntiPoint test, the user is required to press the hollow square opposite to the solid square. The test will always start with the ProPoint test, and after 48 correct trials, the AntiPoint test will begin. After 48 trials of the AntiPoint test, the test will end and return the user to the main menu.

The test is based on this [paper](https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0057364). 

* #### Test Outputs

Upon completion of the test, a .csv file containing the results will be automatically saved to the /Downloads directory of the device. The file will be named as: "date_time_name_SaccadeTest".csv, where 'date','time','name' will be replaced by the app accordingly. For example, a test that is completed on July 10th, 2020 by John at 10:00am will be saved as 07102020_1000_John_SaccadeTest.csv.

The file will contain the participant's name, date and time the test was completed, total elapsed time, and the metrics that are outline below. The file will also contain the raw data for each trial to identify if any outliers were present. The metrics that will be outputted are:

* **Initiation Time** - the time between when a filled-in square appears and when the user lifts their finger off of the center circle.

* **Movement Time** - the time between the user lifting their finger and pressing the correct square.

* **Total Time** - sum of the initiation and movement time.

 ### Task-Switching Test

The task switching test contains two sub-tests, the Numeric Test and the Spatial Test. In each test, the user is required to provide an input based on what appears on the screen. Each test will have a 'simple' trial period where the user's input will not change for 80 trials. After the simple period, the test will enter the switching trial period where after every 2nd trial the required user's input will switch. 

* #### Numeric Test

During the Numeric Test, a number between 1-9 appears on the screen. During the simple trial period, the user will be instructed to press the '4' button if the number is less than 5 or press the '7' button if the number is greater than 5. This condition is known as the magnitude condition. After the simple period, the task will switch to the parity condition. During the parity condition, the user will be required to press the '-' button if the number is  even, or press the '+' button if the number is odd. After 2 trials of the parity condition, the task will switch after every 2nd trial until 240 total trials have been completed.

* #### Spatial Test

During the Spatial Test, a square will randomly at either the top or bottom of the device's screen. During the simple trial period, the user will be instructed to press the '4' button if the square is on the bottom of the screen or press the '7' button if the square is on the top of the screen. This condition is known as the congruent condition. After the simple period, the task will switch to the incongruent condition where the user will be required to press the '-' button if the square is on the bottom of the screen, or press the '+' button if the square is on the top of the screen. After 2 trials of the congruent condition, the task will switch after every 2nd trial until 240 total trials have been completed.

The test is based on this [paper](https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0091379). 

* #### Test Output

Upon completion of the test, a .csv file containing the results will be automatically saved to the /Downloads directory of the device. The file will be named as: "date_time_name_SpatialTest".csv, where 'date','time','name' will be replaced by the app accordingly. For example, a test that is completed on July 10th, 2020 by John at 10:00am will be saved as 07102020_1000_John_SaccadeTest.csv.

The file will contain the participant's name, date and time the test was completed, total elapsed time, and the metrics that are outline below. The file will also contain the raw data for each trial to identify if any outliers were present. The metrics that will be outputted are:

* **Average Congruent/Magnitude Time** - The average time it took the user to press the correct button after the number or square appeared on the screen during the congruent or magnitude condition.

* **Average Incongruent/Parity Time** - The average time it took the user to press the correct button after the number or square appeared on the screen during the incongruent or parity condition.

* **Average Simple Time** - The average time it took the user to press the correct button after the number or square appeared on the screen during the simple trials.

* **Average Stay Time** - The average time it took the user to press the correct button after the number or square appeared on the screen after maintaining their current trial condition.

* **Average Switch Time**- The average time it took the user to press the correct button after the number or square appeared on the screen after switching from their previous trial condition.

* **Global Cost** - Calculated as the proportional difference in reaction times between simple blocks of trails and the ‘stay’ trials (i.e. trials in which the subject maintained their current mode of responding) during the switch blocks of trials.

* **Switch Cost** - Calculated as the proportional difference in reaction times between simple blocks of trails and ‘switch’ trials (i.e. trials in which the subject switched from responding in one mode to the other) during the switch block of trials

## Version Tracking

As the app reaches milestones, branches shall be created that contain stable and complete builds. The description of these releases are below.
* **v0.1** - Initial release of the app. This version contains functional Anti-Saccade and Task Switching tests. This version contains settings that allow the user to modify the number of trials for each test.
* **v1.0** - No functional changes to the app were made, but MVVM was implemented for all tests.  

# SideLine Test App: Development Guide

This section of the Read Me will describe the the code architecture and should be read before making changes or additions to the app. For excellent tutorials on getting started with Android Studio, see this [page](https://developer.android.com/courses/fundamentals-training/toc-v2).

## Development Environment

This app was created in Android Studio, and any changes to the app should also be made via Android Studio. To work on the app, download all files in the repository and open the project file in Android Studio. 

## App Architecture

The SideLine Test App uses the Model-View-ViewModel (MVVM) architecture with Live Data. See [this example](https://proandroiddev.com/mvvm-architecture-viewmodel-and-livedata-part-1-604f50cda1) for an overview and example of how this is implemented. MVVM is commonly used in industry and is the Google recommended architecture for Android development. This architecture satisfies the Separation of Concerns principle and allows the UI to be driven by a model. This section will provide a brief explanation of how the architecture applies to this app. 

Another good resource to learn more about Live Data can be found [here](https://developer.android.com/topic/libraries/architecture/livedata).

### MVVM Architecture
Below are the key elements of the MVVM architecture. 

* **Model** - Holds the data of the app. Only communicates with the ViewModel.
* **View** - The UI of the app. These classes should only be concerned with UI elements that the user interacts with. The View observes the ViewModel.
* **ViewModel** - The link between the View and the Model. Responsible for transferring data between the View and Model. The View Model does not know the View is listening to it (Data Binding) 

In the project directory, the classes are located in their corresponding folder (model/view/viewmodel).

### Live Data
Live Data allows the ViewModel to pass information to observers, in this case, the View. As part of the architecture, the ViewModel is unaware of the objects which observe it. The View objects retrieve information from the ViewModel with the use of observers.

Observers monitor a live data variable and execute some action when this variable changes. Below demonstrates how this is accomplished programmatically:

In the ViewModel class, a live data variable, `display`,  is defined:
```
private MutableLiveData<String> display;
``` 
A getter is also required for this variable inside the ViewModel:

```
public MutableLiveData<String> getDisplay() {  
    Log.d(LOG_TAG, "Got pass target.");  
 if (display == null) {  
        display = new MutableLiveData<String>();  
  display.postValue("null");  
  }  
    return display;  
}
```

This allows other codebases to observe this variable and execute a function when it changes. The below code accomplishes this and should be placed in a View. 

```
final Observer<String> dispObserver = new Observer<String>() {  
    @Override  
  public void onChanged(@Nullable final String msg) {  
        Log.d(LOG_TAG, "Observed condition change.");  
 if (msg.equals("End Test")) {  
            endTestDialogue();  
  }  
    }  
};
``` 
Note, we override the `onChanged()` method. It is this method that gets called whenever the variable is changed in the ViewModel class with the `.postValue()` method. 

To complete the implementation, we must define the View variable that will observe the ViewModel. This is done by attaching the getter to the variable:

```
mViewModel.getDisplay().observe(this, dispObserver);
```
This implementation is used across all tests in the app and should be used for all future tests that are added. 

### Settings

The settings page can be accessed from the opening screen of the app. When opening the settings page, you are prompted to enter a password to login. The 4-digit key is `1234`. Accessing the page does not reveal any personal or secure information, it is only used to change the length of each test. The default values are 48 Saccade Trials, and 20 and 80 simple and total trials for the Task Switching tests, respectively.

In Android Studio, the .xml file for the setting is located at `res>xml>root preferences`.

### Adding Additional Tests

If additional test are added to the app, they should be added in a manner similar to the existing tests and use the MVVM architecture. Each test should contain an instructions page that appears before the test begins. 

When adding your test to the app, create a `button` inside the `Main2Activity` class. Place this button below the existing buttons. Inside the `Main2Activity` code, create an intent that opens the new test.  The participant name should be included. An example is shown below:

```
//Launch New Test  
public void launchNewTest(View view) {  
    String participantID = mIDEditText.getText().toString();  
 if (checkEmptyText(participantID)) return;  
  Intent intent = new Intent(this, newTestInstructions.class);  
  intent.putExtra(EXTRA_MESSAGE, participantID);  
  startActivity(intent);  
}
```

### Creating an Activity

When creating your new activities and tests, use the `Empty Activity` template for your View class, and simply create individual classes for the ViewModel and Model. To use this template click File>New>Activity>Empty Template and ensure that 'Generate Layout File' is selected.

Classes should be placed in the appropriate existing folders. Folders for the View, Model, and ViewModel exist, and all other classes should be placed in the standalone folder. Within the project, View, ViewModel, and Model folders have been created. When creating a new test, place each file in the respective folder. For other files, place in the `standalone` folder. 

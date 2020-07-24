
# SideLine Test App: User Manual

The SideLine Neurological Test App is designed to administer a battery of tests to athletes to evaluate cognitive effects of sub-concussive and concussive injuries. The tests within the app are designed to be given to athletes before and after activity or injury. The test results of an athlete can be compared before and after injury to evaluate if any cognitive deficiencies have occurred due to the injury.  

This ReadMe will describe the SideLine Test App tests, the outputs, and how to install the app. 

## Getting Started

To install the app, copy the 'SideLineTestApp.apk' file to any directory on your Android phone or tablet. Once copied, navigate to the file on your device and open the file to install the app. Accept all warnings about installing a third part app. 

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

# SideLine Test App: Development Guide

This section of the Read Me will describe the the code architecture and should be read before making changes or additions to the app. 

## Development Environment



# SafeCar application - Smart Car


## ABSTRACT
SafeCar is an application that has been designed for helping the driver in improving its driving style.
On one side it offers an *after trip* inspection of the data reports about past trips, and on the other side provides an *during trip* hint generation engine for correcting the driving style during the driving experience.
In order to provide this service, the application uses two smart object, a *Plug* and a *Wearable*.

## APPLICATION 

### REGISTRATION AND LOGIN
In order to use this application, the driver has to register as an user of the service. 
The Registration and Login procedures can be performed by using the custom application functionality or by using the Facebook, Twitter 
and Google Plus APIs.


### APPLICATION FLOW
At this point the application flow develops in two different ways:if the user is not in the radio scan area of the Plug, she can only navigate data about her history trips or about her profile.
Instead if the user's smartphone is near the Plug, the application automatically notifies the user of being detected; this feature is called Automatic Presence Detection. 
Once the user has been detected the application asks him if she is actually driving. This check is done in order to avoid the application to register the trip of person if she is not actually driving, for example is near the plug of a friend. If the user answers "Yes" the app understands that the trip is beginning and the *Driving Experience* actually begins. 
After this moment the driver will be provided of several hints about how to improve her driving style.

As previously told, the application is built of two main features:

1. **After Trip data presentation:**
The user can inspect these data from a screen generated after the trip has been finished. In this screen the user can see:

 * A GPS trip tracking of his movements during the trip, shown in a custom a google maps widget.
 * A general report about the just finished trip, along with a Driver Safety Index (DSI) that estimates the quality of the drive.

2.  **During Trip functionalities:**
This functionality instead is mainly based on the concept building an engine for estimating a Driver Safety Index by merging different data coming from several data sources. Indeed this sources are the reason that make the app smart.
In fact the app integrates data coming from:

 * **Wearable smart object:** this object has to be weared by the person that uses the app in order to provide data about Personal conditions. After having collected these data, it conveys them to a remote Cloud from which the application can gather data by using standard APIs.
 
 * **Plug smart object:** this object is a plug that has to be inserted in the custom car port and that, being provided of a SIM, has the capability of sending cleaned and custom driving style data to a remote Cloud. This cloud is accessible by using its custom APIs.
 
In order to have a better and more complete understanding of the environment that wraps the customer, the DIMA smart app considers also other data:

 * **Time data:** are used to set the time environment.
 * **Noise data:** serve as an index of the level of noise that is present in the car during the trip.
 * **User data:** these data are provided by the user at the beginning of each driving experience and are used to set the severity of the engine and the hints delivery frequency. 
 
Starting from this heterogeneus data lake, the engine builds a weighted sum of all the contributions given by all the data sources, called Driver Safety Index (DSI). This basic algorithm is easily extensible, because building a more complex one is only matter of replacing the specific piece of code (a method) with another one that takes in input the same data and generates a same type index.

In order to provide the user of an almost continous feedback about his driving style, this engine recomputes the DSI index once every 30 seconds. Its computation is done in a dedicated separate thread that generates the current piece of advice to be sent to the user by simply switching on the value of the DSI.
This hint is conveyed in different ways by:

 * using an application toast.
 * filling a specific screen on the application.
 * using a Text-To-Speech software. For enabling this feature, the application has to be connected with the car bluetooth speakers.



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


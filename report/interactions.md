Interactions
============

User - Controller
-----------------

### User -> Controller ###
* Lights on
* Lights off
* Barriers open
* Barriers close
* Deck open
* Deck close

### Controller -> User ###
User input errors:

* Lights on
* Light off
* Barriers open 
* Barriers closed
* Deck open

Status:

* Lights
	* Off
	* Yellow
	* Red
	* Error
* Barriers
	* Open
	* Closed
	* Error
* Locking Pins
	* Engaged
	* Disengaged
	* Error
* Deck
	* Open
	* Closed
	* Error
* Motor
	* Moving Up
	* Moving Down
	* Stopped
	* Broken
* Brakes
	* On
	* Off
	* Error

Sensors - Controller
--------------------
Sensors are read only, so only sensors -> controller communication is possible.

### Light Sensors ###
4 Light sensors, 1 per sign.
The light sensors are grouped per sign.
Giving a grouped status such as Red, Yellow, Off and Error.

### Barrier Sensors ###
8 Barriers sensors, 2 per barrier.

### Pin Sensors ###
2 Pin sensors, 1 per pin.

### Deck Sensors ###
4 Sensors, 2 for down, 2 for up.

### Motor Sensors ###
We assume 1 sensor.

### Brake Sensors ###
We assume 1 sensor.


Hardware - Controller
-------------------
* Yellow lights on
* Yellow lights off
* Red lights on
* Red lights off
* Barriers open
* Barriers close
* Pins disengage
* Pins engage
* Motor on
* Motor off
* Brakes on
* Brakes off

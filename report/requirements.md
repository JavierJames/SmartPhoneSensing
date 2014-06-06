Requirements
============

Functional Requirements
-----------------------
* Cars and boats should have the possibility to safely pass the bridge.
* Through emergency controls the security system should be overridable.
	The system should only be used for real emergencies and not during normal operation.


Safety Requirements
-------------------
* The safety should be able to handle inconsistent sensor input.
* If none of the lights switch on, on one side of the bridge, the barriers are not allowed to close.
* Road traffic may not cross the bridge if the locking pins are not engaged.
* All traffic should remain at safe distance while bridge is in transition.
* In an emergency (e.g. power failure) the system should be able to return to a safe state.
* Do not turn the engine off before the brakes are applied.

Specifics
--------
* The locking pins should only activate when the bridge is closed.
* Do not open barriers unless bridge is locked.


Use Cases
---------
Opening the bridge:

1. A boat signals the bridge opening.
2. The lights turn orange for long enough to alert cars.
3. The lights turn red and the entering barriers start closing.
4. Wait for long enough to empty the bridge.
5. Close all barriers.
6. Unlock locking pins. And release bridge brakes.
7. Start motor.
8. Wait until bridge completely open.
9. Engage brakes.
10. Turn motor off.

Closing the bridge:

1. A boat has just passed bridge.
2. Start motor.
3. Disengage brakes.
4. Wait until bridge completely closes.
5. Turn motor off.
6. Engage brakes.
7. Lock locking pins.
8. Open barriers.
9. Turn lights off.


A bridge is close when cars can pass the bridge and the bridge is open when boats can pass the bridge.


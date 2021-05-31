### GUIStopwatch

A stopwatch written in Java (optimized for cubing).
Inspired by cubetimer.com.
Note: cubetimer.com stops the timer upon key release,
not key depress as it probably should.
This Java application "fixes" that issue.

#### Prerequisites:
* Make  
* Java  

#### Usage:
* Build (makes executable jar):  
  <code>make</code>
* Run:  
  <code>make run</code>
* Clean (excluding jar):  
  <code>make clean</code>
* Clean everything (including jar):  
  <code>make reset</code>

#### Known "features":
* Pressing and _holding_ down a valid key when stopwatch is running will cause displayed data to accumulate incorrectly.  

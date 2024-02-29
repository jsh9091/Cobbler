       IDENTIFICATION DIVISION.
       PROGRAM-ID. MathTest.
       AUTHOR. Joshua Horvath.
       DATE-WRITTEN. March 16, 2021
       ENVIRONMENT DIVISION. 
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 Number1 PIC 9 VALUE 2.
       01 Number2 PIC 9 VALUE 5. 
       01 TheAnswer PIC S99V99 VALUE 0.

       PROCEDURE DIVISION. 

       COMPUTE TheAnswer = Number1 + Number2
       DISPLAY "COMPUTED: " Number1 " + " Number2 " = " TheAnswer

       ADD Number1 TO Number2 GIVING TheAnswer
       DISPLAY "ADDED:  " Number1 " + " Number2 " = " TheAnswer

       SUBTRACT Number1 FROM Number2 GIVING TheAnswer
       DISPLAY "SUBTRACTED:  " Number2 " - " Number1 " = " TheAnswer

       MULTIPLY Number1 BY Number2 GIVING TheAnswer
       DISPLAY "MULTIPLIED:  " Number1 " x " Number2 " = " TheAnswer

       COMPUTE TheAnswer = Number2 / Number1
       DISPLAY "COMPUTED: " Number2 " / " Number1 " = " TheAnswer

       COMPUTE TheAnswer = Number2 ** 2
       DISPLAY Number2 " to the second power = " TheAnswer

       COMPUTE TheAnswer = (5 * Number1) - Number2
       DISPLAY "(5 * 2) - 5 = " TheAnswer

       STOP RUN.


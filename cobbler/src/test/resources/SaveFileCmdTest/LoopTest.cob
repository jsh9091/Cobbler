       IDENTIFICATION DIVISION.
       PROGRAM-ID. LoopTest.
       AUTHOR. Joshua Horvath.
       DATE-WRITTEN. March 18, 2021
       ENVIRONMENT DIVISION. 
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 IndexValue PIC 9(1) VALUE 0.
       PROCEDURE DIVISION. 

       MainParagraph.
	      PERFORM TheAdd WITH TEST AFTER UNTIL IndexValue >5.
	      STOP RUN.
       TheAdd.
	      DISPLAY "Index value is " IndexValue.
	      ADD 1 TO IndexValue.

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/S3JgeyUd)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=17294719)
Setup Steps:

Go to the ports tab. Click "Add port" and type the number 6080.
Follow the link to your forwarded address. This is where your GUI should be available once you run your code. NOTE - there may be some delay while using the GUI. 

Programming Instructions: (All code should be added within GameState.java)
DONT TOUCH THE GUI FILE (unless you are feeling very confident)

Part 3
    0. Check out the additional resources below. 
    1. Copy over old code for the first 4 methods
    2. TEST! See how things look in the GUI. Does the initial state work?
    3. TEST! Does drawFromDeck work? Click the deck icon to see!
    4. Implement canCardMove
    5. Implement moveCardFromVisibleCardsToPile
    6. Implement moveCards
    7. Add your own card image! To do this, replace the image card_back.png

Part 4
    0. Crtl-F "Your name" and change to your name in the GUI 
    1. Update drawFromDeck - This is an oversight on Mr. Brown's part - sorry! Please make sure that discardCards is called before adding a new set of 3 card. When the deck is empty, call discardCards and then move all cards from discardPile to the deck. 
    2. Implement canMoveToFoundation
    3. Implement moveToFoundation
    4. Implement moveToFoundationFromVisibleCards
    5. Test, test, test! There are bound to be bugs, see what you can find!

EXTRAS [Not required]

    1. If you are feeling adventurous, here are 3 ideas for cool extensions to add. 
    2. Game reset button
    3. Dragging cards instead of click click
    4. Autoplay - can you create a button that has that automatically makes a move? 

Additional Resources:

https://www.geeksforgeeks.org/stack-peek-method-in-java/
https://www.geeksforgeeks.org/java-program-to-define-ordinal-method-using-enum-concept/


Testing: To test your code, open the GUI you create! Play sample games, see what works and what doesn't!

Submission: Run the following 3 commands in the terminal:

git add .
git commit -m "Submitting my code"
git push
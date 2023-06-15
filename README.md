# Programming  Portfolio

## Introduction
Welcome to my programming project portfolio! Here you'll find a collection of some of my diverse and relevant programming projects; I've ensured to include an array of projects that showcase my capability over all aspects of web and full-stack development. You'll find my take on the important but often overlooked design and prototyping phase, all the way to my understanding of creating fully fledged full-stack applications, and then the web analytics to ensure the site is active and in use.

In this ZIP file, you should find nine directories:
> ascii-uno
> design-and-prototyping
> diabyte
> dna
> filter
> finance
> monopoly
> speak-up
> unity-rpg

I will go through them one by one in this README file.

## ascii-uno

UNO is hands-down my favorite card game; it's a simple classic, that everyone knows. For that reason, I wanted to recreate the game using only Python. I wanted to implement Object Oriented Programming concepts in the creation, to make the program as readable as possible. The program uses ASCII characters to visually represent UNO cards on the terminal. The result emulates a real game of UNO, played with a bot. You can run the code by running `project.py`. I recommend ensuring line-wrapping on the terminal is **disabled**. This can be done in different ways depending on the IDE, but in VS Code the shortcut for Mac is <kbd>Alt</kbd>+<kbd>Z</kbd>. I believe this project shows my ability to create programs from scratch, using readable code and good coding practices.

## design-and-prototyping

### Designing a Vocabulary Web-app

As ardent enthusiast of both writing and programming, the prospect of creating a vocabulary web-app has always excited me. In order to satisfy this desire, I have started development on a vocabulary web-app that will prepare students for the SAT Reading, Writing and Language, and GRE Verbal Reasoning.

One thing I wanted to prioritize whilst creating this app is UI; the product has to be visually appealing, and contain illustrations for every word. This is because I find that associating words some kind of a visual counterpart helps me memorize them. 

For that reason, the design phase of the app is crucial, and must be thoroughly thought-out in order to ensure the user's experience is exceptional. In addition, the app has to set itself apart from the hundreds of already existing vocabulary apps out there.

In the `design-and-prototyping` directory, you'll find a subdirectory named `design`. In this subdirectory, you'll find three files:
> design.fig
> design.pdf
> VAS_Report.pdf

If you have Figma, open the `design.fig` file. Otherwise, you can open the `design.pdf` file, which has the same slides but in a PDF format. It is not interactive, however.

This is initial design for the vocabulary web-app. It is far from complete; it has only three flashcards, but I am proud of it for a first draft.

Now, open the `VAS_Report.pdf` file. This is a report generated by 3M's `Visual Attention Software`, which uses AI to determine which areas the user is most likely to look at. I am happy with the results, as all the hotspots I wanted the user to look at, were classified as active by the AI. However, I am unhappy with the order of the gaze sequence (the order in which the user looks at certain areas), so that may be an improvement for the next draft.

### Turning the Design Into a Prototype

Now, it's time for turning the design into a semi-functional prototype. Navigate to the `vocabulary-webapp` subdirectory, which is just out of scope of the current one. In order to run the React app, navigate to the current directory on the terminal, and run `npm run start`. If you get an error, ensure you have all the necessary modules installed. If not, you should be able to see URL which you can open to view the web-app.

Notice a few stylistic changes, such as the modified background color to make it look more like wood, or coffee. Other than that, the prototype and design are identical. Also note that not all of the features work; you can swap through flashcards, but the other calls to action (i.e. more info, heart, and share, and listen to pronunciation) don't work. In the actual web-app, I would properly implement these features, after peer-review and modifying the design. In the case of DIAMUN, peer review would be review by other departments, such as the media and events departments.

## DI@BYTE

This project was a timed programming task for DI@BYTE. The purpose of the app was to create a financial aid app that allows users to keep track of their savings. The app retains information so that even after reset, the program will still keep track of user information. To use the program, simply run the `base.py` file. This project showcases my understanding of SQL databases, and working under time pressure.

## dna

This project was the product of a CS50 problem set. It reads information from databases and determines matches in DNA from a given DNA sequence. As you can see, the code is filled with comments and is very readable. In order to run this, just run the `dna.py` file. This project shows my ability to make readable code, which comes in handy when collaborating with others. It also shows my ability to understand and implement harder tasks, while adhering to the given criteria.

## filter

This project was a CS50 problem set problem, which asked for the creation of a program to filter images with certain functions (blur, edge detection, grayscale, reflect). This on its own is relatively easy, using APIs. However, using external image-filter APIs was not permitted. In addition, the task was given to be coded in C, a relatively low-level language. To run the program, use the format `./filter [flag] infile outfile`, where [flag] is the first letter of the filter name (i.e. b, e, g, r), infile is the input file, and outfile is the output file. This project showcases my ability to use lower-level languages, and implement complicated tasks.

## finance

This project is a full-stack application that emulates a stock market trader, using `Flask` as a backend Python framework. This was also a project for CS50. In order to run the app, make sure all the requirements in `requirements.txt` are install. The app won't run otherwise. Then, navigate to the `app.py` file. Find the 13th line:

```py
os.environ["API_KEY"] =  "YOUR_API_KEY"
```

And replace `YOUR_API_KEY` with a valid API key. API keys can be generated for free, at `iexcloud.io`. Once this is done, run the app by navigating to the correct directory in the terminal and running `flask run`. This shows my ability to create a functional full-stack application.

## monopoly

### Breaking a Tough Problem Down

Not too long ago, I had an idea: recreate the board game `Monopoly` in code.

Of course, that's easier said than done.

The idea sparked when I was learning about `singly linked lists`, a basic data structure. Singly linked lists are essentially nodes with pointers to their successive nodes. Singly linked lists have a clearly defined starting node (known as the `head`) and ending node (which points to nothing). 

I wondered if it was possible to have the last node point to the first node, so that the result resembled a loop. An analogy for this is a monopoly board; `GO` is the starting point for all players, and `boardwalk` is the last. However, when players reach `boardwalk`, the game isn't yet over. They can cross back to the start!

This is actually a real data structure, with many real-life use cases in computer science. It's known as a `circular linked list`, and works in the same way as the places on a Monopoly board.

Because of this connection I had in my mind, I tried to make Monopoly run on an IDE like VS Code. However, for a larger task like this, I had to split the problem into smaller pieces; especially since I was starting from scratch. I split it into a variety of functions, starting off with pseudocode. Object Oriented Programming concepts really came in handy here. 

### Creating the Game

Eventually, I got something I was happy with, with a rudimentary opponent AI. The code is entirely in Java. It takes user input and the AI's moves and transfers it to a `.md` file. This file then visually represents the board at any given state, using nothing but ASCII characters.

To test the game, navigate to the current directory, and enter `java Game.Game` in the terminal. If this returns and error, the file may not be properly compiled, however it *should* run properly. Make sure to open the `.md` file in **preview** mode, whilst still keeping the terminal open. You should see changes in the board reflected live, on the .md file. If not, try restarting. 

I believe this project speaks a lot to my passion for programming; it wasn't a problem set for any course, or a leetcode puzzle or a challenge from a friend. It was an idea, from me, because I thought it would be fun. In addition, it shows that I can learn new programming languages quickly (as I was relatively new to Java when I started this) and I can program original ideas (as, again, this was completely from scratch).

## speak-up

Like the vocabulary web-app, this project was also a product of my two passions; programming and writing. This site was designed using React as a front-end framework, and showcases poetry from students around DIA.

The website uses React, which is also a framework used in the DIAMUN website. It has gone through the entire design cycle, and has come out quite well. It is responsive, aesthetically pleasing, and has noticeable calls to action, which are all great elements to have in a good website. In addition, I have set up Google Analytics, to get a sense of how many user's I'm getting, and the average retention rate.

I believe this project shows both my experience and passion for web-development, and my understanding of the design process. I have also shown that publication is not the final step; maintenance and regular engagement analysis is also important.

## unity-rpg

Last but not least, is my final project for CS50x. This project was made, as the name indicates, in the Unity game engine. It is a 2D RPG that teaches players how to program in Python. The code was written in C#, which was completely new to me at the time of development. However, at the end of the project, I had gained a better understanding of the language. I believe this project shows both my passion for programming, and my ability to acquire new programming languages quickly.
